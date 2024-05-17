package ru.rusguardian.bot.command.main;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

@Component
public class WelcomeCommand extends Command {
    @Override
    public CommandName getType() {
        return CommandName.WELCOME;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        sendMessage(update, getText(), getMainKeyboard());
    }

    public String getText() {
        return getTextFromFileInResources("text/welcome.txt");
    }

    private ReplyKeyboard getMainKeyboard() {
        return ReplyMarkupUtil.getReplyKeyboard(List.of(
                List.of(CommandName.GPT_ROLES_VIEW.getViewName(), CommandName.MY_ACCOUNT_VIEW.getViewName()),
                List.of(CommandName.SUBSCRIPTION_VIEW.getViewName(), CommandName.SETTINGS_VIEW.getViewName())
        ));
    }

}
