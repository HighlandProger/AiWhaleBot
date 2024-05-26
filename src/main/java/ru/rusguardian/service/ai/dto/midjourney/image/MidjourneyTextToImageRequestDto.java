package ru.rusguardian.service.ai.dto.midjourney.image;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MidjourneyTextToImageRequestDto {

    private String key;
    private String secret;
    private String imagine;
}
