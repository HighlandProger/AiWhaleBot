package ru.rusguardian.service.translate.dto.get_translation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GetTranslationRequestDto {

    private String targetLanguageCode;
    private String format;
    private List<String> texts;
    private String folderId;
}
