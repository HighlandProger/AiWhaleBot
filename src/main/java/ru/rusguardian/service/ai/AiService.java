package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.speech.AiSpeechModel;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final OpenAiApi api;
    private final OpenAiAudioApi audioApi;
    private final OpenAiImageApi imageApi;

    public OpenAiApi.ChatCompletion getTextPromptResponse(List<OpenAiApi.ChatCompletionMessage> messages, AIModel aiModel, float temperature) {

        OpenAiApi.ChatCompletionRequest request = new OpenAiApi.ChatCompletionRequest(messages, aiModel.getModelName(), temperature);
        return api.chatCompletionEntity(request).getBody();
    }

    public OpenAiApi.ChatCompletion getTextPromptResponse(List<OpenAiApi.ChatCompletionMessage> messages, AIModel model, float temperature, int maxTokens) {
        OpenAiApi.ChatCompletionRequest request = new OpenAiApi.ChatCompletionRequest(messages, model.getModelName(), null, null, maxTokens, null, null, null, null, null, false, temperature, null, null, null, null);
        return api.chatCompletionEntity(request).getBody();
    }

    public String getSpeechToText(File file, String language, float temperature) {

        OpenAiAudioApi.TranscriptionRequest request = OpenAiAudioApi.TranscriptionRequest.builder()
                .withFile(FileUtils.getBytes(file))
                .withLanguage(language)
                .withModel(AiSpeechModel.WHISPER_1)
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withTemperature(temperature)
                .build();

        return audioApi.createTranscription(request, String.class).getBody();
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

    public URL getImageUrlFromText(String text) {

        //TODO add midjourney and stable diffusion
        OpenAiImageApi.OpenAiImageRequest request = new OpenAiImageApi.OpenAiImageRequest(
                text, AIModel.DALL_E_3.getModelName()
        );

        OpenAiImageApi.OpenAiImageResponse response = imageApi.createImage(request).getBody();

        try {
            //TODO NPE
            return new URL(response.data().get(0).url());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
