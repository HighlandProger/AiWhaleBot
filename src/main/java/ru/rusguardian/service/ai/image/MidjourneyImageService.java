package ru.rusguardian.service.ai.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageResponseDto;
import ru.rusguardian.service.ai.exception.MidjourneyRequestException;
import ru.rusguardian.util.WebExceptionMessageUtil;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MidjourneyImageService {

    private static final String MIDJOURNEY_GENERATION_URL = "https://app.midjourneyapi.net/api/imagine";

    private final WebClient midjourneyWebClient;

    @Autowired
    public MidjourneyImageService(@Qualifier("midjourneyWebClient") WebClient midjourneyWebClient) {
        this.midjourneyWebClient = midjourneyWebClient;
    }

    @Value("${midjourney.key}")
    private String midjourneyKey;
    @Value("${midjourney.secret}")
    private String midjourneySecret;

    @Async
    public CompletableFuture<String> getTextToImageUrl(MidjourneyTextToImageRequestDto dto) {
        dto.setKey(midjourneyKey);
        dto.setSecret(midjourneySecret);

        return midjourneyWebClient.post()
                .uri(MIDJOURNEY_GENERATION_URL)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(MidjourneyTextToImageResponseDto.class)
                .toFuture()
                .thenApply(MidjourneyTextToImageResponseDto::getUrl)
                .exceptionally(e -> {
                    String errorMessage = WebExceptionMessageUtil.getErrorMessage(e);
                    log.error(errorMessage);
                    throw new MidjourneyRequestException(errorMessage, e);
                });
    }

}
