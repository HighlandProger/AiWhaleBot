package ru.rusguardian.bot.command.main.my_account.partner_cabinet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.get.ProcessGetTextPartner;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@RequiredArgsConstructor
@Component
public class PartnerCabinetBlindCommand extends Command {

    private static final String VIEW_DATA = "PARTNER_CABINET";

    private final ProcessGetTextPartner processGetTextPartner;

    @Override
    public CommandName getType() {
        return CommandName.PARTNER_CABINET_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        editMessage(update, getText(chat), getKeyboard(chat));
    }

    private String getText(Chat chat) {
        String textPattern = getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage());
        return processGetTextPartner.get(chat, textPattern);
    }

    private InlineKeyboardMarkup getKeyboard(Chat chat) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text("\uD83D\uDCE2 Поделиться ссылкой").switchInlineQuery(getShareWithContactsLink(chat.getId())).build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text(INVITE_WITH_BUTTON_BLIND.getViewName()).callbackData(INVITE_WITH_BUTTON_BLIND.getBlindName()).build();
        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text(CASH_OUT_BLIND.getViewName()).callbackData(CASH_OUT_BLIND.getBlindName()).build();
        InlineKeyboardButton button4 = InlineKeyboardButton.builder().text(BACK.getViewName()).callbackData(MY_ACCOUNT_VIEW.getBlindName()).build();

        markup.setKeyboard(List.of(List.of(button1), List.of(button2), List.of(button3), List.of(button4)));

        return markup;
    }

    private String getShareWithContactsLink(Long chatId) {
        return String.format("https://t.me/%s?start=%s\nНейробот\uD83D\uDC4D", bot.getBotUsername(), chatId);
    }

}
