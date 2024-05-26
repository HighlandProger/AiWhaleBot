package ru.rusguardian.service.ai.dto.open_ai.image;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OpenAiImageRequestDto {

    //TODO minor add all types by documentation
    private String model;
    private String prompt;
    private int n;
    private String size;
    private String user;
}
