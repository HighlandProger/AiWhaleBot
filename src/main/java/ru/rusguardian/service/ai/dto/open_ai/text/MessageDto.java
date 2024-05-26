package ru.rusguardian.service.ai.dto.open_ai.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.domain.ChatCompletionMessage;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String content;
    private String role;

    public MessageDto(ChatCompletionMessage message) {
        this.content = message.getMessage();
        this.role = message.getRole().getValue();
    }
}
