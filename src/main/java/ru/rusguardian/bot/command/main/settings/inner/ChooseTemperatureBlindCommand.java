package ru.rusguardian.bot.command.main.settings.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

@RequiredArgsConstructor
@Service
public class ChooseTemperatureBlindCommand extends Command {

    private static final String FILE_PATH = "text/settings/choose_temperature/";
    private static final String OPERATION_RESTRICTED_FILE_PATH = "text/settings/choose_temperature/operation_restricted/";

    @Override
    public CommandName getType() {
        return CommandName.CHOOSE_TEMPERATURE_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);

        if (chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
            AnswerCallbackQuery callbackQuery = new AnswerCallbackQuery();
            callbackQuery.setText(getTextFromFileByChatLanguage(OPERATION_RESTRICTED_FILE_PATH, chat));
            callbackQuery.setCallbackQueryId(TelegramUtils.getCallbackQueryId(update));
            callbackQuery.setShowAlert(true);
            bot.execute(callbackQuery);
            return;
        }

        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextFromFileByChatLanguage(FILE_PATH, chat));
        edit.setReplyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons()));

        bot.execute(edit);
    }


    private String[][][] getButtons() {
        return new String[][][]{
                {{"Максимально креативный", getCallback("1.8")}},
                {{"Креативный", getCallback("1.4")}},
                {{"Средний", getCallback("1.0")}},
                {{"Мало креативный", getCallback("0.6")}},
                {{"Некреативный", getCallback("0.2")}},
                {{CommandName.BACK.getViewName(), CommandName.SETTINGS_BLIND.getViewName()}}
        };
    }

    private String getCallback(String arg) {
        return TelegramCallbackUtils.getCallbackWithArgs(CommandName.SET_TEMPERATURE_BLIND_D.name(), arg);
    }
}
