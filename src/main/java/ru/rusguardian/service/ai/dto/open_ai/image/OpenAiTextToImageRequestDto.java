package ru.rusguardian.service.ai.dto.open_ai.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiTextToImageRequestDto {

    private String model;
    private String prompt;
    private Integer n;
    private String size;
    private String user;
}
