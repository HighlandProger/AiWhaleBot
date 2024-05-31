package ru.rusguardian.service.ai.exception;

public class OpenAiRequestException extends RuntimeException {
    public OpenAiRequestException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public OpenAiRequestException(Throwable e) {
        super(e);
    }
}
