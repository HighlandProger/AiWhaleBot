package ru.rusguardian.service.ai.dto.stable_diffusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class StableDiffusionV6ResponseDto {

    private String status;
    private String tip;
    private Double eta;
    private Object message;
    @JsonProperty("fetch_result")
    private String fetchResult;
    private Long id;
    @JsonDeserialize(using = StringToEmptyListDeserializer.class)
    private List<String> output;
    private Object meta;
}
