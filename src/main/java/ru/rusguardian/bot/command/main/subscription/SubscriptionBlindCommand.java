package ru.rusguardian.bot.command.main.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMarkupUtil;
import ru.rusguardian.util.SubscriptionsKeyboardUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionBlindCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.SUBSCRIPTION_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        String typeString = TelegramCallbackUtils.getArgsFromCallback(update).length > 1
                ? TelegramCallbackUtils.getArgFromCallback(update, 1)
                : Type.MONTH.name();

        Type type = Type.valueOf(typeString);
        List<SubscriptionInfo> subscriptionInfos = new ArrayList<>(subscriptionInfoService.getAll().stream().filter(e -> e.getType().getTimeType() == type).toList());

        InlineKeyboardMarkup markup = SubscriptionsKeyboardUtil.getKeyboard(type, subscriptionInfos);

        EditMessageReplyMarkup edit = EditMarkupUtil.getSimple(update, markup);

        bot.execute(edit);
    }
}
