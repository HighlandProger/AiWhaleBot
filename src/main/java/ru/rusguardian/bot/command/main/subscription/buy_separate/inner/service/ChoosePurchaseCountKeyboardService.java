package ru.rusguardian.bot.command.main.subscription.buy_separate.inner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ButtonViewDataService;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@RequiredArgsConstructor
@Service
public class ChoosePurchaseCountKeyboardService {

    private final ButtonViewDataService buttonViewDataService;
    private static final String GPT_4_IMAGE_BUTTON_VIEW_DATA = "GPT_4_IMAGE_SEPARATE_PURCHASE";
    private static final String CLAUDE_BUTTON_VIEW_DATA = "CLAUDE_SEPARATE_PURCHASE";

    public InlineKeyboardMarkup getKeyboard(AIModel.BalanceType balanceType, AILanguage language) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(SeparatePurchase.getByBalanceType(balanceType).stream().map(purchase -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(getButtonText(purchase, language));
            button.setCallbackData(getCallback(purchase));
            return List.of(button);
        }).toList());
        buttons.add(List.of(InlineKeyboardButton.builder().text(BACK.getViewName()).callbackData(BUY_SEPARATE_BLIND.getBlindName()).build()));
        return ReplyMarkupUtil.getInlineKeyboardByButtons(buttons);
    }

    private String getButtonText(SeparatePurchase separatePurchase, AILanguage language) {

        return switch (separatePurchase.getBalanceType()) {
            case GPT_4, IMAGE -> getButtonFormattedString(GPT_4_IMAGE_BUTTON_VIEW_DATA, separatePurchase, language);
            case CLAUDE -> getButtonFormattedString(CLAUDE_BUTTON_VIEW_DATA, separatePurchase, language);
            default -> throw new RuntimeException("Not supported balance type");
        };
    }

    private String getButtonFormattedString(String buttonViewName, SeparatePurchase separatePurchase, AILanguage language) {
        String pattern = buttonViewDataService.getByNameLanguageAndButtonNumber(buttonViewName, language, 1);
        return MessageFormat.format(pattern, separatePurchase.getCount(), separatePurchase.getPrice());
    }

    private String getCallback(SeparatePurchase separatePurchase) {
        return TelegramCallbackUtils.getCallbackWithArgs(CHS_SEP_PURCH_TYPE_BLIND_D.getBlindName(), separatePurchase.name());
    }
}
