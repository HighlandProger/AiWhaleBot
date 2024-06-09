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
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.create.ProcessCreateInvoice;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;

import java.text.MessageFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseSeparateBlindDifCommand extends Command {

    private static final String VIEW_DATA = "SEPARATE_PURCHASE_INFO";
    private static final String BUTTON_VIEW_DATA = "PAY";
    private final ProcessCreateInvoice createSeparateInvoice;

    @Override
    public CommandName getType() {
        return CommandName.PURCH_SEP_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SeparatePurchase separatePurchase = SeparatePurchase.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));
        PurchaseProvider provider = PurchaseProvider.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 2));
        Chat chat = getChatOwner(update);

        EditMessageText edit = EditMessageUtil.getMessageText(update, getText(chat, separatePurchase));
        edit.setReplyMarkup(getKeyboard(chat, separatePurchase, provider));
        edit.setParseMode(ParseMode.HTML);

        edit(edit);
    }

    private String getText(Chat chat, SeparatePurchase separatePurchase) {
        return MessageFormat.format(getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()), separatePurchase.getBalanceType().name());
    }

    private InlineKeyboardMarkup getKeyboard(Chat chat, SeparatePurchase separatePurchase, PurchaseProvider provider) {

        String url = switch (provider) {
            case ROBOKASSA -> createSeparateInvoice.getRobokassaInvoiceUrl(chat, separatePurchase).toString();
            case CRYPTOCLOUD -> createSeparateInvoice.getCryptocloudInvoiceUrl(chat, separatePurchase).toString();
            default -> throw new UnsupportedOperationException(provider.getName());
        };

        String viewButton = buttonViewDataService.getByNameLanguageAndButtonNumber(BUTTON_VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage(), 1);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(viewButton);
        button.setUrl(url);
        keyboard.setKeyboard(List.of(List.of(button)));
        return keyboard;
    }
}
