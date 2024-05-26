package ru.rusguardian.service.ai.dto.open_ai.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiCreateSpeechRequestDto {

    private String model;
    private String input;
    private String voice;
    @JsonProperty("response_format")
    private String responseFormat;
    private Double speed;

}
