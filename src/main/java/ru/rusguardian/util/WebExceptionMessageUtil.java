package ru.rusguardian.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiErrorResponseDto;

import java.util.Objects;
import java.util.concurrent.CompletionException;

@Slf4j
public class WebExceptionMessageUtil {

    private WebExceptionMessageUtil() {
    }

    public static String getErrorMessage(Throwable e) {
        if (e instanceof CompletionException ex) {
            e = ex.getCause();
        }
        if (e instanceof WebClientResponseException ex) {
            try {
                return Objects.requireNonNull(ex.getResponseBodyAs(OpenAiErrorResponseDto.class)).getError().getMessage();
            } catch (Exception exc) {
                log.error(exc.getMessage());
            }
        }
        return e.getMessage();
    }
}
