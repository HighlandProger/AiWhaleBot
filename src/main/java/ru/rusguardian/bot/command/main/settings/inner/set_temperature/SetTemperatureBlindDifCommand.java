package ru.rusguardian.bot.command.main.settings.inner.set_temperature;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

@Component
@RequiredArgsConstructor
public class SetTemperatureBlindDifCommand extends Command {

    private static final String FILE_PATH = "text/settings/choose_temperature/set_temperature/";

    @Override
    public CommandName getType() {
        return CommandName.SET_TEMPERATURE_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        updateChatTemperature(update, chat);
        EditMessageText edit = EditMessageUtil.getMessageText(update, getTextFromFileByChatLanguage(FILE_PATH, chat));
        edit.setReplyMarkup(ReplyMarkupUtil.getInlineKeyboard(getButtons()));

        bot.execute(edit);
    }

    private String [][][] getButtons(){
        return new String[][][]{
                {{CommandName.BACK.getViewName(), CommandName.SETTINGS_BLIND.getBlindName()}}
        };
    }
    private void updateChatTemperature(Update update, Chat chat){
        String valueString = TelegramCallbackUtils.getArgFromCallback(update, 1);
        double temperature = Double.parseDouble(valueString);
        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();
        settings.setTemperature(temperature);
        chatService.update(chat);
    }
}
