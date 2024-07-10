package ru.rusguardian.service.ai.dto.stable_diffusion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class StableDiffusionV6VideoResponseDto {

    private String status;
    private Long id;
    @JsonDeserialize(using = StringToEmptyListDeserializer.class)
    private List<String> output;
}
