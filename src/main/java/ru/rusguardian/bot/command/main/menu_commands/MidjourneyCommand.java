package ru.rusguardian.bot.command.main.menu_commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

@Component
@RequiredArgsConstructor
@CommandMapping(viewCommands = "/midjourney")
public class MidjourneyCommand extends Command {

    private static final String VIEW_DATA = "MIDJOURNEY";
    private static final String BUTTONS_VIEW_DATA = "IMAGE_BUY_BUTTONS";

    @Override
    public CommandName getType() {
        return CommandName.MIDJOURNEY;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChatOwner(update);
        SendMessage message = SendMessage.builder()
                .text(getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()))
                .chatId(chat.getId())
                .disableWebPagePreview(true)
                .replyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons(chat.getAiSettingsEmbedded().getAiLanguage())))
                .parseMode(ParseMode.HTML)
                .build();
        sendMessage(message);
    }

    private String[][][] getButtons(AILanguage language) {
        List<String> buttonsView = buttonViewDataService.getByNameAndLanguage(BUTTONS_VIEW_DATA, language);

        return new String[][][]{
                {{buttonsView.get(0), CommandName.SUBSCRIPTION.getBlindName()}},
                {{buttonsView.get(1), CommandName.BUY_SEPARATE_BLIND.getBlindName()}}
        };
    }
}
