package ru.rusguardian.bot.command.main.my_account;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Component
public class MyAccountViewCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.MY_ACCOUNT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        sendMessage(update, getText(update));
    }

    private String getText(Update update) {

        String textPattern = FileUtils.getTextFromFile(FileUtils.getFileFromResources2(this, "text/my_account.txt"));
        Chat chat = getChat(update);

        return MessageFormat.format(textPattern,
                chat.getId(),
                chat.getSubscriptionEmbedded().getSubscriptionInfo().getType(),
                chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE ? "-" : chat.getSubscriptionEmbedded().getExpirationTime(),
                chat.getSubscriptionEmbedded().getPurchaseType() == null ? "-" : chat.getSubscriptionEmbedded().getPurchaseType(),
                //----------------------------------------------------------
                getGpt3_5PerDayRequests(chat.getId()),
                getGemini1_5PerDayRequests(chat.getId()),
                getGPT4PerDayRequests(chat.getId()),
                getImagePerDayRequests(chat.getId()),
                chat.getUserBalanceEmbedded().getClaudeTokens(),
                chat.getSubscriptionEmbedded().getSubscriptionInfo().getSongMonthLimit() - getSunoMonthRequests(chat.getId()),
                //----------------------------------------------------------
                chat.getUserBalanceEmbedded().getExtraGPT4Requests(),
                chat.getUserBalanceEmbedded().getExtraImageRequests(),
                chat.getUserBalanceEmbedded().getExtraSunoRequests(),
                //----------------------------------------------------------
                chat.getAiSettingsEmbedded().getAiActiveModel(),
                chat.getAiSettingsEmbedded().getAssistantRole(),
                chat.getAiSettingsEmbedded().getTemperature(),
                chat.getAiSettingsEmbedded().isContextEnabled() ? "✅ Вкл" : "❌ Выкл",
                chat.getAiSettingsEmbedded().isVoiceResponseEnabled() ? "✅ Вкл" : "❌ Выкл",

                getHoursAndMinsOfDayRemaining());
    }

    //TODO
    private int getGpt3_5PerDayRequests(Long chatId) {
        return 0;
    }

    //TODO
    private int getGemini1_5PerDayRequests(Long chatId) {
        return 0;
    }

    //TODO
    private int getGPT4PerDayRequests(Long chatId) {
        return 0;
    }

    //TODO
    private int getImagePerDayRequests(Long chatId) {
        return 0;
    }

    //TODO
    private int getSunoMonthRequests(Long chatId) {
        return 0;
    }

    private String getHoursAndMinsOfDayRemaining() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endOfDay = LocalDateTime.MAX;

        LocalDateTime timeRemaining = endOfDay.minusHours(currentTime.getHour())
                .minusMinutes(currentTime.getMinute())
                .minusSeconds(currentTime.getSecond())
                .minusNanos(currentTime.getNano());

        int hoursRemaining = timeRemaining.getHour();
        int minutesRemaining = timeRemaining.getMinute();

        return String.format("%s ч. %s мин.", hoursRemaining, minutesRemaining);
    }


}
