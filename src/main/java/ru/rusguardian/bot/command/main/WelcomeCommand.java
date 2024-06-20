package ru.rusguardian.bot.command.main;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;

@Component
public class WelcomeCommand extends Command {
    @Override
    public CommandName getType() {
        return CommandName.WELCOME;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        AILanguage language = getChatLanguage(update);
        sendMessage(update, getText(language), getMainKeyboard(language));
    }

    public String getText(AILanguage language) {
        return getTextByViewDataAndChatLanguage(getType().name(), language);
    }


}
