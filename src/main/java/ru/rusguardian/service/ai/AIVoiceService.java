package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiCreateSpeechRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiTranscriptionRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiTranscriptionResponseDto;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class AIVoiceService {

    private static final String TRANSCRIPTIONS_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String CREATE_SPEECH_URL = "https://api.openai.com/v1/audio/speech";

    @Qualifier("openAIWebClient")
    private final WebClient webClient;

    @Async
    public CompletableFuture<String> getSpeechToText(OpenAiTranscriptionRequestDto requestBody) {

        return webClient.post()
                .uri(TRANSCRIPTIONS_URL)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(getObjectMultiValueMap(requestBody)))
                .retrieve()
                .bodyToMono(OpenAiTranscriptionResponseDto.class)
                .map(e -> e.getText())
                .toFuture();
    }

    private MultiValueMap<String, Object> getObjectMultiValueMap(OpenAiTranscriptionRequestDto requestBody) {
        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("file", new ByteArrayResource(FileUtils.getBytes(requestBody.getFile())) {
            @Override
            public String getFilename() {
                return "audio.webm";
            }
        });
        multipartBody.add("model", requestBody.getModel());
        if (requestBody.getLanguage() != null) {
            multipartBody.add("language", requestBody.getLanguage());
        }
        if (requestBody.getPrompt() != null) {
            multipartBody.add("prompt", requestBody.getPrompt());
        }
        if (requestBody.getResponseFormat() != null) {
            multipartBody.add("response_format", requestBody.getResponseFormat());
        }
        multipartBody.add("temperature", requestBody.getTemperature());
        return multipartBody;
    }

    @Async
    public CompletableFuture<File> getTextToSpeech(OpenAiCreateSpeechRequestDto dto) {

        return webClient.post()
                .uri(CREATE_SPEECH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Resource.class)
                .toFuture().thenApply(res -> {
                    try {
                        //TODO minor functional refactor for response type
                        return FileUtils.getTempFileFromBytes(res.getContentAsByteArray(), "mp3");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
