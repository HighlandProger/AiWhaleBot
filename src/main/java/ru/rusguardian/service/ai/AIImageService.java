package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageResponseDto;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiImageRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiImageResponseDto;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class AIImageService {

    private static final String GENERATION_URL = "https://api.openai.com/v1/images/generations";
    private static final String MIDJOURNEY_GENERATION_URL = "https://app.midjourneyapi.net/api/imagine";

    @Qualifier("openAIWebClient")
    private final WebClient openAIWebClient;
    private final WebClient midjourneyWebClient;

    @Value("${midjourney.key}")
    private String midjourneyKey;
    @Value("${midjourney.secret}")
    private String midjourneySecret;

    @Async
    public CompletableFuture<String> getImageUrl(Long chatId, AIModel model, String prompt) {
        if (model.getBalanceType() != AIModel.BalanceType.IMAGE) {
            throw new RuntimeException("UNSUPPORTED BALANCE TYPE " + model);
        }

        return switch (model.getProvider()) {
            case OPEN_AI -> getImageUrl(getOpenAiDto(chatId, model, prompt));
            case MIDJOURNEY -> getImageUrl(prompt).thenApply(url -> {
                if (url.isEmpty()) {
                    return getImageUrl(getOpenAiDto(chatId, AIModel.DALL_E_3, prompt)).join();
                }
                return url;
            });
            default -> throw new RuntimeException("UNKNOWN IMAGE PROVIDER " + model.getProvider());
        };
    }

    private CompletableFuture<String> getImageUrl(OpenAiImageRequestDto dto) {

        return openAIWebClient.post()
                .uri(GENERATION_URL)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(OpenAiImageResponseDto.class)
                .map(e -> e.getData().get(0).getUrl())
                .toFuture();
    }

    private CompletableFuture<String> getImageUrl(String prompt) {
        MidjourneyTextToImageRequestDto dto = new MidjourneyTextToImageRequestDto();
        dto.setKey(midjourneyKey);
        dto.setSecret(midjourneySecret);
        dto.setImagine(prompt);

        return midjourneyWebClient.post()
                .uri(MIDJOURNEY_GENERATION_URL)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(MidjourneyTextToImageResponseDto.class)
                .toFuture()
                .thenApply(resp -> {
                    if (resp.isLimitExceed() || !resp.isStatus()) {
                        log.error("MIDJOURNEY request faiulure. {}", resp);
                    }
                    return resp.getUrl();
                });
    }


    private OpenAiImageRequestDto getOpenAiDto(Long chatId, AIModel model, String prompt) {
        return new OpenAiImageRequestDto(model.getModelName(), prompt, 1, "1024x1024", chatId.toString());
    }
}
