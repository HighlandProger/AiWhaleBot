package ru.rusguardian.bot.command.service.commands.admin.get_input_file_id_chain;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;

@Component
public class _1_AskInputFileCommand extends Command {

    private static final String MESSAGE = "Отправьте файл";

    @Override
    public CommandName getType() {
        return CommandName.ASK_INPUT_FILE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        sendMessage(update, MESSAGE);
        setNextCommand(update, CommandName.RETURN_INPUT_FILE_ID);
    }
}
