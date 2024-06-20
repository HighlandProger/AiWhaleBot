package ru.rusguardian.bot.command.prompts.image.img;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.service.data.ViewDataService;
import ru.rusguardian.telegram.bot.service.message.MessageService;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface SendPromptImageService extends MessageService {

    String VIEW_DATA = "IMAGE_PREPARING";

    default void sendPromptImage(String url, String chatId, int messageToReplyId, TelegramLongPollingBot bot) {
        SendPhoto photo = new SendPhoto(chatId, new InputFile(url));
        photo.setReplyToMessageId(messageToReplyId);
        try {
            bot.execute(photo);
        } catch (TelegramApiException e) {
            if (e instanceof TelegramApiRequestException ex && ex.getErrorCode() == 400) {
                System.out.println("ERROR DURING SEND BY URL");
                File file = FileUtils.getFileFromURL(url);
                try {
                    bot.execute(new SendDocument(photo.getChatId(), new InputFile(file)));
                } catch (TelegramApiException exc) {
                    System.out.println("ERROR DURING SEND BY DOCUMENT");
                    sendPhoto(new SendPhoto(photo.getChatId(), new InputFile(file)));
                }
            }
        }
    }

    default Message sendReplyToImageRequest(Update update, AILanguage language, ViewDataService viewDataService, TelegramLongPollingBot bot) {
        SendMessage message = SendMessageUtil.getSimple(update, viewDataService.getViewByNameAndLanguage(VIEW_DATA, language));
        message.setReplyToMessageId(TelegramUtils.getMessageId(update));
        try {
            return bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    default int editReplyToImageRequest(Update update, AILanguage language, ViewDataService viewDataService, TelegramLongPollingBot bot) {
        EditMessageText edit = EditMessageUtil.getMessageText(update, viewDataService.getViewByNameAndLanguage(VIEW_DATA, language));
        try {
            bot.execute(edit);
            return TelegramUtils.getMessageId(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    default CompletableFuture<Void> sendTextToImagePrompt(SendPhoto sendPhoto) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendPhoto(sendPhoto);
        }).exceptionally(e -> {
            throw new RuntimeException(e);
        });
    }

}
