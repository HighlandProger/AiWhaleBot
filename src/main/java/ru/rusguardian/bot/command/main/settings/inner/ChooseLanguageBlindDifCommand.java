package ru.rusguardian.bot.command.main.settings.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChooseLanguageBlindDifCommand extends Command {

    private static final String VIEW_DATA = "CHOOSE_LANGUAGE";

    @Override
    public CommandName getType() {
        return CommandName.CHOOSE_LANGUAGE_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = getChat(update);
        String languageToChangeString = TelegramCallbackUtils.getArgFromCallback(update, 1);

        if (languageToChangeString != null) {
            if (chat.getAiSettingsEmbedded().getAiLanguage() == AILanguage.valueOf(languageToChangeString)){return;}
            changeChatLanguage(chat, languageToChangeString);
        }
        AILanguage currentLanguage = chat.getAiSettingsEmbedded().getAiLanguage();;
        editMessage(update, getText(currentLanguage), getKeyboard(currentLanguage));
    }

    private void changeChatLanguage(Chat chat, String languageString) {
        AILanguage language = AILanguage.valueOf(languageString);

        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();
        settings.setAiLanguage(language);
        chatService.update(chat);
    }

    private String getText(AILanguage currentLanguage){
        return getTextByViewDataAndChatLanguage(VIEW_DATA, currentLanguage);
    }

    private InlineKeyboardMarkup getKeyboard(AILanguage currentLanguage){

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(getButton(AILanguage.RUSSIAN, currentLanguage)));
        keyboard.add(List.of(getButton(AILanguage.ENGLISH, currentLanguage)));
        keyboard.add(List.of(getButton(AILanguage.GERMAN, currentLanguage)));
        keyboard.add(List.of(getButton(AILanguage.UZBEK, currentLanguage)));
        keyboard.add(List.of(InlineKeyboardButton.builder().text(CommandName.BACK.getViewName()).callbackData(CommandName.SETTINGS_BLIND.getBlindName()).build()));

        markup.setKeyboard(keyboard);
        return markup;
    }

    private InlineKeyboardButton getButton(AILanguage language, AILanguage currentLanguage){
        return InlineKeyboardButton.builder()
                .text(getViewTextForButton(language, currentLanguage))
                .callbackData(getCallback(language))
                .build();
    }
    private String getViewTextForButton(AILanguage language, AILanguage currentLanguage){
        String smile = language == currentLanguage ? "✅" : "";
        return smile + " " + language.getSmile() + language.getDescription();
    }

    private String getCallback(AILanguage language){
        return TelegramCallbackUtils.getCallbackWithArgs(CommandName.CHOOSE_LANGUAGE_BLIND_D.getBlindName(), language.name());
    }
}
