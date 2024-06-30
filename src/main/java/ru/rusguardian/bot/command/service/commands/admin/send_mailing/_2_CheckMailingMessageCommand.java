package ru.rusguardian.bot.command.service.commands.admin.send_mailing;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramMessageUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

@Component
public class _2_CheckMailingMessageCommand extends Command {
    private static final String CHECK_MESSAGE = "Проверьте сообщение";

    @Override
    public CommandName getType() {
        return CommandName.CHECK_MAILING_MESSAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int messageId = TelegramMessageUtils.forwardMessage(bot, update.getMessage(), TelegramUtils.getChatIdString(update)).orElseThrow().getMessageId();
        SendMessage message = SendMessage.builder()
                .text(CHECK_MESSAGE)
                .chatId(TelegramUtils.getChatId(update))
                .replyToMessageId(messageId)
                .replyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons()))
                .build();

        sendMessage(message);
    }

    private String[][][] getButtons() {
        return new String[][][]{
                {{"Отправить снова", CommandName.GIVE_ME_MESSAGE_FOR_MAILING.getBlindName()}, {"Подтвердить", CommandName.SEND_MAILING.getBlindName()}},
                {{"Назад", CommandName.ADMIN.getBlindName()}}
        };
    }
}
