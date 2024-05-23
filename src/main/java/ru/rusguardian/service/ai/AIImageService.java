package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.image.OpenAiImageRequestDto;
import ru.rusguardian.service.ai.dto.image.OpenAiImageResponseDto;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class AIImageService {

    private static final String GENERATION_URL = "https://api.openai.com/v1/images/generations";

    @Qualifier("openAIWebClien")
    private final WebClient openAIWebClient;

    @Async
    public CompletableFuture<OpenAiImageResponseDto> getImage(OpenAiImageRequestDto dto) {

        return openAIWebClient.post()
                .uri(GENERATION_URL)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(OpenAiImageResponseDto.class)
                .toFuture();
    }

}
