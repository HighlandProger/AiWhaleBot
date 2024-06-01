package ru.rusguardian.bot.command.prompts.voice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessPromptText;
import ru.rusguardian.service.process.prompt.ProcessPromptVoice;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.io.File;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteVoicePromptCommand extends PromptCommand {

    private final ProcessPromptVoice processPromptVoice;
    private final ProcessPromptText processPromptText;

    @Override
    public CommandName getType() {
        return CommandName.EXECUTE_VOICE_PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int replyId = sendQuickReply(update);

        Chat chat = getChat(update);
        AIModel model = chat.getAiSettingsEmbedded().getAiActiveModel();
        File voiceFile = FileUtils.getFileFromUpdate(update, bot);

        if (!isChatLimitExpired(chat, model)) {
            processPromptVoice.processVoice2Text(chat, voiceFile).thenAccept(transcription -> {
                processPromptText.process(chat, transcription).thenAccept(response ->{
                    if (!chat.getAiSettingsEmbedded().isVoiceResponseEnabled()) {
                        try {
                            bot.execute(getEditMessageWithResponse(chat.getId(), response, replyId));
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                    processPromptVoice.processText2Voice(chat, response).thenAccept(voiceFileResponse ->{
                        SendVoice voice = SendVoice.builder().voice(new InputFile(voiceFileResponse)).replyToMessageId(replyId).chatId(chat.getId()).build();
                        try {
                            bot.execute(voice);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
            }).exceptionally(e -> {
                log.error(e.getMessage());
                commandContainerService.getCommand(CommandName.ERROR).execute(update);
                //TODO minor correct exceptionally obtain
                return null;
            });
        } else {
            String response;
            if (chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
                response = getTextFromFileByChatLanguage(LIMIT_EXPIRED_FREE, chat);
            } else {
                response = getTextFromFileByChatLanguage(LIMIT_EXPIRED, chat);
            }
            EditMessageText edit = getEditMessageWithResponse(chat.getId(), response, replyId);
            bot.execute(edit);
        }
    }

}
