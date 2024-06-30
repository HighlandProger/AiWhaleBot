package ru.rusguardian.bot.command.service.commands.admin.reports.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.repository.ChatRepository;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GetUsersReportCommand extends Command {

    private final ChatRepository chatRepository;

    @Override
    public CommandName getType() {
        return CommandName.GET_USERS_REPORT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Long allCount = chatRepository.getAllChatsCount();
        Long allNotKickedCount = chatRepository.getAllNotKickedChatsCount();
        Long allAddedDuringWeek = chatRepository.getAllNotKickedAndAddedDuringWeekChatsCount(LocalDateTime.now().minusWeeks(1L));

        editMessage(update, getText(allCount, allNotKickedCount, allAddedDuringWeek), ReplyMarkupUtil.getInlineKeyboard(getButtons()));
    }

    private String getText(Long allCount, Long allNotKickedCount, Long allAddedDuringWeek){
        String pattern = """
                Отчет по пользователям:
                Всего в базе: {0}
                Активные пользователи: {1}
                Добавились в течение недели: {2}
                """;

        return MessageFormat.format(pattern, allCount, allNotKickedCount, allAddedDuringWeek);
    }

    private String[][][] getButtons(){
        return new String[][][]{
                {{CommandName.BACK.getViewName(), CommandName.CHOOSE_REPORT.getBlindName()}}
        };
    }
}
