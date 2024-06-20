package ru.rusguardian.service.translate;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.service.ai.exception.MidjourneyRequestException;
import ru.rusguardian.service.translate.dto.get_iam_token.GetIAMTokenRequestDto;
import ru.rusguardian.service.translate.dto.get_iam_token.GetIAMTokenResponseDto;
import ru.rusguardian.service.translate.dto.get_translation.GetTranslationRequestDto;
import ru.rusguardian.service.translate.dto.get_translation.GetTranslationsResponseDto;
import ru.rusguardian.util.WebExceptionMessageUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class YandexTranslateService {

    private final WebClient yandexTranslateWebClient;
    private static final String TRANSLATION_URL = "https://translate.api.cloud.yandex.net/translate/v2/translate";
    private static final String IAM_TOKEN_URL = "https://iam.api.cloud.yandex.net/iam/v1/tokens";

    @Value("${yandex.passport-oauth-token}")
    private String yandexPassportOauthToken;
    @Value("${yandex.folder-id}")
    private String yandexFolderId;
    private String IAMToken;

    public YandexTranslateService(@Qualifier("yandexTranslateWebClient") WebClient yandexTranslateWebClient) {
        this.yandexTranslateWebClient = yandexTranslateWebClient;
    }

    @PostConstruct
    private void init() {
        initIAMToken();
    }

    public CompletableFuture<String> getTranslation(String text, AILanguage language) {

        GetTranslationRequestDto dto = new GetTranslationRequestDto();
        dto.setTexts(List.of(text));
        dto.setFormat("HTML");
        dto.setFolderId(yandexFolderId);
        dto.setTargetLanguageCode(language.getValue());

        return yandexTranslateWebClient.post()
                .uri(TRANSLATION_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + IAMToken)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(GetTranslationsResponseDto.class)
                .toFuture()
                .thenApply(response -> response.getTranslations().stream().map(GetTranslationsResponseDto.Translation::getText).collect(Collectors.joining("\n")))
                .exceptionally(e -> {
                    String errorMessage = WebExceptionMessageUtil.getErrorMessage(e);
                    log.error(errorMessage);
                    throw new MidjourneyRequestException(errorMessage, e);
                });
    }

    @Scheduled(fixedRate = 3600000L)
    private void initIAMToken() {
        GetIAMTokenRequestDto dto = new GetIAMTokenRequestDto(yandexPassportOauthToken);
        this.IAMToken = requestIAMToken(dto).join();
    }

    private CompletableFuture<String> requestIAMToken(GetIAMTokenRequestDto dto) {
        return yandexTranslateWebClient.post()
                .uri(IAM_TOKEN_URL)
                .bodyValue(dto)
                .retrieve()
                .toEntity(GetIAMTokenResponseDto.class)
                .toFuture()
                .thenApply(response -> Objects.requireNonNull(response.getBody()).getIamToken());
    }
}
