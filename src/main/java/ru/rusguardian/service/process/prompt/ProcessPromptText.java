package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AITextService;
import ru.rusguardian.service.ai.constant.Role;
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

    @Async
    public CompletableFuture<String> process(Chat chat, String prompt) {

        OpenAiTextRequestDto requestDto = getRequestDto(chat, prompt);
        return aiTextService.getText(requestDto)
                .thenApply(responseDto -> {
                    transactionalAITextRequestUpdate.update(chat, prompt, responseDto);
                    return responseDto.getChoices().get(0).getMessage().getContent();
                }).exceptionally(e -> {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                });
    }

    private OpenAiTextRequestDto getRequestDto(Chat chat, String prompt) {
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
