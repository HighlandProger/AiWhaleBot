package ru.rusguardian.service.ai.dto.open_ai.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.domain.ChatCompletionMessage;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessageDto {
    private List<ContentDto> content;
    private String role;

    public RequestMessageDto(ChatCompletionMessage message) {
        this.content = List.of(new ContentDto(ContentDtoType.TEXT.getValue(), message.getMessage(), null));
        this.role = message.getRole().getValue();
    }

    public RequestMessageDto(String textContent, String role) {
        this.content = List.of(new ContentDto(ContentDtoType.TEXT.getValue(), textContent, null));
        this.role = role;
    }
}
