package ru.rusguardian.bot.command.main.subscription.buy_separate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Component
@RequiredArgsConstructor
public class BuySeparateBlindCommand extends Command {

    private static final String VIEW_DATA = "BUY_SEPARATE";
    private static final String BUTTONS_VIEW_DATA = "BUY_SEPARATE";

    @Override
    public CommandName getType() {
        return CommandName.BUY_SEPARATE_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChatOwner(update);
        AILanguage language = chat.getAiSettingsEmbedded().getAiLanguage();
        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextByViewDataAndChatLanguage(VIEW_DATA, language));
        edit.setReplyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons(language)));
        edit.setParseMode(ParseMode.MARKDOWN);

        bot.executeAsync(edit);
    }

    private String[][][] getButtons(AILanguage language) {
        List<String> buttonsView = buttonViewDataService.getByNameAndLanguage(BUTTONS_VIEW_DATA, language);
        return new String[][][]{
                {{buttonsView.get(0), BUY_GPT_4_BLIND.getBlindName()}},
                {{buttonsView.get(1), BUY_IMAGE_BLIND.getBlindName()}},
                {{buttonsView.get(2), BUY_CLAUDE_BLIND.getBlindName()}},
                {{buttonsView.get(3), SUBSCRIPTION.getBlindName()}}

        };
    }
}
