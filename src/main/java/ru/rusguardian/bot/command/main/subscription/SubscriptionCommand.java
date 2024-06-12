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
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.domain.user.Chat;
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
        Chat chat = getChatOwner(update);
        if (update.hasCallbackQuery()) {
            edit(update, chat);
            return;
        }
        send(update, chat);
    }

    private void send(Update update, Chat chat) throws TelegramApiException {
        Type type = Type.MONTH;
        List<SubscriptionInfo> subscriptionInfos = new ArrayList<>(subscriptionInfoService.getAll().stream().filter(e -> e.getType().getTimeType() == type).toList());

        SendMessage message = SendMessageUtil.getSimple(update, getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()));
        message.setReplyMarkup(subscriptionsKeyboardService.getKeyboard(type, subscriptionInfos, chat.getAiSettingsEmbedded().getAiLanguage()));
        message.setParseMode(ParseMode.HTML);

        sendMessage(message);
    }

    private void edit(Update update, Chat chat) throws TelegramApiException {
        String typeString = TelegramCallbackUtils.getArgsFromCallback(update).length > 1
                ? TelegramCallbackUtils.getArgFromCallback(update, 1)
                : Type.MONTH.name();

        Type type = Type.valueOf(typeString);
        List<SubscriptionInfo> subscriptionInfos = new ArrayList<>(subscriptionInfoService.getAll().stream().filter(e -> e.getType().getTimeType() == type).toList());

        InlineKeyboardMarkup markup = subscriptionsKeyboardService.getKeyboard(type, subscriptionInfos, getChatOwner(update).getAiSettingsEmbedded().getAiLanguage());

        editMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()), markup);
    }
}
