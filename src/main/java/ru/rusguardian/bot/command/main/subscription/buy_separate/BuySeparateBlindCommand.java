package ru.rusguardian.bot.command.main.subscription.buy_separate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Component
@RequiredArgsConstructor
public class BuySeparateBlindCommand extends Command {

    private static final String FILE_PATH = "text/subscription/buy_separate/";

    @Override
    public CommandName getType() {
        return CommandName.BUY_SEPARATE_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextFromFileByChatLanguage(FILE_PATH, getChat(update)));
        edit.setReplyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons()));
        edit.setParseMode(ParseMode.MARKDOWN);

        bot.executeAsync(edit);
    }

    private String[][][] getButtons() {
        return new String[][][]{
                {{BUY_GPT_4_BLIND.getViewName(), BUY_GPT_4_BLIND.getBlindName()}},
                {{BUY_IMAGE_BLIND.getViewName(), BUY_IMAGE_BLIND.getBlindName()}},
                {{BUY_CLAUDE_BLIND.getViewName(), BUY_CLAUDE_BLIND.getBlindName()}},
                {{BUY_SUNO_BLIND.getViewName(), BUY_SUNO_BLIND.getBlindName()}},
                {{BACK.getViewName(), SUBSCRIPTION_BLIND_D.getBlindName()}}

        };
    }
}
