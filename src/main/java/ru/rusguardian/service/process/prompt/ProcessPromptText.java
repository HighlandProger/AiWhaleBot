package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.ChatCompletionMessage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AITextService;
import ru.rusguardian.service.ai.constant.AIRequestSetting;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.ai.dto.open_ai.text.MessageDto;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextRequestDto;
import ru.rusguardian.service.data.ChatCompletionMessageService;
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
    private final ChatCompletionMessageService chatCompletionMessageService;

    @Async
    public CompletableFuture<String> process(Chat chat, String prompt) {

        OpenAiTextRequestDto requestDto = getRequestDto(chat, prompt);
        return aiTextService.getText(requestDto)
                .thenApply(responseDto -> {
                    transactionalAITextRequestUpdate.update(chat, prompt, responseDto);
                    return responseDto.getChoices().get(0).getMessage().getContent();
                });
    }

    private OpenAiTextRequestDto getRequestDto(Chat chat, String prompt) {
        OpenAiTextRequestDto dto = new OpenAiTextRequestDto();
        dto.setModel(chat.getAiSettingsEmbedded().getAiActiveModel().getModelName());
        dto.setMaxTokens(getChatMaxTokens(chat));
        dto.setTemperature(chat.getAiSettingsEmbedded().getTemperature());
        dto.setMessages(getChatMessages(chat, prompt));
        dto.setUser((String.valueOf(chat.getId())));

        return dto;
    }

    private int getChatMaxTokens(Chat chat) {
        return chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize() * AIRequestSetting.TOKEN_SIZE.getValue();
    }

    private List<MessageDto> getChatMessages(Chat chat, String prompt) {
        List<MessageDto> messages = getPreviousChatCompletionMessages(chat);
        MessageDto userMessage = new MessageDto(prompt, Role.USER.getValue());

        if (messages.isEmpty()) {
            ChatCompletionMessage systemMessage = createSystemChatCompletionMessage(chat);
            messages.add(new MessageDto(systemMessage));
            return messages;
        }

        List<MessageDto> cuttedMessages = getCuttedMessages(chat, messages);
        cuttedMessages.add(userMessage);
        return cuttedMessages;
    }

    private ChatCompletionMessage createSystemChatCompletionMessage(Chat chat) {
        ChatCompletionMessage systemMessage = new ChatCompletionMessage();
        systemMessage.setChat(chat);
        systemMessage.setRole(Role.SYSTEM);
        systemMessage.setMessage(chat.getAiSettingsEmbedded().getAssistantRole().getDescription());
        return chatCompletionMessageService.save(systemMessage);
    }

    private List<MessageDto> getPreviousChatCompletionMessages(Chat chat) {
        if (!chat.getAiSettingsEmbedded().isContextEnabled()) {
            return new ArrayList<>();
        }
        List<ChatCompletionMessage> messages = chatCompletionMessageService.findByChatId(chat.getId());
        return new ArrayList<>(messages.stream().map(MessageDto::new).toList());
    }

    private List<MessageDto> getCuttedMessages(Chat chat, List<MessageDto> messages) {
        int messagesInList = AIRequestSetting.MESSAGES_IN_LIST.getValue() * chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize();
        return new ArrayList<>(Stream.concat(
                messages.stream().limit(1),
                messages.stream().skip(Math.max(0, messages.size() - messagesInList))
        ).toList());
    }
}
