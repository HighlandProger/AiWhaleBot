package ru.rusguardian.bot.command.main.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.main.subscription.service.SubscriptionsKeyboardService;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.Subscription;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@CommandMapping(viewCommands = {
        "/premium",
        "/subscription",
        "\uD83D\uDE80 Премиум",
        "\uD83D\uDE80 Premium",
        "\uD83D\uDE80 Premium",
        "\uD83D\uDE80 Premium"},
        isBlindVariable = true)
public class SubscriptionCommand extends Command {

    private final SubscriptionsKeyboardService subscriptionsKeyboardService;

    private static final String VIEW_DATA = "SUBSCRIPTION";

    @Override
    public CommandName getType() {
        return CommandName.SUBSCRIPTION;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        AILanguage language = getChatLanguage(update);
        if (update.hasCallbackQuery()) {
            edit(update, language);
            return;
        }
        send(update, language);
    }

    private void send(Update update, AILanguage language) throws TelegramApiException {
        Type type = Type.MONTH;
        List<Subscription> subscriptions = subscriptionService.getAll();

        SendMessage message = SendMessageUtil.getSimple(update, getTextByViewDataAndChatLanguage(VIEW_DATA, language));
        message.setReplyMarkup(subscriptionsKeyboardService.getKeyboard(type, subscriptions, language));
        message.setParseMode(ParseMode.HTML);

        sendMessage(message);
    }

    private void edit(Update update, AILanguage language) throws TelegramApiException {
        String typeString = TelegramCallbackUtils.getArgsFromCallback(update).length > 1
                ? TelegramCallbackUtils.getArgFromCallback(update, 1)
                : Type.MONTH.name();

        Type type = Type.valueOf(typeString);
        List<Subscription> subscriptions = subscriptionService.getAll();

        InlineKeyboardMarkup markup = subscriptionsKeyboardService.getKeyboard(type, subscriptions, getChatOwner(update).getAiSettingsEmbedded().getAiLanguage());

        editMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, language), markup);
    }
}
