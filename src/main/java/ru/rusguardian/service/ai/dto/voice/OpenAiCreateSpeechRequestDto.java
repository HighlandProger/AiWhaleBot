package ru.rusguardian.service.ai.dto.voice;

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
    //TODO minor functionality
//    @JsonProperty("response_format")
//    private String responseFormat;
//    private Double speed;

}
