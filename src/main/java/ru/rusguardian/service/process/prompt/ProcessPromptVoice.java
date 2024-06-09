package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AIVoiceService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.constant.OpenAiVoiceType;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiCreateSpeechRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiTranscriptionRequestDto;
import ru.rusguardian.service.data.AIUserRequestService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProcessPromptVoice {

    private final AIVoiceService aiVoiceService;
    private final AIUserRequestService aiUserRequestService;

    public CompletableFuture<String> processVoice2Text(Chat chat, File voiceFile) {
        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();

        return aiVoiceService.getSpeechToText(getRequestDto(settings, voiceFile))
                .thenApply(resp -> {
                    addAIUserVoice2TextRequestToDatabase(chat);
                    return resp;
                });
    }

    public CompletableFuture<File> processText2Voice(Chat chat, String text){
        AIModel model = AIModel.TTS;
        return aiVoiceService.getTextToSpeech(getText2VoiceRequestDto(model, text)).thenApply(file ->{
            addAIUserText2VoiceRequestToDatabase(chat, model);
            return file;
        }).exceptionally(e -> {throw new RuntimeException(e);});
    }

    private OpenAiCreateSpeechRequestDto getText2VoiceRequestDto(AIModel model, String text){
        OpenAiCreateSpeechRequestDto dto = new OpenAiCreateSpeechRequestDto();
        //TODO middle functionality get from AISettings
        dto.setVoice(OpenAiVoiceType.SHIMMER.getValue());
        dto.setModel(model.getModelName());
        dto.setInput(text);
        return dto;
    }

    private OpenAiTranscriptionRequestDto getRequestDto(AISettingsEmbedded settings, File voiceFile) {
        return OpenAiTranscriptionRequestDto.builder()
                .file(voiceFile)
                .language(settings.getAiLanguage().getValue())
                .temperature(settings.getTemperature().getValue())
                .model(AIModel.WHISPER.getModelName())
                .build();
    }

    private void addAIUserVoice2TextRequestToDatabase(Chat chat) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.now());
        request.setChat(chat);
        request.setAiModel(AIModel.WHISPER);

        aiUserRequestService.save(request);
    }

    private void addAIUserText2VoiceRequestToDatabase(Chat chat, AIModel model) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.now());
        request.setChat(chat);
        request.setAiModel(model);

        aiUserRequestService.save(request);
    }
}
