package ru.rusguardian.bot.command.prompts.image.img.inner;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

@Component
public class HelpForImageCommand extends PromptCommand {

    private static final String VIEW_DATA = "HELP_FOR_IMAGE_REQUEST";

    @Override
    public CommandName getType() {
        return CommandName.HELP_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        AILanguage language = getChatLanguage(update);
        edit(EditMessageText.builder()
                .chatId(TelegramUtils.getChatId(update))
                .text(viewDataService.getViewByNameAndLanguage(VIEW_DATA, language))
                .parseMode(ParseMode.HTML)
                .messageId(TelegramUtils.getMessageId(update))
                .replyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButton(language)))
                .build());

    }

    private String[][][] getButton(AILanguage language) {
        String viewName = buttonViewDataService.getByNameAndLanguage("BACK", language).get(0);
        return new String[][][]{
                {{viewName, CommandName.OBTAIN_IMAGE_REQUEST.getBlindName()}}
        };
    }
}
