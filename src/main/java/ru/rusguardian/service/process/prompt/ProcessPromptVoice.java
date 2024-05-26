package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AIVoiceService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiTranscriptionRequestDto;
import ru.rusguardian.service.data.AIUserRequestService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProcessPromptVoice {

    private final AIVoiceService aiVoiceService;
    private final ProcessPromptText processPromptText;
    private final AIUserRequestService aiUserRequestService;

    public CompletableFuture<String> processTextResponse(Chat chat, File voiceFile) {
        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();

        return aiVoiceService.getSpeechToText(getRequestDto(settings, voiceFile))
                .thenCompose(resp -> {
                    addAIUserVoiceRequestToDatabase(chat);
                    return processPromptText.process(chat, resp);
                });
    }

    private OpenAiTranscriptionRequestDto getRequestDto(AISettingsEmbedded settings, File voiceFile) {
        return OpenAiTranscriptionRequestDto.builder()
                .file(voiceFile)
                .language(settings.getAiLanguage().getValue())
                .temperature(settings.getTemperature())
                .model(AIModel.WHISPER.getModelName())
                .build();
    }

    private void addAIUserVoiceRequestToDatabase(Chat chat) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.now());
        request.setChat(chat);
        request.setAiModel(AIModel.WHISPER);

        aiUserRequestService.save(request);
    }
}
