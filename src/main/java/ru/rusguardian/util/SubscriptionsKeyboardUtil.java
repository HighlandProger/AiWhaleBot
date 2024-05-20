package ru.rusguardian.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.rusguardian.bot.command.main.subscription.Type;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.telegram.bot.util.constants.Callback;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.Collections;
import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

public class SubscriptionsKeyboardUtil {

    private SubscriptionsKeyboardUtil() {
    }

    public static InlineKeyboardMarkup getKeyboard(Type currentType, List<SubscriptionInfo> subscriptionInfos) {
        InlineKeyboardMarkup head = getChangeTypeButtons(currentType);
        InlineKeyboardMarkup subscriptions = getSubscriptionButtons(subscriptionInfos);
        InlineKeyboardMarkup separately = getSeparatelyBuyButtons();

        return ReplyMarkupUtil.getMergedKeyboard(head, subscriptions, separately);
    }

    private static InlineKeyboardMarkup getChangeTypeButtons(Type currentType) {
        String currentSmile = "\uD83D\uDFE2";
        String month = Type.MONTH == currentType ? currentSmile + Type.MONTH.getRuFull() : Type.MONTH.getRuFull();
        String monthBlind = Type.MONTH == currentType ? EMPTY.name() : String.join(Callback.ARGS_DELIMITER.getValue(), SUBSCRIPTION_BLIND_D.getBlindName(), Type.MONTH.name());
        String year = Type.YEAR == currentType ? currentSmile + Type.YEAR.getRuFull() : Type.YEAR.getRuFull();
        String yearBlind = Type.YEAR == currentType ? EMPTY.name() : String.join(Callback.ARGS_DELIMITER.getValue(), SUBSCRIPTION_BLIND_D.getBlindName(), Type.YEAR.name());

        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{month, monthBlind}, {year, yearBlind}}
        });
    }

    private static InlineKeyboardMarkup getSubscriptionButtons(List<SubscriptionInfo> subscriptionInfos) {
        Collections.reverse(subscriptionInfos);
        return new InlineKeyboardMarkup(subscriptionInfos.stream()
                .distinct()
                .map(el -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(getSubscriptionInfoString(el));
                    button.setCallbackData(getBuySubscriptionCommand(el));
                    return List.of(button);
                }).toList());
    }

    private static InlineKeyboardMarkup getSeparatelyBuyButtons() {
        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{BUY_SEPARATE_BLIND.getViewName(), BUY_SEPARATE_BLIND.getBlindName()}},
                {{BUY_CLAUDE_BLIND.getViewName(), BUY_CLAUDE_BLIND.getBlindName()}},
                {{BUY_SUNO_BLIND.getViewName(), BUY_SUNO_BLIND.getBlindName()}},
        });
    }

    //TODO
    private static String getBuySubscriptionCommand(SubscriptionInfo info) {
        return CommandName.EMPTY.getBlindName();
    }

    //TODO refactor
    private static String getSubscriptionInfoString(SubscriptionInfo info) {
        return String.join(" ", info.getSmile(), "Купить", info.getName(), "за", getPriceByType(info));
    }

    private static String getPriceByType(SubscriptionInfo info) {
        return "$" + info.getPrice() + "/" + info.getType().getTimeType().getRuShort();
    }
}
