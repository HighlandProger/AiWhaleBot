package ru.rusguardian.constant.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AITemperature {

    HIGHEST(1.6),
    HIGH(1.3),
    MIDDLE(1.0),
    LOW(0.7),
    LOWEST(0.4);

    private final double value;

}
