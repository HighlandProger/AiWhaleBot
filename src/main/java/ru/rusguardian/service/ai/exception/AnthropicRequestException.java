package ru.rusguardian.service.ai.exception;

public class AnthropicRequestException extends RuntimeException {
    public AnthropicRequestException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public AnthropicRequestException(Throwable e) {
        super(e);
    }
}
