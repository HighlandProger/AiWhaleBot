package ru.rusguardian.service.data;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandContainerService;
import ru.rusguardian.domain.Task;
import ru.rusguardian.repository.TaskRepository;
import ru.rusguardian.telegram.bot.service.task.domain.CommandTask;
import ru.rusguardian.telegram.bot.util.util.DateTimeUtils;
import ru.rusguardian.telegram.bot.util.util.UpdateUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final ThreadPoolTaskScheduler scheduler;
    @Lazy
    @Autowired
    private CommandContainerService commandContainerService;

    @PostConstruct
    private void init() {
        log.info("INITIALIZING TASKS");
        List<Task> tasksToDelete = new ArrayList<>();
        for (Task task : findAll()) {
            if (task.getExecuteTime().isAfter(LocalDateTime.now())) {
                log.info("Planning task {}", task);

                Command commandToExecute = commandContainerService.getCommand(task.getCommandToExecute());
                Command commandToCheck = commandContainerService.getCommand(task.getCommandToCheck());
                Update update = UpdateUtils.getMockUpdate(task.getChatId(), null);
                Date time = DateTimeUtils.convertToDate(task.getExecuteTime());

                scheduler.schedule(new CommandTask<>(commandToExecute, commandToCheck, update), time);
            } else {
                tasksToDelete.add(task);
            }
        }
        repository.deleteAll(tasksToDelete);
    }

    public Task create(Task task) {
        return repository.save(task);
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

}
