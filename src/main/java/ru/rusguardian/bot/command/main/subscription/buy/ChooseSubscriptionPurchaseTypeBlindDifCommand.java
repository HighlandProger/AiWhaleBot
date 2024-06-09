package ru.rusguardian.bot.command.main.subscription.buy;

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
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Component
@RequiredArgsConstructor
public class ChooseSubscriptionPurchaseTypeBlindDifCommand extends Command {

    //TODO minor refactor name
    private static final String SUBSCRIPTION_PURCHASING_INFO = "SUBSCRIPTION_PURCHASING_INFO";
    private static final String BUTTON_VIEW_DATA = "CHOOSE_PURCHASE_TYPE";

    @Override
    public CommandName getType() {
        return CHS_SUBS_PURCH_TYPE_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChatOwner(update);
        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextByViewDataAndChatLanguage(SUBSCRIPTION_PURCHASING_INFO, getChatOwner(update).getAiSettingsEmbedded().getAiLanguage()));
        edit.setReplyMarkup(getKeyboard(update, chat.getAiSettingsEmbedded().getAiLanguage()));
        edit(edit);
    }

    private InlineKeyboardMarkup getKeyboard(Update update, AILanguage language) {
        SubscriptionType subscriptionType = SubscriptionType.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));
        List<String> viewButtons = buttonViewDataService.getByNameAndLanguage(BUTTON_VIEW_DATA, language);

        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{viewButtons.get(0), getCallback(PURCH_SUBS_BLIND_D, subscriptionType, PurchaseProvider.ROBOKASSA)}},
                {{viewButtons.get(1), getCallback(PURCH_SUBS_CRYPTO_BLIND_D, subscriptionType, PurchaseProvider.CRYPTOCLOUD)}},
                {{viewButtons.get(2), SUBSCRIPTION.getBlindName()}},
        });
    }

    private String getCallback(CommandName commandName, SubscriptionType subscriptionType, PurchaseProvider provider) {
        return TelegramCallbackUtils.getCallbackWithArgs(commandName.name(), subscriptionType.name(), provider.name());
    }
}
