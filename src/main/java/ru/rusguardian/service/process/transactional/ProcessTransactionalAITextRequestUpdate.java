package ru.rusguardian.service.process.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.ChatCompletionMessage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.ai.dto.common.AiResponseCommonDto;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.data.ChatCompletionMessageService;
import ru.rusguardian.service.process.update.ProcessUpdateUserExtraBalance;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessTransactionalAITextRequestUpdate {

    private final AIUserRequestService aiUserRequestService;
    private final ProcessUpdateUserExtraBalance updateChatBalance;
    private final ChatCompletionMessageService chatCompletionMessageService;

    @Transactional
    public void update(Chat chat, String userPrompt, AiResponseCommonDto responseDto) {
        addAIUserRequestToDatabase(chat, responseDto);
        updateCompletionMessages(chat, userPrompt, responseDto);
        updateChatBalance.updateUserTextExtraBalance(chat, responseDto.getModel());
    }

    private void updateCompletionMessages(Chat chat, String userPrompt, AiResponseCommonDto responseDto) {
        ChatCompletionMessage userMessage = new ChatCompletionMessage();
        userMessage.setRole(Role.USER);
        userMessage.setMessage(userPrompt);
        userMessage.setChat(chat);

        ChatCompletionMessage assistantMessage = new ChatCompletionMessage();
        assistantMessage.setChat(chat);
        assistantMessage.setRole(Role.ASSISTANT);
        assistantMessage.setMessage(responseDto.getAiResponse());

        chatCompletionMessageService.saveAll(List.of(userMessage, assistantMessage));
    }

    private void addAIUserRequestToDatabase(Chat chat, AiResponseCommonDto responseDto) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.now());
        request.setChat(chat);
        request.setPromptTokens(responseDto.getPromptTokens());
        request.setCompletionTokens(responseDto.getCompletionTokens());
        request.setTotalTokens(responseDto.getTotalTokens());
        request.setAiModel(responseDto.getModel());

        aiUserRequestService.save(request);
    }
}
