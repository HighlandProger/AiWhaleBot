package ru.rusguardian.bot.command.main.my_account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.get.ProcessGetTextUserAccount;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Component
@RequiredArgsConstructor
@CommandMapping(viewCommands = {
        "\uD83D\uDC64 Мой аккаунт",
        "\uD83D\uDC64 My account",
        "\uD83D\uDC64 Mein Konto",
        "\uD83D\uDC64 Mening hisobim"})
public class MyAccountCommand extends Command {

    private static final String VIEW_DATA = "MY_ACCOUNT";

    private final ProcessGetTextUserAccount getUserAccountTextService;

    @Override
    public CommandName getType() {
        return CommandName.MY_ACCOUNT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChatOwner(update);
        if (update.hasCallbackQuery()) {
            editMessage(update, getText(chat), getKeyboard(chat.getAiSettingsEmbedded().getAiLanguage()));
        } else sendMessage(update, getText(chat), getKeyboard(chat.getAiSettingsEmbedded().getAiLanguage()));
    }

    private String getText(Chat chat) {
        String textPattern = getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage());
        return getUserAccountTextService.get(chat, textPattern);
    }

    private InlineKeyboardMarkup getKeyboard(AILanguage language) {

        List<String> viewButtons = buttonViewDataService.getByNameAndLanguage(getType().name(), language);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text(viewButtons.get(0)).callbackData(SETTINGS.getBlindName()).build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text(viewButtons.get(1)).callbackData(PARTNER_CABINET.getBlindName()).build();
        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text(viewButtons.get(2)).url("https://t.me/freeeman98").build();
        InlineKeyboardButton button4 = InlineKeyboardButton.builder().text(viewButtons.get(3)).callbackData(SUBSCRIPTION_BLIND_D.getBlindName()).build();

        markup.setKeyboard(List.of(List.of(button1), List.of(button2), List.of(button3), List.of(button4)));

        return markup;
    }


}
