package ru.rusguardian.bot.command.service.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@Slf4j
public class MainMenuCommand extends Command {

    private static final String MESSAGE = "Вы в главном меню";

    @Override
    public CommandName getType() {
        return CommandName.MAIN_MENU;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = chatService.findById(TelegramUtils.getChatId(update));

        editMessage(update, MESSAGE, null);
        setNullCompletedCommand(update);
    }
}
