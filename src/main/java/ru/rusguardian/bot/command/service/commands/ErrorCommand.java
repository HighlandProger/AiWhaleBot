package ru.rusguardian.bot.command.service.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;

@Component
@RequiredArgsConstructor
public class ErrorCommand extends Command {

    private static final String MESSAGE = "Произошла неизвестная ошибка, пожалуйста, попробуйте /start или обратитесь в поддержку @freeeman98";

    @Override
    public CommandName getType() {
        return CommandName.ERROR;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        setNullCompletedCommand(update);
        sendMessage(update, MESSAGE);
    }

}
