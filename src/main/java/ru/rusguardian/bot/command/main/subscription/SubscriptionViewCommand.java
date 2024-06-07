package ru.rusguardian.bot.command.main.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.main.subscription.service.SubscriptionsKeyboardService;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@CommandMapping(viewCommands = {
        "\uD83D\uDE80 Премиум",
        "\uD83D\uDE80 Premium",
        "\uD83D\uDE80 Premium",
        "\uD83D\uDE80 Premium"})
public class SubscriptionViewCommand extends Command {

    private final SubscriptionsKeyboardService subscriptionsKeyboardService;

    private static final String VIEW_DATA = "SUBSCRIPTION";

    @Override
    public CommandName getType() {
        return CommandName.SUBSCRIPTION_VIEW;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        Type type = Type.MONTH;
        List<SubscriptionInfo> subscriptionInfos = new ArrayList<>(subscriptionInfoService.getAll().stream().filter(e -> e.getType().getTimeType() == type).toList());

        SendMessage message = SendMessageUtil.getSimple(update, getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()));
        message.setReplyMarkup(subscriptionsKeyboardService.getKeyboard(type, subscriptionInfos, chat.getAiSettingsEmbedded().getAiLanguage()));
        message.setParseMode(ParseMode.HTML);

        sendMessage(message);
    }

}
