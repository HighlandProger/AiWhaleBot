package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.UserDataDto;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.constant.Provider;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.img2video.StableDiffusionImg2VideoRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image.SDModelId;
import ru.rusguardian.service.ai.image.OpenAIImageService;
import ru.rusguardian.service.ai.image.StableDiffusionImageService;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAIImageRequestUpdate;
import ru.rusguardian.service.translate.YandexTranslateService;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessImagePrompt {

    private static final String ANIME_PROMPT = "Make it anime";
    private static final String REMOVE_TEXT_PROMPT = "Remove all texts";
    private static final String CHANGE_BACKGROUND_PROMPT = "Change background to ";
    private static final String IMAGES_DOWNLOAD_PATH = "/app/images/";
    private static final String IMAGES_DOWNLOAD_DIR_URI = "/images/";

    @Value("${server.port}")
    private String localPort;
    @Value("${server.host}")
    private String localHost;

    private final OpenAIImageService openAIImageService;
    private final StableDiffusionImageService stableDiffusionImageService;
    private final ProcessTransactionalAIImageRequestUpdate transactionalAIImageRequestUpdate;
    private final YandexTranslateService yandexTranslateService;

    @Async
    public CompletableFuture<String> processAnimeImageUrl(Chat chat, String telegramImageUrl) {
        return processImageChangeUrl(chat, telegramImageUrl, ANIME_PROMPT);
    }

    @Async
    public CompletableFuture<String> processRemoveTextFromImageUrl(Chat chat, String telegramImageUrl) {
        return processImageChangeUrl(chat, telegramImageUrl, REMOVE_TEXT_PROMPT);
    }

    @Async
    public CompletableFuture<String> processChangeBackgroundImageUrl(Chat chat, String telegramImageUrl, String backgroundPrompt) {
        return processImageChangeUrl(chat, telegramImageUrl, CHANGE_BACKGROUND_PROMPT + backgroundPrompt);
    }

    public CompletableFuture<String> processImageChangeUrl(Chat chat, String telegramImageUrl, String prompt) {
        String initImageUrl = getInitImageUrl(telegramImageUrl);
        return yandexTranslateService.tryTranslate(prompt, AILanguage.ENGLISH)
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
    public CompletableFuture<String> processRemoveBackground(Chat chat, String telegramImageUrl) {
        String initImageUrl = getInitImageUrl(telegramImageUrl);
        return stableDiffusionImageService.getRemoveBackgroundImageUrl(initImageUrl).thenApply(url -> {
                    transactionalAIImageRequestUpdate.update(chat, AIModel.STABLE_DIFFUSION);
                    return url;
                })
                .exceptionally(e -> {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                });
    }

    @Async
    public CompletableFuture<String> processSuperResolution(Chat chat, String telegramImageUrl) {
        String initImageUrl = getInitImageUrl(telegramImageUrl);
        return stableDiffusionImageService.getSuperResolutionUrl(initImageUrl).thenApply(url -> {
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

    @Async
    public CompletableFuture<String> processImg2VideoUrl(Chat chat, UserDataDto dto){
        StableDiffusionImg2VideoRequestDto requestDto = new StableDiffusionImg2VideoRequestDto();
        requestDto.setPrompt(yandexTranslateService.tryTranslate(dto.getPrompt(), AILanguage.ENGLISH).join());
        requestDto.setInitImage(getInitImageUrl(dto.getImageUrl()));
        requestDto.setModelId(dto.getVideoModel().getValue());
        log.info(requestDto.toString());
        return stableDiffusionImageService.getVideoFromImageUrl(requestDto)
                .thenApply(url -> {
                    transactionalAIImageRequestUpdate.update(chat, AIModel.STABLE_DIFFUSION);
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
            case STABLE_DIFFUSION -> yandexTranslateService.tryTranslate(prompt, AILanguage.ENGLISH)
                    .thenCompose(stableDiffusionImageService::getRealtimeTextToImageUrls);
            case OPEN_AI ->
                    openAIImageService.getTextToImageUrl(new OpenAiTextToImageRequestDto(chat.getId(), model, prompt)).thenApply(List::of);
            case MIDJOURNEY -> yandexTranslateService.tryTranslate(prompt, AILanguage.ENGLISH)
                    .thenCompose(resp -> stableDiffusionImageService.getModelTextToImageUrl(resp, SDModelId.CYBERREALISTIC_111));
            default -> throw new RuntimeException("UNKNOWN IMAGE PROVIDER " + model.getProvider());
        };
    }

    private String getInitImageUrl(String telegramImageUrl){
        File file = FileUtils.getFileFromURLInPath(telegramImageUrl, IMAGES_DOWNLOAD_PATH);
        return "http://" + localHost + ":" + localPort + IMAGES_DOWNLOAD_DIR_URI + file.getName();
    }

}
