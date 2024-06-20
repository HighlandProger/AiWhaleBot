package ru.rusguardian.bot.command.prompts.image.img;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.prompts.image.img.service.ImageRequestInlineKeyboardService;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@RequiredArgsConstructor
@Component
public class ObtainImageRequestCommand extends PromptCommand {

    private final ImageRequestInlineKeyboardService keyboardService;

    private static final String VIEW_DATA = "OBTAIN_IMAGE_REQUEST";

    @Override
    public CommandName getType() {
        return CommandName.OBTAIN_IMAGE_REQUEST;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        if (!(update.hasMessage() && update.getMessage().hasPhoto())) throw new RuntimeException();
        setNullCompletedCommand(update);
        AILanguage language = getChatLanguage(update);

        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtils.getChatIdString(update))
                .text(getTextByViewDataAndChatLanguage(VIEW_DATA, language))
                .replyToMessageId(TelegramUtils.getMessageId(update))
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboardService.getMarkup(language))
                .build();

        sendMessage(message);
    }

}
