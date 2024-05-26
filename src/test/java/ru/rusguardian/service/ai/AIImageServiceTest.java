package ru.rusguardian.service.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiImageRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.image.OpenAiImageResponseDto;
import ru.rusguardian.telegram.bot.util.util.telegram_message.InputFileUtil;

import java.util.List;

//@SpringBootTest
class AIImageServiceTest {

    @Autowired
    private AIImageService imageService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TelegramLongPollingBot bot;

    @Value("${open-api.secret-key}")
    private String token;

    @Test
    void getImageUrlFromText() throws InterruptedException {

        imageService.getImageUrl(324L, AIModel.DALL_E_2, "белый котенок").thenAccept(url -> {
            System.out.println("end of request");

            InputFile inputFile = InputFileUtil.getInputFileFromURL(url);
            SendPhoto photo = SendPhoto.builder().photo(inputFile).chatId(366902969L).build();
            try {
                bot.execute(photo);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            System.out.println("message sent");
        }).exceptionally(ex -> {
            throw new RuntimeException(ex.getMessage());
        });

        Thread.sleep(15000);
        System.out.println("hi");
    }

    @Test
    void test() {
        OpenAiImageRequestDto requestDto = new OpenAiImageRequestDto(AIModel.DALL_E_2.getModelName(), "белый котенок", 1, "1024x1024", "1");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("Authorization", List.of(token));
        HttpEntity<OpenAiImageRequestDto> request = new HttpEntity<>(requestDto, map);
        ResponseEntity<OpenAiImageResponseDto> responseDto = restTemplate.postForEntity("https://api.openai.com/v1/images/generations", request, OpenAiImageResponseDto.class);

        System.out.println(responseDto.getBody());
    }
}