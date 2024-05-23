package ru.rusguardian.service.ai.dto.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpenAiTextRequestDto {

    //TODO minor add all types by documentation
    private List<MessageDto> messages;
    private String model;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    @DecimalMin("0.0")
    @DecimalMax("2.0")
    private float temperature;
    //TODO A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse
//    private String user;

}
