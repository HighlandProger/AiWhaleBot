package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AIVoiceService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.text.OpenAiTextResponseDto;
import ru.rusguardian.service.ai.dto.voice.OpenAiTranscriptionRequestDto;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProcessPromptVoice {

    private final AIVoiceService aiVoiceService;
    private final ProcessPromptText processPromptText;

    public CompletableFuture<OpenAiTextResponseDto> processTextResponse(Chat chat, File voiceFile) {
        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();

        return aiVoiceService.getSpeechToText(getRequestDto(settings, voiceFile))
                .thenCompose(resp -> processPromptText.process(chat, resp.getText()));
    }

    private OpenAiTranscriptionRequestDto getRequestDto(AISettingsEmbedded settings, File voiceFile) {
        return OpenAiTranscriptionRequestDto.builder()
                .file(voiceFile)
                .language(settings.getAiLanguage().getValue())
                .temperature(settings.getTemperature())
                .model(AIModel.WHISPER.getModelName())
                .build();
    }
}
