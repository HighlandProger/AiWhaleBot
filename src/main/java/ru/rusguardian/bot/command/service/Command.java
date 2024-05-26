package ru.rusguardian.bot.command.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.commands.ErrorCommand;
import ru.rusguardian.domain.LogEvent;
import ru.rusguardian.domain.Task;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.data.LogEventService;
import ru.rusguardian.service.data.SubscriptionInfoService;
import ru.rusguardian.service.data.TaskService;
import ru.rusguardian.telegram.bot.service.BotService;
import ru.rusguardian.telegram.bot.service.task.TaskCommandService;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.time.LocalDateTime;

@Slf4j
public abstract class Command implements BotService<CommandName> {

    @Value("${telegram.owner.chat.id}")
    private String ownerChatId;
    @Autowired
    protected ErrorCommand errorCommand;

    @Autowired
    @Getter
    protected TelegramLongPollingBot bot;

    @Autowired
    @Getter
    protected ThreadPoolTaskScheduler scheduler;

    @Autowired
    @Lazy
    protected CommandContainerService commandContainerService;

    @Autowired
    protected ChatService chatService;

    @Autowired
    protected SubscriptionInfoService subscriptionInfoService;

    @Autowired
    protected LogEventService logEventService;

    @Autowired
    protected TaskService taskService;

    @Override
    public boolean hasEvent(Update update, CommandName event) {
        return false;
    }

    @Override
    public TaskCommandService<CommandName> getCommand(CommandName commandName) {
        return commandContainerService.getCommand(commandName);
    }

    public abstract CommandName getType();

    protected abstract void mainExecute(Update update) throws TelegramApiException;

    public void execute(Update update) {
        log.debug("Executing command: " + this.getClass().getSimpleName());
        try {
            mainExecute(update);
            addLogEvent(update, this.getType().name());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            if (this.getType() != CommandName.ERROR) errorCommand.execute(update);
            else throw new RuntimeException(e);
        }
    }

    protected Chat getChat(Update update) {
        return chatService.findById(TelegramUtils.getChatId(update));
    }

    protected void addLogEvent(Update update, String event) {
        Long chatId = TelegramUtils.getChatId(update);
        Chat chat = chatService.findById(chatId);
        logEventService.create(new LogEvent(chat, LocalDateTime.now(), event));
    }

    public void createTaskInDatabase(Update update, CommandName commandName, LocalDateTime executeTime, CommandName checkCommandName) {
        taskService.create(new Task(TelegramUtils.getChatId(update), commandName, executeTime, checkCommandName));
    }

    protected void setNextCommand(Update update, CommandName commandName) {
        chatService.updateNextCommand(TelegramUtils.getChatId(update), commandName);
    }

    protected void setNullCompletedCommand(Update update) {
        chatService.updateNextCommand(TelegramUtils.getChatId(update), null);
    }

    protected String getTextFromFileInResources(String fileName) {
        return FileUtils.getTextFromFile(FileUtils.getFileFromResources2(this, fileName));
    }

    protected String getTextFromFileByChatLanguage(String filePath, Chat chat) {
        String path = filePath + chat.getAiSettingsEmbedded().getAiLanguage().getValue() + ".txt";
        return FileUtils.getTextFromFile(FileUtils.getFileFromResources2(this, path));
    }

    protected void edit(EditMessageText editText) throws TelegramApiException {
        editText.setParseMode(ParseMode.HTML);
        bot.execute(editText);
    }

}
