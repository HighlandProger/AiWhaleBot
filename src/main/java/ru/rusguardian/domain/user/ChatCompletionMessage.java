package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.ai.dto.text.MessageDto;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatCompletionMessage {

    @Column
    private String content;
    @Enumerated(EnumType.STRING)
    private Role role;

    public ChatCompletionMessage(MessageDto dto) {
        this.content = dto.getContent();
        this.role = Role.getByValue(dto.getRole());
    }
}
