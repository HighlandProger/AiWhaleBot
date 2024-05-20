package ru.rusguardian.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

public class ChoosePurchaseCountKeyboardUtil {

    private ChoosePurchaseCountKeyboardUtil() {
    }


    public static InlineKeyboardMarkup getKeyboard(AIModel.BalanceType balanceType) {
        List<List<InlineKeyboardButton>> buttons = SeparatePurchase.getByBalanceType(balanceType).stream().map(purchase -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(getButtonText(purchase));
            button.setCallbackData(getCallback(purchase));
            return List.of(button);
        }).toList();
        buttons.add(List.of(InlineKeyboardButton.builder().text(BACK.getViewName()).callbackData(BUY_SEPARATE_BLIND.getBlindName()).build()));
        return ReplyMarkupUtil.getInlineKeyboardByButtons(buttons);
    }

    private static String getButtonText(SeparatePurchase separatePurchase) {
        switch (separatePurchase.getBalanceType()) {
            case GPT_4:
            case IMAGE: {
                return separatePurchase.getCount() + " запросов - $" + separatePurchase.getPrice();
            }
            case MUSIC: {
                return separatePurchase.getCount() + " песен - $" + separatePurchase.getPrice();
            }
            case CLAUDE: {
                return separatePurchase.getCount() / 1000000 + " млн токенов - $" + separatePurchase.getPrice();
            }
            default:
                throw new RuntimeException("Not supported balance type");
        }
    }

    private static String getCallback(SeparatePurchase separatePurchase) {
        return TelegramCallbackUtils.getCallbackWithArgs(CHS_PURCH_TYPE_BLIND_D.getBlindName(), separatePurchase.name());
    }
}
