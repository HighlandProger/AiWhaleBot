package ru.rusguardian.bot.command.prompts.image.img.inner;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.image.img.inner.service.ImageUrlDtoService;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
public class GPT4VisionForImageCommand extends Command {

    private static final String VIEW_DATA = "GPT4_VISION_FOR_IMAGE";

    @Override
    public CommandName getType() {
        return CommandName.GPT_4_VISION_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        String initImageUrl = FileUtils.getFileUrlFromMessage(((Message) update.getCallbackQuery().getMessage()).getReplyToMessage(), bot).toString();

        setNextCommand(update, CommandName.GPT_4_VISION_FOR_IMAGE_EXECUTE);
        editMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, getChatLanguage(update)), null);
        ImageUrlDtoService.addImageUrl(TelegramUtils.getChatId(update), initImageUrl);
    }
}
