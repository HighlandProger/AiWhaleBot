package ru.rusguardian.bot.command.main.settings.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.ai.AITemperature;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChooseTemperatureBlindDifCommand extends Command {

    private static final String SETTINGS_CHOOSE_TEMPERATURE = "SETTINGS_CHOOSE_TEMPERATURE";
    private static final String OPERATION_RESTRICTED_FOR_FREE = "OPERATION_RESTRICTED_FOR_FREE";

    @Override
    public CommandName getType() {
        return CommandName.CHOOSE_TEMPERATURE_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = getChat(update);
        String temperatureString = TelegramCallbackUtils.getArgFromCallback(update, 1);

        if (chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
            AnswerCallbackQuery callbackQuery = new AnswerCallbackQuery();
            callbackQuery.setText(getTextByViewDataAndChatLanguage(OPERATION_RESTRICTED_FOR_FREE, chat.getAiSettingsEmbedded().getAiLanguage()));
            callbackQuery.setCallbackQueryId(TelegramUtils.getCallbackQueryId(update));
            callbackQuery.setShowAlert(true);
            bot.execute(callbackQuery);
            return;
        }

        if (temperatureString != null) {
            if (chat.getAiSettingsEmbedded().getTemperature() == AITemperature.valueOf(temperatureString)) {
                return;
            }
            changeChatTemperature(chat, temperatureString);
        }

        AITemperature currentTemperature = chat.getAiSettingsEmbedded().getTemperature();
        editMessage(update, getText(chat.getAiSettingsEmbedded().getAiLanguage()), getKeyboard(currentTemperature));
    }

    private InlineKeyboardMarkup getKeyboard(AITemperature aiTemperature) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(getButton(AITemperature.HIGHEST, aiTemperature)));
        keyboard.add(List.of(getButton(AITemperature.HIGH, aiTemperature)));
        keyboard.add(List.of(getButton(AITemperature.MIDDLE, aiTemperature)));
        keyboard.add(List.of(getButton(AITemperature.LOW, aiTemperature)));
        keyboard.add(List.of(getButton(AITemperature.LOWEST, aiTemperature)));
        keyboard.add(List.of(InlineKeyboardButton.builder().text(CommandName.BACK.getViewName()).callbackData(CommandName.SETTINGS_BLIND.getBlindName()).build()));

        markup.setKeyboard(keyboard);
        return markup;
    }

    private InlineKeyboardButton getButton(AITemperature aiTemperature, AITemperature currentTemperature) {
        return InlineKeyboardButton.builder()
                .text(getViewTextForButton(aiTemperature, currentTemperature))
                .callbackData(getCallback(aiTemperature))
                .build();
    }

    private String getViewTextForButton(AITemperature aiTemperature, AITemperature currentTemperature) {
        String smile = aiTemperature == currentTemperature ? "✅" : "";
        return smile + " " + aiTemperature.getValue();
    }

    private String getCallback(AITemperature aiTemperature) {
        return TelegramCallbackUtils.getCallbackWithArgs(CommandName.CHOOSE_TEMPERATURE_BLIND_D.getBlindName(), aiTemperature.name());
    }

    private String getText(AILanguage aiLanguage) {
        return getTextByViewDataAndChatLanguage(SETTINGS_CHOOSE_TEMPERATURE, aiLanguage);
    }

    private void changeChatTemperature(Chat chat, String temperature) {
        AITemperature aiTemperature = AITemperature.valueOf(temperature);

        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();
        settings.setTemperature(aiTemperature);
        chatService.update(chat);
    }

}
