package ru.rusguardian.bot.command.main.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

@Component
@RequiredArgsConstructor
public class SettingsViewCommand extends Command {

    private static final String FILE_PATH = "text/settings/";

    @Override
    public CommandName getType() {
        return CommandName.SETTINGS_VIEW;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);

        SendMessage message = SendMessageUtil.getSimple(update, getTextFromFileByChatLanguage(FILE_PATH, chat));
        message.setReplyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons(chat)));
        message.setParseMode(ParseMode.MARKDOWN);

        sendMessage(message);
    }


    private String[][][] getButtons(Chat chat) {
        String smileForContext = chat.getAiSettingsEmbedded().isContextEnabled() ? "✅" : "❌";
        String smileForVoice = chat.getAiSettingsEmbedded().isVoiceResponseEnabled() ? "\uD83D\uDD0A" : "\uD83D\uDD07";
        String smileForLanguage = AILanguage.RUSSIAN.getSmile();

        return new String[][][]{
                {{"\uD83E\uDD16 Выбрать модель GPT & Claude", CommandName.CHOOSE_AI_MODEL_BLIND.getBlindName()}},
                {{"\uD83C\uDFAD Выбрать GPT - Роль", CommandName.CHOOSE_AI_ROLE_BLIND.getBlindName()}},
                {{"\uD83C\uDFA8 Креативность ответов", CommandName.CHOOSE_TEMPERATURE_BLIND.getBlindName()}},
                {{smileForContext + " Поддержка контекста", CommandName.SWITCH_CONTEXT_BLIND.getBlindName()}},
                {{smileForVoice + " Голосовые ответы", CommandName.VOICE_RESPONSE_BLIND.getBlindName()}},
                {{smileForLanguage + " Голосовые ответы", CommandName.INTERFACE_LANGUAGE_BLIND.getBlindName()}}
        };
    }
}