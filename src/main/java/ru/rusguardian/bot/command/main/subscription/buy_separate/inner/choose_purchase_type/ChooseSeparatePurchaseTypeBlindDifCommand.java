package ru.rusguardian.bot.command.main.subscription.buy_separate.inner.choose_purchase_type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Component
@RequiredArgsConstructor
public class ChooseSeparatePurchaseTypeBlindDifCommand extends Command {

    private static final String CHOOSE_PURCHASE_TYPE = "CHOOSE_PURCHASE_TYPE";

    @Override
    public CommandName getType() {
        return CommandName.CHS_SEP_PURCH_TYPE_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextByViewDataAndChatLanguage(CHOOSE_PURCHASE_TYPE, getChat(update).getAiSettingsEmbedded().getAiLanguage()));
        edit.setReplyMarkup(getKeyboard(update));

        bot.executeAsync(edit);
    }

    private InlineKeyboardMarkup getKeyboard(Update update) {
        SeparatePurchase separatePurchase = SeparatePurchase.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));

        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{PURCH_SEP_RUS_BLIND_D.getViewName(), getCallback(PURCH_SEP_RUS_BLIND_D, separatePurchase)}},
//                {{PURCH_SEP_INTERN_BLIND_D.getViewName(), getCallback(PURCH_SEP_INTERN_BLIND_D, separatePurchase)}},
                {{PURCH_SEP_CRYPTO_BLIND_D.getViewName(), getCallback(PURCH_SEP_CRYPTO_BLIND_D, separatePurchase)}},
                {{BACK.getViewName(), BUY_SEPARATE_BLIND.getBlindName()}},
        });
    }

    private String getCallback(CommandName commandName, SeparatePurchase separatePurchase) {
        return TelegramCallbackUtils.getCallbackWithArgs(commandName.name(), separatePurchase.name());
    }

}
