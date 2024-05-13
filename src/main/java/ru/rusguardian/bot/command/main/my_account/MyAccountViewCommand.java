package ru.rusguardian.bot.command.main.my_account;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.Subscription;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.text.MessageFormat;

@Component
public class MyAccountViewCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.MY_ACCOUNT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {


    }

    private String getText(Update update) {
        Chat chat = getChat(update);
        Subscription subscription = chat.getSubscription();
        return MessageFormat.format(FileUtils.getTextFromFile(FileUtils.getFileFromResources2(this, "text/my_account.txt")),
                chat.getId(),
//                subscription.getSubscriptionType(),
//                subscription.getSubscriptionType() == SubscriptionType.FREE ? "-" : subscription.getExpirationTime(),
                subscription.getPurchaseTime()


        );
    }
}
