package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Service
@RequiredArgsConstructor
public class ProcessAssistantTypeChange {


    public void process(Update update, Chat chat, TelegramLongPollingBot bot) throws TelegramApiException {
        AssistantRole type = AssistantRole.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 2));
        if (chat.getAiSettingsEmbedded().getAssistantRole() == type) {
            return;
        }
        if (chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(TelegramUtils.getCallbackQueryId(update));
            answer.setShowAlert(true);
            answer.setText("Нельзя");
            bot.execute(answer);
            return;
        }

        chat.getAiSettingsEmbedded().setAssistantRole(type);
    }
}
