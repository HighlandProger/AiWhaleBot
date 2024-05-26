package ru.rusguardian.bot.command.main.subscription.buy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Component
@RequiredArgsConstructor
public class ChooseSubscriptionPurchaseTypeBlindDifCommand extends Command {

    //TODO minor refactor name
    private static final String FILE_PATH = "text/subscription/purchase/";

    @Override
    public CommandName getType() {
        return CHS_SUBS_PURCH_TYPE_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextFromFileByChatLanguage(FILE_PATH, getChat(update)));
        edit.setReplyMarkup(getKeyboard(update));
        bot.executeAsync(edit);
    }

    private InlineKeyboardMarkup getKeyboard(Update update) {
        SubscriptionType subscriptionType = SubscriptionType.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));

        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{PURCH_SUBS_RUS_BLIND_D.getViewName(), getCallback(PURCH_SUBS_RUS_BLIND_D, subscriptionType)}},
//                {{PURCH_SUBS_INTERN_BLIND_D.getViewName(), getCallback(PURCH_SUBS_INTERN_BLIND_D, subscriptionType)}},
                {{PURCH_SUBS_CRYPTO_BLIND_D.getViewName(), getCallback(PURCH_SUBS_CRYPTO_BLIND_D, subscriptionType)}},
                {{BACK.getViewName(), SUBSCRIPTION_BLIND_D.getBlindName()}},
        });
    }

    private String getCallback(CommandName commandName, SubscriptionType subscriptionType) {
        return TelegramCallbackUtils.getCallbackWithArgs(commandName.name(), subscriptionType.name());
    }


}
