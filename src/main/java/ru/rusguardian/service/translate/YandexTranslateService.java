package ru.rusguardian.service.translate;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.service.translate.dto.get_iam_token.GetIAMTokenRequestDto;
import ru.rusguardian.service.translate.dto.get_iam_token.GetIAMTokenResponseDto;
import ru.rusguardian.service.translate.dto.get_translation.GetTranslationRequestDto;
import ru.rusguardian.service.translate.dto.get_translation.GetTranslationsResponseDto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class YandexTranslateService {

    private final RestClient yandexTranslateRestClient;
    private static final String TRANSLATION_URL = "https://translate.api.cloud.yandex.net/translate/v2/translate";

    @Value("${yandex.passport-oauth-token}")
    private String yandexPassportOauthToken;
    @Value("${yandex.folder-id}")
    private String yandexFolderId;
    private String IAMToken;

    @PostConstruct
    private void init() {
        initIAMToken();
    }

    public String getTranslation(String text, AILanguage language) {

        GetTranslationRequestDto dto = new GetTranslationRequestDto();
        dto.setTexts(List.of(text));
        dto.setFormat("HTML");
        dto.setFolderId(yandexFolderId);
        dto.setTargetLanguageCode(language.getValue());

        return Objects.requireNonNull(yandexTranslateRestClient.post()
                .uri(TRANSLATION_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + IAMToken)
                .body(dto)
                .retrieve()
                .toEntity(GetTranslationsResponseDto.class).getBody()).getTranslations().stream().map(GetTranslationsResponseDto.Translation::getText).collect(Collectors.joining("\n"));
    }

    @Scheduled(fixedDelay = 3600000L)
    private void initIAMToken() {
        GetIAMTokenRequestDto dto = new GetIAMTokenRequestDto(yandexPassportOauthToken);
        this.IAMToken = requestIAMToken(dto);
    }

    private String requestIAMToken(GetIAMTokenRequestDto dto) {
        return Objects.requireNonNull(yandexTranslateRestClient.post()
                .uri("https://iam.api.cloud.yandex.net/iam/v1/tokens")
                .body(dto)
                .retrieve()
                .toEntity(GetIAMTokenResponseDto.class).getBody()).getIamToken();
    }
}
