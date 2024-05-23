package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AIImageService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.image.OpenAiImageRequestDto;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAIRequestUpdate;
import ru.rusguardian.telegram.bot.util.util.telegram_message.InputFileUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPromptImage {

    private final AIImageService aiImageService;
    private final ProcessTransactionalAIRequestUpdate transactionalAIRequestUpdate;
    private final TelegramLongPollingBot bot;

    @Async
    public void process(Chat chat, AIModel model, String prompt) {

        aiImageService.getImage(getDto(model, prompt)).thenAccept(responseDto -> {
            sendResponseToUser(responseDto.getData().get(0).getUrl(), chat.getId(), model.name(), prompt);
            transactionalAIRequestUpdate.update(chat, model);
        });
    }

    private OpenAiImageRequestDto getDto(AIModel model, String prompt) {
        return new OpenAiImageRequestDto(model.getModelName(), prompt, 1, "1024x1024");
    }


    //TODO minor refactor
    private void sendResponseToUser(String fileUrl, Long chatId, String model, String prompt) {
        InputFile file = InputFileUtil.getInputFileFromURL(fileUrl);
        SendPhoto photo = SendPhoto.builder().photo(file).chatId(chatId).caption(getCaption(model, prompt)).parseMode(ParseMode.MARKDOWN).build();
        try {
            bot.execute(photo);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO minor 1)add language support 2)move to txt file
    private String getCaption(String model, String prompt) {
        return String.format("""
                🖼️ Формат: 1024x1024
                🤖 Модель: %s
                🪄 Стиль: No style
                🧮 Количество изображений: 1
                🏛 Промпт: %s
                                
                [ChatGPT 4.0 | Telegram Bot](https://t.me/ChatGPT_Midjourney_PRO_bot)
                """, model, prompt);
    }
}
