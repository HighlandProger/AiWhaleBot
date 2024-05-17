package ru.rusguardian.bot.command.main.my_account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.ProcessGetTextUserAccount;
import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Component
@RequiredArgsConstructor
public class MyAccountViewCommand extends Command {

    private static final String FILE_PATH = "text/my_account/";

    private final ProcessGetTextUserAccount getUserAccountTextService;

    @Override
    public CommandName getType() {
        return CommandName.MY_ACCOUNT_VIEW;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        sendMessage(update, getText(update), getKeyboard());
    }

    private String getText(Update update) {
        Chat chat = getChat(update);
        String textPattern = getTextFromFileByChatLanguage(FILE_PATH, chat);
        return getUserAccountTextService.process(chat, textPattern);
    }

    private InlineKeyboardMarkup getKeyboard() {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text(SETTINGS_BLIND.getViewName()).callbackData(SETTINGS_BLIND.getBlindName()).build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text(PARTNER_CABINET_BLIND.getViewName()).callbackData(PARTNER_CABINET_BLIND.getBlindName()).build();
        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text("\uD83D\uDC68\u200D\uD83D\uDD27 Техподдержка").url("https://t.me/freeeman98").build();
        InlineKeyboardButton button4 = InlineKeyboardButton.builder().text(BUY_PREMIUM.getViewName()).callbackData(BUY_PREMIUM.getBlindName()).build();

        markup.setKeyboard(List.of(List.of(button1), List.of(button2), List.of(button3), List.of(button4)));

        return markup;
    }


}
