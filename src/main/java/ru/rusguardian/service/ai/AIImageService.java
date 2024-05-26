package ru.rusguardian.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.midjourney.image.MidjourneyTextToImageResponseDto;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiTextToImageResponseDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.image.StableDiffusionTextToImageRequestDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.image.StableDiffusionTextToImageResponseDto;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AIImageService {

    private static final String OPEN_AI_GENERATION_URL = "https://api.openai.com/v1/images/generations";
    private static final String MIDJOURNEY_GENERATION_URL = "https://app.midjourneyapi.net/api/imagine";
    private static final String STABLE_DIFFUSION_GENERATION_URL = "https://modelslab.com/api/v6/realtime/text2img";

    @Autowired
    public AIImageService(@Qualifier("openAIWebClient") WebClient openAIWebClient,
                          @Qualifier("midjourneyWebClient") WebClient midjourneyWebClient,
                          @Qualifier("stablediffusionWebClient") WebClient stablediffusionWebClient) {
        this.openAIWebClient = openAIWebClient;
        this.midjourneyWebClient = midjourneyWebClient;
        this.stablediffusionWebClient = stablediffusionWebClient;
    }

    private final WebClient openAIWebClient;
    private final WebClient midjourneyWebClient;
    private final WebClient stablediffusionWebClient;

    @Value("${midjourney.key}")
    private String midjourneyKey;
    @Value("${midjourney.secret}")
    private String midjourneySecret;
    @Value("${stable-diffusion.key}")
    private String stableDiffusionKey;

    @Async
    public CompletableFuture<OpenAiTextToImageResponseDto> getOpenAiImageUrl(OpenAiTextToImageRequestDto dto) {

        return openAIWebClient.post()
                .uri(OPEN_AI_GENERATION_URL)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(OpenAiTextToImageResponseDto.class)
                .toFuture();
    }

    @Async
    public CompletableFuture<MidjourneyTextToImageResponseDto> getMidjourneyImageUrl(MidjourneyTextToImageRequestDto prompt) {
        MidjourneyTextToImageRequestDto dto = new MidjourneyTextToImageRequestDto();
        dto.setKey(midjourneyKey);
        dto.setSecret(midjourneySecret);

        return midjourneyWebClient.post()
                .uri(MIDJOURNEY_GENERATION_URL)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(MidjourneyTextToImageResponseDto.class)
                .toFuture();
    }

    @Async
    public CompletableFuture<StableDiffusionTextToImageResponseDto> getStableDiffusionImageUrl(StableDiffusionTextToImageRequestDto dto) {
        dto.setKey(stableDiffusionKey);
        return stablediffusionWebClient.post()
                .uri(STABLE_DIFFUSION_GENERATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(StableDiffusionTextToImageResponseDto.class)
                .toFuture();
    }
}
