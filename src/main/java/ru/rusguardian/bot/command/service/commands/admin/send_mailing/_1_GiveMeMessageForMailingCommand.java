package ru.rusguardian.bot.command.service.commands.admin.send_mailing;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.service.message.message_type.Textable;

@Component
public class _1_GiveMeMessageForMailingCommand extends Command implements Textable {
    @Override
    public CommandName getType() {
        return CommandName.GIVE_ME_MESSAGE_FOR_MAILING;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        sendMessage(update, this);
        setNextCommand(update, CommandName.CHECK_MAILING_MESSAGE);
    }

    @Override
    public String getText() {
        return "Отправьте текстовое сообщение. Также поддерживается голосовое, фото и видео (не круглешок) с описанием и без описания";
    }

    @Override
    public String[][][] getButtons() {
        return null;
    }
}
