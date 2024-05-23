package ru.rusguardian.service.process.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.ChatCompletionMessage;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.text.OpenAiTextResponseDto;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.process.update.ProcessUpdateUserExtraBalance;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProcessTransactionalAITextRequestUpdate {

    private final AIUserRequestService aiUserRequestService;
    private final ProcessUpdateUserExtraBalance updateChatBalance;
    private final ChatService chatService;

    @Transactional
    public void update(Chat chat, OpenAiTextResponseDto responseDto) {
        addAIUserRequestToDatabase(chat, responseDto);

        chat.getMessages().add(new ChatCompletionMessage(responseDto.getChoices().get(0).getMessage()));
        chatService.update(chat);

        updateChatBalance.updateUserTextExtraBalance(chat, AIModel.getByModelName(responseDto.getModel()));
    }

    private void addAIUserRequestToDatabase(Chat chat, OpenAiTextResponseDto responseDto) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.now());
        request.setChat(chat);
        request.setPromptTokens(responseDto.getUsage().getPromptTokens());
        request.setCompletionTokens(responseDto.getUsage().getCompletionTokens());
        request.setTotalTokens(responseDto.getUsage().getTotalTokens());
        //VULNERABILITY if model name unknown
        request.setAiModel(AIModel.getByModelName(responseDto.getModel()));

        aiUserRequestService.save(request);
    }
}
