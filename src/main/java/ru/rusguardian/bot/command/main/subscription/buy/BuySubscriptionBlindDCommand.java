package ru.rusguardian.bot.command.main.subscription.buy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.main.subscription.Type;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;

@Component
@RequiredArgsConstructor
public class BuySubscriptionBlindDCommand extends Command {
    @Override
    public CommandName getType() {
        return CommandName.BUY_SUBSCRIPTION_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SubscriptionType subscriptionType = SubscriptionType.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));
        Type type = Type.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 2));


    }
}
