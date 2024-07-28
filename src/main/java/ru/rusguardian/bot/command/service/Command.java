package ru.rusguardian.bot.command.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.commands.ErrorCommand;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.LogEvent;
import ru.rusguardian.domain.Task;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.*;
import ru.rusguardian.telegram.bot.service.BotService;
import ru.rusguardian.telegram.bot.service.task.TaskCommandService;
import ru.rusguardian.telegram.bot.util.constants.ChatType;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.rusguardian.bot.command.service.ProcessUpdateService.ASK_COMMAND;

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
    protected SubscriptionService subscriptionService;

    @Autowired
    protected LogEventService logEventService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected ViewDataService viewDataService;

    @Autowired
    protected AssistantRoleDataService assistantRoleDataService;

    @Autowired
    protected ButtonViewDataService buttonViewDataService;
    @Autowired
    protected UserSubscriptionService userSubscriptionService;

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
            if (this.getType() == CommandName.EMPTY) {
                return;
            }
            addLogEvent(update, this.getType().name());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            if (this.getType() != CommandName.ERROR) errorCommand.execute(update);
            else throw new RuntimeException(e);
        }
    }

    protected Chat getChatOwner(Update update) {
        ChatType chatType = TelegramUtils.getChatType(update);
        if (chatType == ChatType.PRIVATE) {
            return chatService.findById(TelegramUtils.getChatId(update));
        }

        Long chatOwnerId = TelegramUtils.getChatOwnerId(TelegramUtils.getChatIdString(update), bot);
        Optional<Chat> chatOwner = chatService.findByIdOptional(chatOwnerId);
        if (chatOwner.isEmpty()) {
            sendChatOwnerNotFoundErrorMessage(TelegramUtils.getChatIdString(update));
        }
        return chatOwner.orElseThrow();
    }

    private void sendChatOwnerNotFoundErrorMessage(String chatId) {
        SendMessage message = SendMessage.builder().chatId(chatId).text("Владелец группы не зарегистирован в боте").build();
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addLogEvent(Update update, String event) {
        Chat chat = getChatOwner(update);
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

    protected String getTextByViewDataAndChatLanguage(String viewDataName, AILanguage language) {
        return viewDataService.getViewByNameAndLanguage(viewDataName, language);
    }

    protected ReplyKeyboard getMainKeyboard(AILanguage language) {

        List<String> buttons = buttonViewDataService.getByNameAndLanguage(CommandName.WELCOME.name(), language);

        return ReplyMarkupUtil.getReplyKeyboard(List.of(
                List.of(buttons.get(0), buttons.get(1)),
                List.of(buttons.get(2), buttons.get(3))
        ));
    }

    protected String getViewTextMessage(Update update) {
        String viewText = TelegramUtils.getViewTextMessage(update).orElseThrow();
        if (TelegramUtils.getChatType(update) == ChatType.PRIVATE) {
            return viewText;
        }
        if (!viewText.startsWith(ASK_COMMAND)) {
            throw new RuntimeException("Сюда не должно было дойти. Фильтр в ProcessUpdate");
        }
        return viewText.substring(ASK_COMMAND.length()).trim();
    }

    protected Long getInitialChatId(Update update) {
        return TelegramUtils.getChatId(update);
    }

    protected void edit(EditMessageText edit) throws TelegramApiException {
        edit.setParseMode(ParseMode.HTML);
        bot.execute(edit);
    }

    protected int editOrSend(Update update, String text, InlineKeyboardMarkup keyboard) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            return editMessage(update, text, keyboard);
        }
        return sendMessage(update, text, keyboard).getMessageId();
    }

    protected AILanguage getChatLanguage(Update update) {
        return chatService.getChatLanguage(TelegramUtils.getChatId(update));
    }
}
