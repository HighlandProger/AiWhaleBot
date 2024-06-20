package ru.rusguardian.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.anthropic.AnthropicTextRequestDto;
import ru.rusguardian.service.ai.dto.anthropic.AnthropicTextResponseDto;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextResponseDto;
import ru.rusguardian.service.ai.exception.AnthropicRequestException;
import ru.rusguardian.service.ai.exception.OpenAiRequestException;
import ru.rusguardian.util.WebExceptionMessageUtil;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AITextService {

    private static final String OPEN_AI_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String ANTHROPIC_MESSAGES_URL = "https://api.anthropic.com/v1/messages";

    private final WebClient openAITextWebClient;
    private final WebClient anthropicWebClient;

    public AITextService(@Qualifier("openAITextWebClient") WebClient openAITextWebClient,
                         @Qualifier("anthropicWebClient") WebClient anthropicWebClient) {
        this.openAITextWebClient = openAITextWebClient;
        this.anthropicWebClient = anthropicWebClient;
    }

    @Async
    public CompletableFuture<OpenAiTextResponseDto> getText(OpenAiTextRequestDto dto) {

        return openAITextWebClient.post()
                .uri(OPEN_AI_COMPLETIONS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(OpenAiTextResponseDto.class)
                .toFuture()
                .exceptionally(e -> {
                    String errorMessage = WebExceptionMessageUtil.getErrorMessage(e);
                    log.error(errorMessage);
                    throw new OpenAiRequestException(errorMessage, e);
                });
    }

    @Async
    public CompletableFuture<AnthropicTextResponseDto> getText(AnthropicTextRequestDto dto) {

        return anthropicWebClient.post()
                .uri(ANTHROPIC_MESSAGES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(AnthropicTextResponseDto.class)
                .toFuture()
                .exceptionally(e -> {
                    String errorMessage = WebExceptionMessageUtil.getErrorMessage(e);
                    log.error(errorMessage);
                    throw new AnthropicRequestException(errorMessage, e);
                });
    }

}
