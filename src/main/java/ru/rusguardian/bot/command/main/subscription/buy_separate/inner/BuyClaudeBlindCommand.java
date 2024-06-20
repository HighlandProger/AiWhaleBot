package ru.rusguardian.bot.command.main.subscription.buy_separate.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.main.subscription.buy_separate.inner.service.ChoosePurchaseCountKeyboardService;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.service.ai.constant.AIModel;

@Component
@RequiredArgsConstructor
public class BuyClaudeBlindCommand extends Command {

    private final ChoosePurchaseCountKeyboardService choosePurchaseCountKeyboardService;
    private static final String BUY_SEPARATE_CLAUDE = "BUY_SEPARATE_CLAUDE";

    @Override
    public CommandName getType() {
        return CommandName.BUY_CLAUDE_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        AILanguage language = getChatLanguage(update);
        editMessage(update, getTextByViewDataAndChatLanguage(BUY_SEPARATE_CLAUDE, language),
                choosePurchaseCountKeyboardService.getKeyboard(AIModel.BalanceType.CLAUDE, language));
    }
}
