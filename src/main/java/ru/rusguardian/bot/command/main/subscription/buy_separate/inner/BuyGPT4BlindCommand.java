package ru.rusguardian.bot.command.main.subscription.buy_separate.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.main.subscription.buy_separate.inner.service.ChoosePurchaseCountKeyboardService;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;

@Component
@RequiredArgsConstructor
public class BuyGPT4BlindCommand extends Command {

    private final ChoosePurchaseCountKeyboardService choosePurchaseCountKeyboardService;
    private static final String BUY_SEPARATE_GPT4 = "BUY_SEPARATE_GPT4";

    @Override
    public CommandName getType() {
        return CommandName.BUY_GPT_4_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChatOwner(update);
        editMessage(update, getTextByViewDataAndChatLanguage(BUY_SEPARATE_GPT4, getChatOwner(update).getAiSettingsEmbedded().getAiLanguage()),
                choosePurchaseCountKeyboardService.getKeyboard(AIModel.BalanceType.GPT_4, chat.getAiSettingsEmbedded().getAiLanguage()));
    }
}
