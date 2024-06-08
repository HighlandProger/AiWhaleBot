package ru.rusguardian.bot.command.main.my_account.partner_cabinet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.get.ProcessGetTextPartner;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@RequiredArgsConstructor
@Component
@CommandMapping(viewCommands = "/partner")
public class PartnerCabinetCommand extends Command {

    private static final String VIEW_DATA = "PARTNER_CABINET";

    private final ProcessGetTextPartner processGetTextPartner;

    @Override
    public CommandName getType() {
        return CommandName.PARTNER_CABINET;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChatOwner(update);
        if (update.hasCallbackQuery()) {
            editMessage(update, getText(chat), getKeyboard(chat));
        } else sendMessage(update, getText(chat), getKeyboard(chat));
    }

    private String getText(Chat chat) {
        String textPattern = getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage());
        return processGetTextPartner.get(chat, textPattern);
    }

    private InlineKeyboardMarkup getKeyboard(Chat chat) {

        List<String> buttonViews = buttonViewDataService.getByNameAndLanguage(getType().name(), chat.getAiSettingsEmbedded().getAiLanguage());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text(buttonViews.get(0)).switchInlineQuery(getShareWithContactsLink(chat.getId())).build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text(buttonViews.get(1)).callbackData(INVITE_WITH_BUTTON_BLIND.getBlindName()).build();
        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text(buttonViews.get(2)).callbackData(CASH_OUT_BLIND.getBlindName()).build();
        InlineKeyboardButton button4 = InlineKeyboardButton.builder().text(buttonViews.get(3)).callbackData(MY_ACCOUNT.getBlindName()).build();

        markup.setKeyboard(List.of(List.of(button1), List.of(button2), List.of(button3), List.of(button4)));

        return markup;
    }

    private String getShareWithContactsLink(Long chatId) {
        return String.format("https://t.me/%s?start=%s\nНейробот\uD83D\uDC4D", bot.getBotUsername(), chatId);
    }

}
