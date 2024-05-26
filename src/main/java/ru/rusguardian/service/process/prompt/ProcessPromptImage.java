package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AIImageService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAIRequestUpdate;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPromptImage {

    private final AIImageService aiImageService;
    private final ProcessTransactionalAIRequestUpdate transactionalAIRequestUpdate;

    @Async
    public CompletableFuture<String> processUrl(Chat chat, AIModel model, String prompt) {

        return aiImageService.getImageUrl(chat.getId(), model, prompt).thenApply(url -> {
            transactionalAIRequestUpdate.update(chat, model);
            return url;
        });
    }

}
