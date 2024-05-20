package ru.rusguardian.config;

import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableAsync
public class AppConfig {


    @Bean
    public OpenAiApi openAiApi(@Value("${open-api.secret-key}") String apiKey) {
        return new OpenAiApi(apiKey);
    }

    @Bean
    public OpenAiAudioApi openAiAudioApi(@Value("${open-api.secret-key}") String apiKey) {
        return new OpenAiAudioApi(apiKey);
    }

    @Bean
    public OpenAiImageApi openAiImageApi(@Value("${open-api.secret-key}") String apiKey) {
        return new OpenAiImageApi(apiKey);
    }


    @Bean
    public OpenAiChatClient openAiChatClient(OpenAiApi api) {
        return new OpenAiChatClient(api);
    }

    @Bean
    public OpenAiEmbeddingClient openAiEmbeddingClient(OpenAiApi api) {
        return new OpenAiEmbeddingClient(api);
    }

    @Bean
    public OpenAiImageClient openAiImageClient(OpenAiImageApi api) {
        return new OpenAiImageClient(api);
    }

    @Bean
    public OpenAiAudioTranscriptionClient openAiAudioTranscriptionClient(OpenAiAudioApi api) {
        return new OpenAiAudioTranscriptionClient(api);
    }


    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("SCHEDULED");
        return scheduler;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
