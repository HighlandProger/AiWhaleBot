package ru.rusguardian.bot.command.main.my_account.partner_cabinet.cash_out;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
public class CashOutBlindCommand extends Command {

    private static final String FILE_PATH = "text/my_account/partner_cabinet/cash_out/";
    private static final String ERROR_FILE_PATH = FILE_PATH + "error/";

    @Override
    public CommandName getType() {
        return CommandName.CASH_OUT_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        if (chat.getPartnerEmbeddedInfo().getBalance() < 50) {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                    .text(getTextFromFileByChatLanguage(ERROR_FILE_PATH, chat))
                    .callbackQueryId(TelegramUtils.getCallbackQueryId(update))
                    .build();

            bot.execute(answer);
        }
        //TODO
        else throw new RuntimeException("NOT REALIZED");
    }
}
