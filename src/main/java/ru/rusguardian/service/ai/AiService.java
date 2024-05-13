//package ru.rusguardian.service.ai;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.openai.api.OpenAiApi;
//import org.springframework.ai.openai.api.OpenAiAudioApi;
//import org.springframework.stereotype.Service;
//import ru.rusguardian.constant.ai.AILanguage;
//import ru.rusguardian.constant.ai.AIModel;
//import ru.rusguardian.constant.ai.speech.AiSpeechModel;
//import ru.rusguardian.telegram.bot.util.util.FileUtils;
//
//import java.io.File;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AiService {
//
//    private final OpenAiApi api;
//    private final OpenAiAudioApi audioApi;
//
//    public OpenAiApi.ChatCompletion getTextPromptResponse(List<OpenAiApi.ChatCompletionMessage> messages, AIModel aiModel, float temperature) {
//
////        OpenAiApi.ChatCompletionMessage message = new OpenAiApi.ChatCompletionMessage(prompt, role);
//        OpenAiApi.ChatCompletionRequest request = new OpenAiApi.ChatCompletionRequest(messages, aiModel.getModelName(), temperature);
//        return api.chatCompletionEntity(request).getBody();
//    }
//
//    public String getVoiceTranscription(File file, String language, float temperature){
//
//        OpenAiAudioApi.TranscriptionRequest request = OpenAiAudioApi.TranscriptionRequest.builder()
//                .withFile(FileUtils.getBytes(file))
//                .withLanguage(language)
//                .withModel(AiSpeechModel.WHISPER_1)
//                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
//                .withTemperature(temperature)
//                .build();
//
//        return audioApi.createTranscription(request, String.class).getBody();
//    }
//}
