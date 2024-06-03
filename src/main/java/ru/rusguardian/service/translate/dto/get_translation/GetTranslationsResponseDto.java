package ru.rusguardian.service.translate.dto.get_translation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GetTranslationsResponseDto {

    private List<Translation> translations;

    @NoArgsConstructor
    @Data
    public static class Translation {
        private String text;
        public String detectedLanguageCode;
    }
}
