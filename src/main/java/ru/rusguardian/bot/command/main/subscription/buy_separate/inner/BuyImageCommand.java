package ru.rusguardian.bot.command.main.subscription.buy_separate.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.util.ChoosePurchaseCountKeyboardUtil;

@Component
@RequiredArgsConstructor
public class BuyImageCommand extends Command {
    private static final String FILE_PATH = "text/subscription/buy_separate/image/";

    @Override
    public CommandName getType() {
        return CommandName.BUY_IMAGE_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        editMessage(update, getTextFromFileByChatLanguage(FILE_PATH, getChat(update)),
                ChoosePurchaseCountKeyboardUtil.getKeyboard(AIModel.BalanceType.IMAGE));
    }
}
