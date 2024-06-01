package ru.rusguardian.constant.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AITemperature {

    HIGHEST(1.8),
    HIGH(1.4),
    MIDDLE(1.0),
    LOW(0.6),
    LOWEST(0.2);

    private final double value;

}
