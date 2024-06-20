package ru.rusguardian.service.ai.exception;

public class StableDiffusionRequestException extends RuntimeException {
    public StableDiffusionRequestException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public StableDiffusionRequestException(Throwable e) {
        super(e);
    }

    public StableDiffusionRequestException(String message) {
        super(message);
    }
}
