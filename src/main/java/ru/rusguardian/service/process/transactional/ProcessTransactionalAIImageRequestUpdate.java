package ru.rusguardian.service.process.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.process.update.ProcessUpdateUserExtraBalance;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProcessTransactionalAIImageRequestUpdate {

    private final AIUserRequestService aiUserRequestService;
    private final ProcessUpdateUserExtraBalance updateChatBalance;

    @Transactional
    public void update(Chat chat, AIModel model) {
        addImageAIUserRequestToDatabase(chat, model);
        updateChatBalance.updateImageGenerationBalance(chat, model);
    }

    private void addImageAIUserRequestToDatabase(Chat chat, AIModel model) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.now());
        request.setChat(chat);
        request.setAiModel(model);

        aiUserRequestService.save(request);
    }

}
