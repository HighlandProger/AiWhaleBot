package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.text.OpenAiTextRequestDto;
import ru.rusguardian.service.ai.dto.text.OpenAiTextResponseDto;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AITextService {

    private static final String COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";

    @Qualifier("openAIWebClien")
    private final WebClient openAIWebClient;

    @Async
    public CompletableFuture<OpenAiTextResponseDto> getText(OpenAiTextRequestDto dto) {

        return openAIWebClient.post()
                .uri(COMPLETIONS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(OpenAiTextResponseDto.class)
                .toFuture();
    }

}
