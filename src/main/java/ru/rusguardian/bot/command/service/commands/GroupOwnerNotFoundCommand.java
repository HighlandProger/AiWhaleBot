package ru.rusguardian.bot.command.service.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
public class GroupOwnerNotFoundCommand extends Command {
    @Override
    public CommandName getType() {
        return CommandName.GROUP_OWNER_NOT_FOUND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SendMessage message = SendMessage.builder().chatId(TelegramUtils.getChatIdString(update)).text("Владелец группы не зарегистрирован в боте").build();
        bot.execute(message);
    }
}
