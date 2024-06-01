package ru.rusguardian.bot.command.main.my_account.partner_cabinet.invite_with_but;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InviteWIthButtonBlindCommand extends Command {

    private static final String INVITE_MESSAGE = "INVITE_MESSAGE";

    @Override
    public CommandName getType() {
        return CommandName.INVITE_WITH_BUTTON_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = chatService.findById(TelegramUtils.getChatId(update));
        SendMessage message = SendMessageUtil.getSimple(update, getTextByViewDataAndChatLanguage(INVITE_MESSAGE, chat.getAiSettingsEmbedded().getAiLanguage()));
        message.setReplyMarkup(getKeyboard(chat.getId()));

        bot.execute(message);
    }

    private InlineKeyboardMarkup getKeyboard(Long chatId) {
        InlineKeyboardButton button = InlineKeyboardButton.builder().text("Попробовать и Получить Бонус \uD83C\uDF81").url(getUrl(chatId)).build();
        return InlineKeyboardMarkup.builder().keyboard(List.of(List.of(button))).build();
    }


    //TODO minor refactor
    private String getUrl(Long chatId) {
        return String.format("%s%s?start=%s", "https://t.me/", bot.getBotUsername(), chatId);
    }
}
