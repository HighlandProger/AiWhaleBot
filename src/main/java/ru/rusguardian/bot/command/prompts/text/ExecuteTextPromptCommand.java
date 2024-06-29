package ru.rusguardian.bot.command.prompts.text;

import io.netty.handler.codec.UnsupportedMessageTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessPromptText;
import ru.rusguardian.service.process.prompt.ProcessPromptVoice;
import ru.rusguardian.telegram.bot.util.constants.MessageType;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteTextPromptCommand extends PromptCommand {

    private final ProcessPromptText processPromptText;
    private final ProcessPromptVoice processPromptVoice;
    private static final String VIEW_DATA = "TRANSCRIPTION_VIEW_DATA";

    @Override
    public CommandName getType() {
        return CommandName.EXECUTE_TEXT_PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int replyId = sendQuickReply(update);

        Chat chatOwner = getChatOwner(update);
        Long initialChatId = getInitialChatId(update);
        AIModel model = chatOwner.getAiSettingsEmbedded().getAiActiveModel();

        if (isChatLimitExpired(chatOwner, model)) {
            edit(getEditMessageWithResponse(initialChatId, getChatLimitExpiredString(chatOwner), replyId));
            return;
        }

        try {
            String prompt = extractPrompt(update, chatOwner, initialChatId, replyId);
            processPromptText(update, chatOwner, prompt, initialChatId, replyId);
        } catch (UnsupportedMessageTypeException e) {
            handleUnsupportedMessageType(update, e);
        }
    }

    private String extractPrompt(Update update, Chat chatOwner, Long initialChatId, int replyId) throws UnsupportedMessageTypeException {
        switch (MessageType.getType(update)) {
            case TEXT:
                return getViewTextMessage(update);
            case VOICE:
                return processPromptVoice.processVoice2Text(chatOwner, FileUtils.getFileFromMessage(update.getMessage(), bot))
                        .thenApply(transcription -> {
                            sendMessagePrompt(SendMessage.builder()
                                    .chatId(initialChatId)
                                    .text(getTranscriptionViewData(chatOwner.getAiSettingsEmbedded().getAiLanguage(), transcription))
                                    .replyToMessageId(replyId - 1)
                                    .parseMode(ParseMode.HTML)
                                    .build());
                            return transcription;
                        }).join();
            default:
                throw new UnsupportedMessageTypeException(update.toString());
        }
    }

    private void processPromptText(Update update, Chat chatOwner, String prompt, Long initialChatId, int replyId) {
        processPromptText.process(chatOwner, prompt).thenAccept(response -> {
            if (!chatOwner.getAiSettingsEmbedded().isVoiceResponseEnabled()) {
                editForPrompt(getEditMessageWithResponse(initialChatId, response, replyId));
            } else {
                processVoiceResponse(chatOwner, response, initialChatId, replyId);
            }
        }).exceptionally(e -> {
            log.error(e.getMessage());
            handleProcessingException(update);
            throw new RuntimeException("EXCEPTION DURING FUTURE", e);
        });
    }

    private void processVoiceResponse(Chat chatOwner, String response, Long initialChatId, int replyId) {
        processPromptVoice.processText2Voice(chatOwner, response)
                .thenAccept(voiceResponse -> sendVoice(SendVoice.builder()
                        .voice(new InputFile(voiceResponse))
                        .caption(trimCaption(response))
                        .chatId(initialChatId)
                        .replyToMessageId(replyId - 1)
                        .build()));
    }

    private void handleUnsupportedMessageType(Update update, UnsupportedMessageTypeException e) {
        log.error("Unsupported message type: {}", e.getMessage());
        commandContainerService.getCommand(CommandName.ERROR).execute(update);
    }

    private void handleProcessingException(Update update) {
        commandContainerService.getCommand(CommandName.ERROR).execute(update);
    }

    private String getTranscriptionViewData(AILanguage language, String transcription) {
        String viewDataPattern = viewDataService.getViewByNameAndLanguage(VIEW_DATA, language);
        return MessageFormat.format(viewDataPattern, transcription);
    }

    private String trimCaption(String text) {
        return text.substring(0, Math.min(text.length(), 1024));
    }
}