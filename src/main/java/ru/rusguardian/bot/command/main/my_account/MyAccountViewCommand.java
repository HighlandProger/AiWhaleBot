package ru.rusguardian.bot.command.main.my_account;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

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

        String textPattern = FileUtils.getTextFromFile(FileUtils.getFileFromResources2(this, "text/my_account.txt"));
        Chat chat = getChat(update);
//        SubscriptionEmbedded subscriptionEmbedded = chat.getSubscriptionEmbedded();

//        return MessageFormat.format(FileUtils.getTextFromFile(FileUtils.getFileFromResources2(this, "text/my_account.txt")),
//                chat.getId(),
//                subscriptionEmbedded.getSubscriptionType(),
//                subscriptionEmbedded.getSubscriptionType() == SubscriptionType.FREE ? "-" : subscriptionEmbedded.getExpirationTime(),
//                subscriptionEmbedded.getPurchaseTime()
        return null;

    }
}
