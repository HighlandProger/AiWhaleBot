package ru.rusguardian.service.ai.dto.open_ai.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.service.ai.constant.AIModel;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiTextToImageRequestDto {

    public OpenAiTextToImageRequestDto(Long chatId, AIModel model, String prompt) {
        this.user = String.valueOf(chatId);
        this.model = model.getModelName();
        this.prompt = prompt;
    }

    private String model;
    private String prompt;
    private Integer n;
    private String size;
    private String user;
}
