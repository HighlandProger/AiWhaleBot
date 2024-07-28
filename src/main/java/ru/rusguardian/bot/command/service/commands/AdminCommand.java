package ru.rusguardian.bot.command.service.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

@Component
@CommandMapping(viewCommands = {"/admin"})
public class AdminCommand extends Command {

    private static final String MESSAGE = "Выберите функцию";
    private static final String NOT_ADMIN_MESSAGE = "У вас нет доступа к этой функции";

    @Override
    public CommandName getType() {
        return CommandName.ADMIN;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = chatService.findById(TelegramUtils.getChatId(update));
        setNullCompletedCommand(update);

        if (!chat.isAdmin()) sendMessage(update, NOT_ADMIN_MESSAGE);
        else sendMessage(update, MESSAGE, getKeyboard());
    }

    private ReplyKeyboard getKeyboard() {
        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{"Узнать ID файла", CommandName.ASK_INPUT_FILE.getBlindName()}},
                {{"Отправить рассылку", CommandName.GIVE_ME_MESSAGE_FOR_MAILING.getBlindName()}},
                {{"Отчеты", CommandName.CHOOSE_REPORT.getBlindName()}},
        });
    }

}
