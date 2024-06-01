package ru.rusguardian.bot.command.main.my_account.partner_cabinet.cash_out;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CashOutBlindCommand extends Command {

    private static final String PARTNER_CASH_OUT = "PARTNER_CASH_OUT";
    private static final String PARTNER_CASH_OUT_ERROR = "PARTNER_CASH_OUT_ERROR";

    @Override
    public CommandName getType() {
        return CommandName.CASH_OUT_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        if (chat.getPartnerEmbeddedInfo().getBalance() < 50) {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                    .text(getTextByViewDataAndChatLanguage(PARTNER_CASH_OUT_ERROR, chat.getAiSettingsEmbedded().getAiLanguage()))
                    .callbackQueryId(TelegramUtils.getCallbackQueryId(update))
                    .build();

            bot.execute(answer);
            return;
        }

        editMessage(update, getText(chat), getKeyboard());
    }

    private String getText(Chat chat){
        return getTextByViewDataAndChatLanguage(PARTNER_CASH_OUT, chat.getAiSettingsEmbedded().getAiLanguage());
    }

    private InlineKeyboardMarkup getKeyboard(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = InlineKeyboardButton.builder().text(CommandName.BACK.getViewName()).callbackData(CommandName.PARTNER_CABINET_BLIND.getBlindName()).build();
        markup.setKeyboard(List.of(List.of(button)));

        return markup;
    }
}
