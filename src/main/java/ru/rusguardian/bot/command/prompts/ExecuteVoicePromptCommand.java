package ru.rusguardian.bot.command.prompts;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.service.ai.AiService;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExecuteVoicePromptCommand extends Command {

    private final AiService chatClientService;

    @Override
    public CommandName getType() {
        return CommandName.EXECUTE_VOICE_PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SendMessage reply = SendMessageUtil.getSimple(update, "Ответ обрабатывается");
        reply.setReplyToMessageId(TelegramUtils.getMessageId(update));
        int replyId = bot.execute(reply).getMessageId();

        AISettingsEmbedded settings = chatService.findById(TelegramUtils.getChatId(update)).getAiSettingsEmbedded();

        //TODO
        String voiceTranscription = chatClientService.getSpeechToText(FileUtils.getFileFromUpdate(update, bot), settings.getAiLanguage(), settings.getTemperature());

        //TODO refactor
        String response = chatClientService.getTextPromptResponse(List.of(new OpenAiApi.ChatCompletionMessage(voiceTranscription, OpenAiApi.ChatCompletionMessage.Role.USER)), settings.getAiActiveModel(), settings.getTemperature()).choices().get(0).message().content();

        editMessageWithMarkdown(update, response, replyId);
    }


}
