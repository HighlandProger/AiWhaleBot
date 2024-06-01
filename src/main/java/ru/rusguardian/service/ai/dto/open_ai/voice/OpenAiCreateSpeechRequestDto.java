package ru.rusguardian.service.ai.dto.open_ai.voice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiCreateSpeechRequestDto {

    private String model;
    //TODO obtain ",{ symbols
    private String input;
    private String voice;
    @JsonProperty("response_format")
    private String responseFormat;
    private Double speed;

}
