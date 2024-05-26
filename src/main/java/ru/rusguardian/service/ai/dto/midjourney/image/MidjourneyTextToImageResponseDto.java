package ru.rusguardian.service.ai.dto.midjourney.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MidjourneyTextToImageResponseDto {

    private boolean status;
    @JsonProperty("limit_exceed")
    private boolean limitExceed;
    private String message;
    private String url;
}
