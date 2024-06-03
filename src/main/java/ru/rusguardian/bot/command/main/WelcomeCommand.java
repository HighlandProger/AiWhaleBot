package ru.rusguardian.bot.command.main;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
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
        Chat chat = getChat(update);
        sendMessage(update, getText(chat), getMainKeyboard(chat.getAiSettingsEmbedded().getAiLanguage()));
    }

    public String getText(Chat chat) {
        return getTextByViewDataAndChatLanguage(getType().name(), chat.getAiSettingsEmbedded().getAiLanguage());
    }

    private ReplyKeyboard getMainKeyboard(AILanguage language) {

        List<String> buttons = buttonViewDataService.getByCommandNameAndLanguage(getType(), language);

        return ReplyMarkupUtil.getReplyKeyboard(List.of(
                List.of(buttons.get(0), buttons.get(1)),
                List.of(buttons.get(2), buttons.get(3))
        ));
    }

}
