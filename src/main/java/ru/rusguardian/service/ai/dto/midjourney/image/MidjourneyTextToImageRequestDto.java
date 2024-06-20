package ru.rusguardian.service.ai.dto.midjourney.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MidjourneyTextToImageRequestDto {

    public MidjourneyTextToImageRequestDto(String prompt) {
        this.imagine = prompt;
    }

    private String key;
    private String secret;
    private String imagine;
}
