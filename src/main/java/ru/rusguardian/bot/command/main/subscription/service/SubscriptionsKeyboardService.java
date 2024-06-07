package ru.rusguardian.bot.command.main.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.rusguardian.bot.command.main.subscription.Type;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.ButtonViewData;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.service.data.ButtonViewDataService;
import ru.rusguardian.telegram.bot.util.constants.Callback;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@RequiredArgsConstructor
@Service
public class SubscriptionsKeyboardService {

    private final ButtonViewDataService buttonViewDataService;

    private static final String HEAD_BUTTONS = "SUBSCRIPTION_HEAD";
    private static final String BODY_MONTH_BUTTONS = "SUBSCRIPTION_BODY_MONTH";
    private static final String BODY_YEAR_BUTTONS = "SUBSCRIPTION_BODY_YEAR";
    private static final String SEPARATE_BUTTONS = "SUBSCRIPTION_SEPARATE";

    public InlineKeyboardMarkup getKeyboard(Type currentType, List<SubscriptionInfo> subscriptionInfos, AILanguage language) {
        InlineKeyboardMarkup head = getChangeTypeButtons(currentType, language);
        InlineKeyboardMarkup subscriptions = getSubscriptionButtons(currentType, subscriptionInfos, language);
        InlineKeyboardMarkup separately = getSeparatelyBuyButtons(language);

        return ReplyMarkupUtil.getMergedKeyboard(head, subscriptions, separately);
    }

    private InlineKeyboardMarkup getChangeTypeButtons(Type currentType, AILanguage language) {
        List<ButtonViewData> viewDataList = buttonViewDataService.getByName(HEAD_BUTTONS);
        ButtonViewData monthViewData = viewDataList.get(0);
        ButtonViewData yearViewData = viewDataList.get(1);

        String currentSmile = "\uD83D\uDFE2";
        String month = monthViewData.getValueByLanguage(language);
        String monthBlind = Type.MONTH == currentType ? EMPTY.name() : String.join(Callback.ARGS_DELIMITER.getValue(), SUBSCRIPTION_BLIND_D.getBlindName(), Type.MONTH.name());
        String year = yearViewData.getValueByLanguage(language);
        String yearBlind = Type.YEAR == currentType ? EMPTY.name() : String.join(Callback.ARGS_DELIMITER.getValue(), SUBSCRIPTION_BLIND_D.getBlindName(), Type.YEAR.name());

        if (currentType == Type.MONTH) {
            month = currentSmile + month;
        } else {
            year = currentSmile + year;
        }

        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{month, monthBlind}, {year, yearBlind}}
        });
    }

    private InlineKeyboardMarkup getSubscriptionButtons(Type currentType, List<SubscriptionInfo> subscriptionInfos, AILanguage language) {

        String viewDataName = currentType == Type.MONTH ? BODY_MONTH_BUTTONS : BODY_YEAR_BUTTONS;

        String textPattern = buttonViewDataService.getByNameLanguageAndButtonNumber(viewDataName, language, 1);

        Collections.reverse(subscriptionInfos);
        return new InlineKeyboardMarkup(subscriptionInfos.stream()
                .distinct()
                .filter(el -> el.getType().getTimeType() == currentType)
                .map(el -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(getSubscriptionInfoString(textPattern, el));
                    button.setCallbackData(getBuySubscriptionCommand(el));
                    return List.of(button);
                }).toList());
    }

    private InlineKeyboardMarkup getSeparatelyBuyButtons(AILanguage language) {
        List<String> viewData = buttonViewDataService.getByNameAndLanguage(SEPARATE_BUTTONS, language);

        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{viewData.get(0), BUY_SEPARATE_BLIND.getBlindName()}},
                {{viewData.get(1), BUY_CLAUDE_BLIND.getBlindName()}},
        });
    }

    private static String getBuySubscriptionCommand(SubscriptionInfo info) {
        return TelegramCallbackUtils.getCallbackWithArgs(CHS_SUBS_PURCH_TYPE_BLIND_D.getBlindName(), info.getType().name());
    }

    private static String getSubscriptionInfoString(String textPattern, SubscriptionInfo info) {
        return MessageFormat.format(textPattern, info.getSmile(), info.getName(), info.getPrice());
    }

}
