package ru.rusguardian.service.ai.dto.common;


import lombok.Data;
import ru.rusguardian.service.ai.constant.AIModel;


@Data
public class AiResponseCommonDto {

    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private AIModel model;
    private String aiResponse;
}
