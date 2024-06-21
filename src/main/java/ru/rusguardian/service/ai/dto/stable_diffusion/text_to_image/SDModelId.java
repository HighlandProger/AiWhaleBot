package ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SDModelId {

    REALISTIC_VISION_V_1_3("realistic-vision-v13"),
    SDXL("sdxl"),
    MIDJOURNEY_V_4("midjourney");

    private final String value;
}
