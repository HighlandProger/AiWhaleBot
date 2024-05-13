package ru.rusguardian.bot.command.service.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@Slf4j
public class NotFoundCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.NOT_FOUND;
    }

    @Override
    public void mainExecute(Update update) throws TelegramApiException {

        SendMessage sendMessage = getCustomSendMessage(update);

        bot.execute(sendMessage);
    }

    private SendMessage getCustomSendMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(TelegramUtils.getChatId(update));
        sendMessage.setText("Неизвестная команда. Попробуйте /start");

        return sendMessage;
    }
}
