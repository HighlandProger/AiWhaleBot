package ru.rusguardian.service.ai.image;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.stable_diffusion.StableDiffusionV6ResponseDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.fetch.StableDiffusionFetchRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.fetch.StableDiffusionFetchResponseDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.pix2pix.StableDiffusionPix2PixRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.remove_background.StableDiffusionRemoveBackgroundRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.super_resolution.StableDiffusionSuperResolutionRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image.SDModelId;
import ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image.StableDiffusionTextToImageRequestDto;
import ru.rusguardian.service.ai.exception.StableDiffusionRequestException;
import ru.rusguardian.util.WebExceptionMessageUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@Slf4j
public class StableDiffusionImageService {

    private static final String TEXT_2_IMG_URL = "https://modelslab.com/api/v6/images/text2img";
    private static final String PIX_2_PIX_URL = "https://modelslab.com/api/v6/image_editing/pix2pix";
    private static final String REMOVE_BACKGROUND_URL = "https://modelslab.com/api/v6/image_editing/removebg_mask";
    private static final String SUPER_RESOLUTION_URL = "https://modelslab.com/api/v6/image_editing/super_resolution";
    private static final String PROCESSING_STATUS = "processing";
    private static final String ERROR_STATUS = "error";

    @Resource
    @Lazy
    private StableDiffusionImageService thisService;

    private final WebClient stableDiffusionWebClient;
    @Value("${stable-diffusion.key}")
    private String stableDiffusionKey;
    @Value("${stable-diffusion.image-url-pattern}")
    private String stableDiffusionImageUrlPattern1;
    @Value("${stable-diffusion.image-url-pattern-2}")
    private String stableDiffusionImageUrlPattern2;

    public StableDiffusionImageService(@Qualifier("stablediffusionWebClient") WebClient stableDiffusionWebClient) {
        this.stableDiffusionWebClient = stableDiffusionWebClient;
    }

    @Async
    public CompletableFuture<String> getPix2PixImageUrl(String initImageUrl, String prompt) {
        return thisService.getImageUrl(PIX_2_PIX_URL, new StableDiffusionPix2PixRequestDto(stableDiffusionKey, initImageUrl, prompt));
    }

    @Async
    public CompletableFuture<String> getRemoveBackgroundImageUrl(String imageUrl) {
        return thisService.getImageUrl(REMOVE_BACKGROUND_URL, new StableDiffusionRemoveBackgroundRequestDto(stableDiffusionKey, imageUrl));
    }

    @Async
    public CompletableFuture<String> getSuperResolutionUrl(String imageUrl) {
        return thisService.getImageUrl(SUPER_RESOLUTION_URL, new StableDiffusionSuperResolutionRequestDto(stableDiffusionKey, imageUrl));
    }

    @Async
    public CompletableFuture<String> getTextToImageUrl(String prompt, SDModelId modelId) {
        return thisService.getImageUrl(TEXT_2_IMG_URL, new StableDiffusionTextToImageRequestDto(stableDiffusionKey, prompt, modelId));
    }

    @Async
    public CompletableFuture<String> getImageUrl(String uri, Object dto) {
        return stableDiffusionWebClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(StableDiffusionV6ResponseDto.class)
                .toFuture()
                .thenCompose(response -> {
                    log.info(response.toString());
                    String status = response.getStatus();
                    if (ERROR_STATUS.equals(status)) {
                        log.error("ERROR DURING SD REQUEST");
                        throw new RuntimeException(response.toString());
                    }
                    if (PROCESSING_STATUS.equals(status)) {
                        return retryFetching(response.getFetchResult(), 10);
                    }
                    List<String> outputList = response.getOutput();
                    if (outputList == null || outputList.isEmpty()) {
                        throw new IllegalArgumentException("Output is null or empty");
                    }
                    return CompletableFuture.completedFuture(outputList.get(0));
                })
                .exceptionally(e -> {
                    String errorMessage = WebExceptionMessageUtil.getErrorMessage(e);
                    log.error(errorMessage);
                    throw new StableDiffusionRequestException(errorMessage, e);
                });
    }

    private CompletableFuture<String> retryFetching(String fetchUrl, int retriesLeft) {
        if (retriesLeft <= 0) {
            throw new StableDiffusionRequestException("Exceeded maximum retries");
        }
        return getFetch(fetchUrl).thenCompose(result -> {
            if (result != null) {
                return CompletableFuture.completedFuture(result);
            } else {
                return CompletableFuture.supplyAsync(() -> {
                    log.info("Processing, will retry... " + retriesLeft + " retries left");
                    return retryFetching(fetchUrl, retriesLeft - 1);
                }, CompletableFuture.delayedExecutor(1, TimeUnit.MINUTES)).thenCompose(Function.identity());
            }
        });
    }

    private CompletableFuture<String> getFetch(String fetchUrl) {
        StableDiffusionFetchRequestDto dto = new StableDiffusionFetchRequestDto(stableDiffusionKey);
        return stableDiffusionWebClient.post()
                .uri(fetchUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(StableDiffusionFetchResponseDto.class)
                .toFuture()
                .thenCompose(response -> {
                    log.info(response.toString());
                    String status = response.getStatus();
                    if (ERROR_STATUS.equals(status)) {
                        log.error("ERROR DURING SD REQUEST");
                        throw new RuntimeException(response.getMessage());
                    }
                    if (PROCESSING_STATUS.equals(status)) {
                        return CompletableFuture.completedFuture(null);
                    }
                    List<String> outputList = response.getOutput();
                    if (outputList == null || outputList.isEmpty()) {
                        throw new IllegalArgumentException("Output is null or empty");
                    }
                    return CompletableFuture.completedFuture(outputList.get(0));
                })
                .exceptionally(e -> {
                    String errorMessage = WebExceptionMessageUtil.getErrorMessage(e);
                    log.error(errorMessage);
                    throw new StableDiffusionRequestException(errorMessage, e);
                });
    }

}
