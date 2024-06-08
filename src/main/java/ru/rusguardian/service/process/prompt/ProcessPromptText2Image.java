package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AIImageService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.constant.Provider;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageResponseDto;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.image.StableDiffusionTextToImageRequestDto;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAIRequestUpdate;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPromptText2Image {

    private final AIImageService aiImageService;
    private final ProcessTransactionalAIRequestUpdate transactionalAIRequestUpdate;
    @Value("${stable-diffusion.image-url-pattern}")
    private String stableDiffusionImageUrlPattern1;
    @Value("${stable-diffusion.image-url-pattern-2}")
    private String stableDiffusionImageUrlPattern2;

    @Async
    public CompletableFuture<String> processUrl(Chat chat, AIModel model, String prompt) {
        return getImageUrl(chat, model, prompt).thenApply(url -> {
            transactionalAIRequestUpdate.update(chat, model);
            return url;
        }).exceptionally(e -> {throw new RuntimeException(e);});
    }

    private CompletableFuture<String> getImageUrl(Chat chat, AIModel model, String prompt) {
        Provider provider = model.getProvider();
        if (provider == Provider.STABLE_DIFFUSION) {
            return getStableDiffusionImageUrl(prompt);
        }
        if (provider == Provider.MIDJOURNEY) {
            return getMidjourneyImageUrl(prompt)
                    .thenApply(urlString -> {
                        if (urlString.isEmpty()) {
                            return getOpenAiImageUrl(chat, AIModel.DALL_E_3, prompt).join();
                        }
                        return urlString;
                    }).exceptionally(e -> {throw new RuntimeException(e);});
        }
        if (provider == Provider.OPEN_AI) {
            return getOpenAiImageUrl(chat, model, prompt);
        }
        throw new RuntimeException("UNKNOWN IMAGE PROVIDER " + model.getProvider());
    }

    CompletableFuture<String> getStableDiffusionImageUrl(String prompt) {
        return aiImageService.getStableDiffusionImageUrl(getStableDiffusionRequestDto(prompt))
                .thenCompose(response -> {
                    log.info(response.toString());
                    if (response.getStatus().equals("processing")) {return getImageUrlFromStableDiffusionIn2Minutes(response.getMeta().getFilePrefix());}
                    return CompletableFuture.completedFuture(response.getOutput().get(0));
                }).exceptionally(e -> {throw new RuntimeException(e);});
    }

    private CompletableFuture<String> getImageUrlFromStableDiffusionIn2Minutes(String filePrefix){
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10000); // Ждем 10 секунд
                try {
                    //TODO refactor
                    URL url1 = new URL(MessageFormat.format(stableDiffusionImageUrlPattern1, filePrefix));
                    log.info(url1.toString());
                    return url1.toString();
                } catch (MalformedURLException e) {
                    log.warn("URL 1 not valid");
                    return MessageFormat.format(stableDiffusionImageUrlPattern2, filePrefix);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Восстанавливаем статус прерывания
                throw new RuntimeException("Image retrieval was interrupted");
            }
        });
    }

    CompletableFuture<String> getMidjourneyImageUrl(String prompt) {
        return aiImageService.getMidjourneyImageUrl(getMidjourneyRequestDto(prompt))
                .thenApply(MidjourneyTextToImageResponseDto::getUrl);
    }

    CompletableFuture<String> getOpenAiImageUrl(Chat chat, AIModel model, String prompt) {
        return aiImageService.getOpenAiImageUrl(getOpenAiRequestDto(chat, model, prompt))
                .thenApply(response -> response.getData().get(0).getUrl());
    }

    private StableDiffusionTextToImageRequestDto getStableDiffusionRequestDto(String prompt) {
        StableDiffusionTextToImageRequestDto dto = new StableDiffusionTextToImageRequestDto();
        dto.setPrompt(prompt);
        return dto;
    }

    private OpenAiTextToImageRequestDto getOpenAiRequestDto(Chat chat, AIModel model, String prompt) {
        OpenAiTextToImageRequestDto dto = new OpenAiTextToImageRequestDto();
        dto.setUser(String.valueOf(chat.getId()));
        dto.setModel(model.getModelName());
        dto.setPrompt(prompt);
        return dto;
    }

    private MidjourneyTextToImageRequestDto getMidjourneyRequestDto(String prompt) {
        MidjourneyTextToImageRequestDto dto = new MidjourneyTextToImageRequestDto();
        dto.setImagine(prompt);
        return dto;
    }

}
