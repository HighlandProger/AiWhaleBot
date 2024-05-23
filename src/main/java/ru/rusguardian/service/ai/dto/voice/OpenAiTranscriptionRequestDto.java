package ru.rusguardian.service.ai.dto.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OpenAiTranscriptionRequestDto {

    private File file;
    private String model;
    private String language;
    private String prompt;
    @JsonProperty("response_format")
    private String responseFormat;
    @DecimalMax("1.0")
    @DecimalMin("0.0")
    private float temperature;
}
