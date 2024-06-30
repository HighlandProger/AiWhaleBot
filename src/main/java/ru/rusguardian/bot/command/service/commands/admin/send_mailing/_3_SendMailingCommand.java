package ru.rusguardian.bot.command.service.commands.admin.send_mailing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramMessageUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class _3_SendMailingCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.SEND_MAILING;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Message replyMessage = ((Message)update.getCallbackQuery().getMessage()).getReplyToMessage();

        CompletableFuture.runAsync(() -> {
            AtomicInteger sendCount = new AtomicInteger();
            AtomicInteger notFoundCount = new AtomicInteger();
            List<Long> chatIds = chatService.getAllNotKickedChatIds();
            LocalDateTime startTime = LocalDateTime.now();
            for (Long chatId : chatIds) {
                try {
                    TelegramMessageUtils.forwardMessage(bot, replyMessage, String.valueOf(chatId));
                    sendCount.incrementAndGet();
                } catch (TelegramApiException e) {
                    log.warn(e.getMessage());
                    if (e.getMessage().contains("Forbidden: bot was blocked by the user")) {
                        chatService.setChatKicked(chatId);
                        log.info("Kicked id:" + chatId);
                    }
                    if (e.getMessage().contains("Bad Request: chat not found")){
                        log.warn("Not connect with chat id: {}", chatId);
                        notFoundCount.incrementAndGet();
                    }
                }
            }
            String text = getReportPattern(startTime, sendCount, chatIds.size());
            SendMessage message = SendMessage.builder()
                    .chatId(TelegramUtils.getChatId(update))
                    .text(text)
                    .build();

            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                log.info(text);
                throw new RuntimeException(e);
            }
        });
    }

    private String getReportPattern(LocalDateTime startTime, AtomicInteger sendCount, int allSize) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String pattern = """
                Отчет по рассылке:
                                
                Старт: {0}
                Конец: {1}
                Отправлено пользователям: {2}
                Общее количество в базе: {3}
                """;

        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = LocalDateTime.now().format(formatter);
        return MessageFormat.format(pattern, formattedStartTime, formattedEndTime, sendCount.get(), allSize);
    }
}
