package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiErrorResponseDto;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextResponseDto;
import ru.rusguardian.service.ai.exception.OpenAiRequestException;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .toFuture()
                .exceptionally(e -> {
                    String errorMessage = getErrorMessage(e);
                    log.error(errorMessage);
                    throw new OpenAiRequestException(errorMessage, e);
                });
    }

    private String getErrorMessage(Throwable e) {
        if (e instanceof WebClientResponseException ex) {
            try {
                return ex.getResponseBodyAs(OpenAiErrorResponseDto.class).getError().getMessage();
            } catch (Exception exc) {
                log.error(exc.getMessage());
            }
        }
        return e.getMessage();
    }


}
