package ru.rusguardian.bot.command.main.subscription.buy_separate.inner.choose_purchase_type.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.create.ProcessCreateInvoice;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;

import java.text.MessageFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseSeparateByRussianCardBlindDifCommand extends Command {

    private static final String SEPARATE_PURCHASE_INFO = "SEPARATE_PURCHASE_INFO";
    private final ProcessCreateInvoice createSeparateInvoice;

    @Override
    public CommandName getType() {
        return CommandName.PURCH_SEP_RUS_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SeparatePurchase separatePurchase = SeparatePurchase.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));
        Chat chat = getChat(update);

        EditMessageText edit = EditMessageUtil.getMessageText(update, getText(chat, separatePurchase));
        edit.setReplyMarkup(getKeyboard(chat, separatePurchase));
        edit.setParseMode(ParseMode.HTML);

        bot.execute(edit);
    }

    private String getText(Chat chat, SeparatePurchase separatePurchase) {
        return MessageFormat.format(getTextByViewDataAndChatLanguage(SEPARATE_PURCHASE_INFO, chat.getAiSettingsEmbedded().getAiLanguage()), separatePurchase.getBalanceType().name());
    }

    private InlineKeyboardMarkup getKeyboard(Chat chat, SeparatePurchase separatePurchase) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Перейти к оплате");
        button.setUrl(createSeparateInvoice.getRobokassaInvoiceUrl(chat, separatePurchase).toString());
        keyboard.setKeyboard(List.of(List.of(button)));
        return keyboard;
    }
}
