package ru.rusguardian.service.ai.exception;

public class MidjourneyRequestException extends RuntimeException {
    public MidjourneyRequestException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

    public MidjourneyRequestException(Throwable e) {
        super(e);
    }
}
