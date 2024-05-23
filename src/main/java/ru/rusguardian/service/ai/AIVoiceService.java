package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rusguardian.service.ai.dto.voice.OpenAiTranscriptionRequestDto;
import ru.rusguardian.service.ai.dto.voice.OpenAiTranscriptionResponseDto;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class AIVoiceService {

    private static final String TRANSCRIPTIONS_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String CREATE_SPEECH_URL = "https://api.openai.com/v1/audio/speech";

    private final OpenAiAudioApi audioApi;

    @Qualifier("openAIWebClient")
    private final WebClient webClient;

    @Async
    public CompletableFuture<OpenAiTranscriptionResponseDto> getSpeechToText(OpenAiTranscriptionRequestDto requestBody) {

        MultiValueMap<String, Object> multipartBody = getObjectMultiValueMap(requestBody);

        return webClient.post()
                .uri(TRANSCRIPTIONS_URL)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .retrieve()
                .bodyToMono(OpenAiTranscriptionResponseDto.class)
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

    public File getTextToSpeech(String text) {

        OpenAiAudioApi.SpeechRequest request = OpenAiAudioApi.SpeechRequest.builder()
                .withModel(OpenAiAudioApi.TtsModel.TTS_1.value)
                .withInput(text)
                .withResponseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .build();

        byte[] bytes = audioApi.createSpeech(request).getBody();

        try {
            File tempFile = File.createTempFile("textToSpeech", ".mp3");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);
            fos.close();

            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
