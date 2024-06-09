package ru.rusguardian.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiErrorResponseDto;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiCreateSpeechRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiTranscriptionRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.voice.OpenAiTranscriptionResponseDto;
import ru.rusguardian.service.ai.exception.OpenAiRequestException;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RequiredArgsConstructor
@Service
@Slf4j
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

        String fileFormat = dto.getResponseFormat() == null ? "mp3" : dto.getResponseFormat();
        return webClient
                .post()
                .uri(CREATE_SPEECH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToFlux(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToFlux(DataBuffer.class);
                    } else {
                        return response.createException().flatMapMany(Flux::error);
                    }
                })
                .reduce(FileUtils.getTempFile(fileFormat).toPath(), (path, dataBuffer) -> {
                    try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
                        channel.write(dataBuffer.asByteBuffer());
                        DataBufferUtils.release(dataBuffer);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    return path;
                })
                .map(Path::toFile)
                .toFuture()
                .exceptionally(e -> {
                    String errorMessage = getErrorMessage(e);
                    log.error(errorMessage);
                    throw new OpenAiRequestException(errorMessage, e);
                });
    }

    private String getErrorMessage(Throwable e) {
        if (e instanceof CompletionException ex) {
            e = ex.getCause();
        }
        if (e instanceof WebClientResponseException ex) {
            try {
                return ex.getResponseBodyAs(OpenAiErrorResponseDto.class).getError().getMessage();
            } catch (Exception exc) {
                log.error(exc.getMessage());
            }
        }
        return e.getMessage();
    }
}
