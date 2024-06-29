package ru.rusguardian.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("SCHEDULED");
        return scheduler;
    }

    @Bean(name = "openAITextWebClient")
    public WebClient openAIWebClient(@Value("${open-api.secret-key}") String apiKey) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  // Таймаут на подключение
                        .responseTimeout(Duration.ofSeconds(25))  // Таймаут на получение ответа
                ))
                .build();
    }

    @Bean(name = "openAIImageWebClient")
    public WebClient openAIImageWebClient(@Value("${open-api.secret-key}") String apiKey) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  // Таймаут на подключение
                        .responseTimeout(Duration.ofSeconds(60))  // Таймаут на получение ответа
                ))
                .build();
    }

    @Bean(name = "anthropicWebClient")
    public WebClient anthropicWebClient(@Value("${anthropic.x-api-key}") String xApiKey, @Value("${anthropic.version}") String anthropicVersion) {
        return WebClient.builder()
                .defaultHeader("x-api-key", xApiKey)
                .defaultHeader("anthropic-version", anthropicVersion)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  // Таймаут на подключение
                        .responseTimeout(Duration.ofSeconds(25))  // Таймаут на получение ответа
                ))
                .build();
    }

    @Bean(name = "midjourneyWebClient")
    public WebClient midjourneyWebClient() {
        return WebClient.builder()
                .build();
    }

    @Bean(name = "stablediffusionWebClient")
    public WebClient stablediffusionWebClient() {
        return WebClient.builder()
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "yandexTranslateWebClient")
    public WebClient yandexTranslateClient() {
        return WebClient.builder()
                .build();
    }
}
