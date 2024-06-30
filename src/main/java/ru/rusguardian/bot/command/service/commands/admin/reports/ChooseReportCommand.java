package ru.rusguardian.bot.command.service.commands.admin.reports;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

@Component
public class ChooseReportCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.CHOOSE_REPORT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        EditMessageText edit = EditMessageText.builder()
                .chatId(TelegramUtils.getChatId(update))
                .messageId(TelegramUtils.getMessageId(update))
                .text("Выберите отчет")
                .replyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons()))
                .build();

        edit(edit);
    }

    private String[][][] getButtons(){
        return new String[][][]{
                {{"Пользователи", CommandName.GET_USERS_REPORT.getBlindName()}, {"Заказы", CommandName.GET_ORDERS_REPORT.getBlindName()}},
                {{CommandName.BACK.getViewName(), CommandName.ADMIN.getBlindName()}}
        };
    }
}
