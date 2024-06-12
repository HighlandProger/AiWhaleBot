package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AITextService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.constant.Provider;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.ai.dto.anthropic.AnthropicTextRequestDto;
import ru.rusguardian.service.ai.dto.common.AiResponseCommonDto;
import ru.rusguardian.service.ai.dto.common.AiResponseCommonDtoFactory;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.text.RequestMessageDto;
import ru.rusguardian.service.data.AssistantRoleDataService;
import ru.rusguardian.service.data.ChatCompletionMessageService;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAITextRequestUpdate;
import ru.rusguardian.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPromptText {

    private final AITextService aiTextService;
    private final ProcessTransactionalAITextRequestUpdate transactionalAITextRequestUpdate;
    private final ChatCompletionMessageService chatCompletionMessageService;
    private final AssistantRoleDataService assistantRoleDataService;
    private final AiResponseCommonDtoFactory commonDtoFactory;

    @Async
    public CompletableFuture<String> process(Chat chat, String prompt) {
        Provider provider = chat.getAiSettingsEmbedded().getAiActiveModel().getProvider();

        if (provider == Provider.OPEN_AI){
            return aiTextService.getText(getOpenAiRequestDto(chat, prompt))
                    .thenApply(responseDto -> {
                        AiResponseCommonDto commonResponseDto = commonDtoFactory.create(responseDto);
                        transactionalAITextRequestUpdate.update(chat, prompt, commonResponseDto);
                        return commonResponseDto.getAiResponse();
                    }).exceptionally(e -> {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    });
        }
        if (provider == Provider.ANTHROPIC){
            return aiTextService.getText(getAnthropicRequestDto(chat, prompt))
                    .thenApply(responseDto -> {
                        AiResponseCommonDto commonResponseDto = commonDtoFactory.create(responseDto);
                        transactionalAITextRequestUpdate.update(chat, prompt, commonResponseDto);
                        return commonResponseDto.getAiResponse();
                    }).exceptionally(e -> {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    });
        }

        throw new UnsupportedOperationException("Unknown text request provider " + provider.name());
    }

    private AnthropicTextRequestDto getAnthropicRequestDto(Chat chat, String prompt){
        AssistantRoleData assistantRoleData = assistantRoleDataService.getByChat(chat);
        AIModel model = chat.getAiSettingsEmbedded().getAiActiveModel();
        if (model.getBalanceType() != AIModel.BalanceType.CLAUDE) {throw new UnsupportedOperationException("Not CLAUDE balance type of model " + model);}

        AnthropicTextRequestDto dto = new AnthropicTextRequestDto();
        dto.setSystem(assistantRoleData.getDescription());
        dto.setMessages(getAnthropicMessages(chat, prompt));
        dto.setTemperature(chat.getAiSettingsEmbedded().getTemperature().getValue()/2);
        dto.setModel(model.getModelName());
        return dto;
    }

    private List<AnthropicTextRequestDto.Message> getAnthropicMessages(Chat chat, String prompt){
        List<AnthropicTextRequestDto.Message> messages =
                new ArrayList<>(ChatUtil.getPreviousChatCompletionMessages(chat, chatCompletionMessageService))
                        .stream().map(AnthropicTextRequestDto.Message::new).toList();
        messages.add(new AnthropicTextRequestDto.Message(Role.USER, prompt));
        return messages;
    }

    private OpenAiTextRequestDto getOpenAiRequestDto(Chat chat, String prompt) {
        OpenAiTextRequestDto dto = new OpenAiTextRequestDto();
        dto.setModel(chat.getAiSettingsEmbedded().getAiActiveModel().getModelName());
        dto.setMaxTokens(ChatUtil.getChatMaxTokens(chat));
        dto.setTemperature(chat.getAiSettingsEmbedded().getTemperature().getValue());
        dto.setMessages(getChatMessages(chat, prompt));
        dto.setUser((String.valueOf(chat.getId())));

        return dto;
    }

    private List<RequestMessageDto> getChatMessages(Chat chat, String prompt) {
        AssistantRoleData role = assistantRoleDataService.getByChat(chat);
        List<RequestMessageDto> chatMessages = new ArrayList<>();
        chatMessages.addAll(ChatUtil.getLeadingChatCompletionMessages(chat, chatCompletionMessageService, role).stream().map(RequestMessageDto::new).toList());
        chatMessages.add((new RequestMessageDto(prompt, Role.USER.getValue())));

        return chatMessages;
    }

}
