package ru.rusguardian.bot.command.service.commands.admin.get_input_file_id_chain;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
public class _2_ReturnInputFileIdCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.RETURN_INPUT_FILE_ID;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        String response = TelegramUtils.getInputFileId(update);
        sendMessage(update, response);
        setNullCompletedCommand(update);
    }
}
