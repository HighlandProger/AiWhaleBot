package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.constant.Provider;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image.SDModelId;
import ru.rusguardian.service.ai.image.MidjourneyImageService;
import ru.rusguardian.service.ai.image.OpenAIImageService;
import ru.rusguardian.service.ai.image.StableDiffusionImageService;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAIImageRequestUpdate;
import ru.rusguardian.service.translate.YandexTranslateService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessImagePrompt {

    private static final String ANIME_PROMPT = "Make it anime";
    private static final String REMOVE_TEXT_PROMPT = "Remove all texts";
    private static final String CHANGE_BACKGROUND_PROMPT = "Change background to ";

    private final MidjourneyImageService midjourneyImageService;
    private final OpenAIImageService openAIImageService;
    private final StableDiffusionImageService stableDiffusionImageService;
    private final ProcessTransactionalAIImageRequestUpdate transactionalAIImageRequestUpdate;
    private final YandexTranslateService yandexTranslateService;

    @Async
    public CompletableFuture<String> processAnimeImageUrl(Chat chat, String initImageUrl) {
        return processImageChangeUrl(chat, initImageUrl, ANIME_PROMPT);
    }

    @Async
    public CompletableFuture<String> processRemoveTextFromImageUrl(Chat chat, String initImageUrl) {
        return processImageChangeUrl(chat, initImageUrl, REMOVE_TEXT_PROMPT);
    }

    @Async
    public CompletableFuture<String> processChangeBackgroundImageUrl(Chat chat, String initImageUrl, String backgroundPrompt) {
        return processImageChangeUrl(chat, initImageUrl, CHANGE_BACKGROUND_PROMPT + backgroundPrompt);
    }

    public CompletableFuture<String> processImageChangeUrl(Chat chat, String initImageUrl, String prompt) {
        return yandexTranslateService.getTranslation(prompt, AILanguage.ENGLISH)
                .thenCompose(resp -> stableDiffusionImageService.getPix2PixImageUrl(initImageUrl, resp)
                        .thenApply(url -> {
                            transactionalAIImageRequestUpdate.update(chat, AIModel.STABLE_DIFFUSION);
                            return url;
                        }).exceptionally(e -> {
                            log.error(e.getMessage());
                            throw new RuntimeException(e);
                        }));
    }

    @Async
    public CompletableFuture<String> processRemoveBackground(Chat chat, String imageUrl) {
        return stableDiffusionImageService.getRemoveBackgroundImageUrl(imageUrl).thenApply(url -> {
                    transactionalAIImageRequestUpdate.update(chat, AIModel.STABLE_DIFFUSION);
                    return url;
                })
                .exceptionally(e -> {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                });
    }

    @Async
    public CompletableFuture<String> processSuperResolution(Chat chat, String imageUrl) {
        return stableDiffusionImageService.getSuperResolutionUrl(imageUrl).thenApply(url -> {
            transactionalAIImageRequestUpdate.update(chat, AIModel.STABLE_DIFFUSION);
            return url;
        }).exceptionally(e -> {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        });
    }

    @Async
    public CompletableFuture<List<String>> processText2ImageUrls(Chat chat, AIModel model, String prompt) {
        return getTextToImageUrls(chat, model, prompt)
                .thenApply(url -> {
                    transactionalAIImageRequestUpdate.update(chat, model);
                    return url;
                })
                .exceptionally(e -> {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                });
    }

    private CompletableFuture<List<String>> getTextToImageUrls(Chat chat, AIModel model, String prompt) {
        Provider provider = model.getProvider();

        return switch (provider) {
            case STABLE_DIFFUSION -> yandexTranslateService.getTranslation(prompt, AILanguage.ENGLISH)
                    .thenCompose(stableDiffusionImageService::getRealtimeTextToImageUrls);
            case OPEN_AI ->
                    openAIImageService.getTextToImageUrl(new OpenAiTextToImageRequestDto(chat.getId(), model, prompt)).thenApply(List::of);
            case MIDJOURNEY -> yandexTranslateService.getTranslation(prompt, AILanguage.ENGLISH)
                    .thenCompose(resp -> stableDiffusionImageService.getModelTextToImageUrl(resp, SDModelId.MIDJOURNEY_V_4));
            default -> throw new RuntimeException("UNKNOWN IMAGE PROVIDER " + model.getProvider());
        };
    }


}
