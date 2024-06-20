package ru.rusguardian.bot.command.prompts.image.img.inner;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
public class ClaudeSolveForImageCommand extends Command {
    @Override
    public CommandName getType() {
        return CommandName.CLAUDE_SOLVE_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(TelegramUtils.getCallbackQueryId(update));
        answer.setText("Пока еще в разработке");
        answer.setShowAlert(true);
        bot.execute(answer);
    }
}
