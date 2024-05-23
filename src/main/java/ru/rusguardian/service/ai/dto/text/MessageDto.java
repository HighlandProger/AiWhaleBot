package ru.rusguardian.service.ai.dto.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.domain.user.ChatCompletionMessage;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String content;
    private String role;

    public MessageDto(ChatCompletionMessage message){
        this.content = message.getContent();
        this.role = message.getRole().getValue();
    }
}
