package ru.rusguardian.bot.command.main.subscription.buy_separate.inner.choose_purchase_type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.BUY_SEPARATE_BLIND;
import static ru.rusguardian.bot.command.service.CommandName.PURCH_SEP_BLIND_D;

@Component
@RequiredArgsConstructor
public class ChooseSeparatePurchaseTypeBlindDifCommand extends Command {

    private static final String VIEW_DATA = "CHOOSE_PURCHASE_TYPE";
    private static final String BUTTON_VIEW_DATA = "CHOOSE_PURCHASE_TYPE";

    @Override
    public CommandName getType() {
        return CommandName.CHS_SEP_PURCH_TYPE_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        AILanguage language = getChatLanguage(update);
        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextByViewDataAndChatLanguage(VIEW_DATA, language));
        edit.setReplyMarkup(getKeyboard(update, language));

        bot.executeAsync(edit);
    }

    private InlineKeyboardMarkup getKeyboard(Update update, AILanguage language) {
        SeparatePurchase separatePurchase = SeparatePurchase.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));
        List<String> viewButtons = buttonViewDataService.getByNameAndLanguage(BUTTON_VIEW_DATA, language);
        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{viewButtons.get(0), getCallback(PURCH_SEP_BLIND_D, separatePurchase, PurchaseProvider.ROBOKASSA)}},
                {{viewButtons.get(1), getCallback(PURCH_SEP_BLIND_D, separatePurchase, PurchaseProvider.CRYPTOCLOUD)}},
                {{viewButtons.get(2), BUY_SEPARATE_BLIND.getBlindName()}},
        });
    }

    private String getCallback(CommandName commandName, SeparatePurchase separatePurchase, PurchaseProvider provider) {
        return TelegramCallbackUtils.getCallbackWithArgs(commandName.name(), separatePurchase.name(), provider.name());
    }

}
