package ru.rusguardian.bot.command.main.subscription.buy.inner;

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
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.create.ProcessCreateInvoice;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;

import java.text.MessageFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseSubscriptionByCryptoBlindDifCommand extends Command {

    private static final String SUBSCRIPTION_PURCHASING_INFO = "SUBSCRIPTION_PURCHASING_INFO";
    private final ProcessCreateInvoice createSeparateInvoice;

    @Override
    public CommandName getType() {
        return CommandName.PURCH_SUBS_CRYPTO_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SubscriptionType subscriptionType = SubscriptionType.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));

        EditMessageText edit = EditMessageUtil.getMessageText(update, getText(update, PurchaseProvider.CRYPTOCLOUD));
        edit.setReplyMarkup(getKeyboard(getChat(update), subscriptionType));
        edit.setParseMode(ParseMode.HTML);

        bot.execute(edit);
    }

    private String getText(Update update, PurchaseProvider provider) {
        Chat chat = getChat(update);
        return MessageFormat.format(getTextByViewDataAndChatLanguage(SUBSCRIPTION_PURCHASING_INFO, chat.getAiSettingsEmbedded().getAiLanguage()), provider.getName());
    }

    private InlineKeyboardMarkup getKeyboard(Chat chat, SubscriptionType subscriptionType) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Перейти к оплате");
        button.setUrl(createSeparateInvoice.getCryptocloudInvoiceUrl(chat, subscriptionType).toString());
        keyboard.setKeyboard(List.of(List.of(button)));
        return keyboard;
    }
}
