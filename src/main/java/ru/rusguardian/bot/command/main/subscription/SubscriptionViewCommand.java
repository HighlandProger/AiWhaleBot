package ru.rusguardian.bot.command.main.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;
import ru.rusguardian.util.SubscriptionsKeyboardUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionViewCommand extends Command {

    private static final String VIEW_DATA = "SUBSCRIPTION";

    @Override
    public CommandName getType() {
        return CommandName.SUBSCRIPTION_VIEW;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        Type type = Type.MONTH;
        //TODO refactor
        List<SubscriptionInfo> subscriptionInfos = new ArrayList<>(subscriptionInfoService.getAll().stream().filter(e -> e.getType().getTimeType() == type).toList());

        SendMessage message = SendMessageUtil.getSimple(update, getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()));
        message.setReplyMarkup(SubscriptionsKeyboardUtil.getKeyboard(type, subscriptionInfos));
        message.setParseMode(ParseMode.MARKDOWN);

        sendMessage(message);
    }

}
