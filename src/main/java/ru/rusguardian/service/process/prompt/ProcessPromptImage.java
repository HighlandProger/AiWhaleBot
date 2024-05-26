package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPromptImage {

    private final AIImageService aiImageService;
    private final ProcessTransactionalAIRequestUpdate transactionalAIRequestUpdate;

    @Async
    public CompletableFuture<String> processUrl(Chat chat, AIModel model, String prompt) {
        return getImageUrl(chat, model, prompt).thenApply(url -> {
            transactionalAIRequestUpdate.update(chat, model);
            return url;
        });
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
                    });
        }
        if (provider == Provider.OPEN_AI) {
            return getOpenAiImageUrl(chat, model, prompt);
        }
        throw new RuntimeException("UNKNOWN IMAGE PROVIDER " + model.getProvider());
    }

    CompletableFuture<String> getStableDiffusionImageUrl(String prompt) {
        return aiImageService.getStableDiffusionImageUrl(getStableDiffusionRequestDto(prompt))
                .thenApply(response -> {
                    log.info(response.toString());
                    return response.getOutput().get(0);
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
