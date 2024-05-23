package ru.rusguardian.service.ai.dto.image;

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
    //TODO A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse
//    private String user;
}
