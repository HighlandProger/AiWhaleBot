package ru.rusguardian.bot.command.main.settings.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@RequiredArgsConstructor
@Service
public class SwitchVoiceResponseBlindCommand extends Command {

    private final static String OPERATION_RESTRICTED = "text/settings/operation_restricted_for_free/";

    @Override
    public CommandName getType() {
        return CommandName.SWITCH_VOICE_RESPONSE_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = getChat(update);
        if (isFreeSubscription(chat)) {
            sendOperationRestrictedToChat(chat, update);
            return;
        }
        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();
        settings.setVoiceResponseEnabled(!settings.isVoiceResponseEnabled());
        chat.setAiSettingsEmbedded(settings);
        chatService.update(chat);

        commandContainerService.getCommand(CommandName.SETTINGS_BLIND).execute(update);
    }

    private boolean isFreeSubscription(Chat chat) {
        return chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE;
    }

    private void sendOperationRestrictedToChat(Chat chat, Update update) throws TelegramApiException {
        AnswerCallbackQuery callbackQuery = new AnswerCallbackQuery();
        callbackQuery.setText(getTextFromFileByChatLanguage(OPERATION_RESTRICTED, chat));
        callbackQuery.setCallbackQueryId(TelegramUtils.getCallbackQueryId(update));
        callbackQuery.setShowAlert(true);

        bot.execute(callbackQuery);
    }
}