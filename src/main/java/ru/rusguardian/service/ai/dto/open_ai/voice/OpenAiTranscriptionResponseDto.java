package ru.rusguardian.service.ai.dto.open_ai.voice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OpenAiTranscriptionResponseDto {
    private String text;
}
