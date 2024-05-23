package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AITextService;
import ru.rusguardian.service.ai.constant.AIRequestSetting;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.ai.dto.text.MessageDto;
import ru.rusguardian.service.ai.dto.text.OpenAiTextRequestDto;
import ru.rusguardian.service.ai.dto.text.OpenAiTextResponseDto;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAITextRequestUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPromptText {

    private final AITextService aiTextService;
    private final ProcessTransactionalAITextRequestUpdate transactionalAITextRequestUpdate;

    @Async
    public CompletableFuture<OpenAiTextResponseDto> process(Chat chat, String prompt) {

        return aiTextService.getText(getRequestDto(chat, prompt))
                .thenCompose(responseDto -> {
                    transactionalAITextRequestUpdate.update(chat, responseDto);
                    return CompletableFuture.completedFuture(responseDto);
                });
    }

    private OpenAiTextRequestDto getRequestDto(Chat chat, String prompt) {
        OpenAiTextRequestDto dto = new OpenAiTextRequestDto();
        dto.setModel(chat.getAiSettingsEmbedded().getAiActiveModel().getModelName());
        dto.setMaxTokens(getChatMaxTokens(chat));
        dto.setTemperature(chat.getAiSettingsEmbedded().getTemperature());
        dto.setMessages(getChatMessages(chat, prompt));

        return dto;
    }

    private int getChatMaxTokens(Chat chat) {
        return chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize() * AIRequestSetting.TOKEN_SIZE.getValue();
    }

    private List<MessageDto> getChatMessages(Chat chat, String prompt) {
        List<MessageDto> messages = getPreviousChatCompletionMessages(chat);
        MessageDto userMessage = new MessageDto(prompt, Role.USER.getValue());

        if (messages.isEmpty()) {
            AssistantRole role = chat.getAiSettingsEmbedded().getAssistantRole();
            messages.add(new MessageDto(role.getPrompt(), Role.SYSTEM.getValue()));
            messages.add(userMessage);
            return messages;
        }

        List<MessageDto> cuttedMessages = getCuttedMessages(chat, messages);
        cuttedMessages.add(userMessage);
        return cuttedMessages;
    }

    private List<MessageDto> getPreviousChatCompletionMessages(Chat chat) {
        if (!chat.getAiSettingsEmbedded().isContextEnabled()) {
            return new ArrayList<>();
        }
        return chat.getMessages().stream().map(MessageDto::new).toList();
    }

    private List<MessageDto> getCuttedMessages(Chat chat, List<MessageDto> messages) {
        int messagesInList = AIRequestSetting.MESSAGES_IN_LIST.getValue() * chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize();
        return new ArrayList<>(Stream.concat(
                messages.stream().limit(1),
                messages.stream().skip(Math.max(0, messages.size() - messagesInList))
        ).toList());
    }
}
