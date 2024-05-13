package ru.rusguardian.bot.command.prompts;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

@Component
@RequiredArgsConstructor
public class ExecuteVoicePromptCommand extends Command {

//    private final AiService chatClientService;

    @Override
    public CommandName getType() {
        return CommandName.EXECUTE_VOICE_PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SendMessage reply = SendMessageUtil.getSimple(update, "Ответ обрабатывается");
        reply.setReplyToMessageId(TelegramUtils.getMessageId(update));
        int replyId = bot.execute(reply).getMessageId();

//        String voiceTranscription = chatClientService.getVoiceTranscription(FileUtils.getFileFromUpdate(update, bot));

//        String response = chatClientService.getTextPromptResponse(voiceTranscription);

        editMessageWithMarkdown(update, "response", replyId);
    }


}
