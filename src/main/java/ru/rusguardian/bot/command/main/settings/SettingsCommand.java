package ru.rusguardian.bot.command.main.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.util.List;

@Component
@RequiredArgsConstructor
@CommandMapping(viewCommands = {
        "⚙\uFE0F Настройки",
        "⚙\uFE0F Settings",
        "⚙\uFE0F Einstellungen",
        "⚙\uFE0F Sozlamalar"})
public class SettingsCommand extends Command {

    private static final String SETTINGS = "SETTINGS";

    @Override
    public CommandName getType() {
        return CommandName.SETTINGS;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        if (update.hasCallbackQuery()) {
            editMessage(update, getTextByViewDataAndChatLanguage(SETTINGS, chat.getAiSettingsEmbedded().getAiLanguage()), ReplyMarkupUtil.getInlineKeyboard(getButtons(chat)));
        } else {
            SendMessage message = SendMessageUtil.getSimple(update, getTextByViewDataAndChatLanguage(SETTINGS, chat.getAiSettingsEmbedded().getAiLanguage()));
            message.setReplyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons(chat)));
            sendMessage(message);
        }
    }


    private String[][][] getButtons(Chat chat) {
        AILanguage language = chat.getAiSettingsEmbedded().getAiLanguage();
        String smileForContext = chat.getAiSettingsEmbedded().isContextEnabled() ? "✅" : "❌";
        String smileForVoice = chat.getAiSettingsEmbedded().isVoiceResponseEnabled() ? "\uD83D\uDD0A" : "\uD83D\uDD07";
        String smileForLanguage = language.getSmile();
        List<String> buttonsView = buttonViewDataService.getByNameAndLanguage(getType().name(), language);

        return new String[][][]{
                {{buttonsView.get(0), CommandName.CHOOSE_AI_MODEL_BLIND_D.getBlindName()}},
                {{buttonsView.get(1), CommandName.CHOOSE_AI_ROLE_BLIND.getBlindName()}},
                {{buttonsView.get(2), CommandName.CHOOSE_TEMPERATURE_BLIND_D.getBlindName()}},
                {{smileForContext + buttonsView.get(3), CommandName.SWITCH_CONTEXT_BLIND.getBlindName()}},
                {{smileForVoice + buttonsView.get(4), CommandName.SWITCH_VOICE_RESPONSE_BLIND.getBlindName()}},
                {{smileForLanguage + buttonsView.get(5), CommandName.CHOOSE_LANGUAGE_BLIND_D.getBlindName()}}
        };
    }
}
