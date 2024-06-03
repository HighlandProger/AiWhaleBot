package ru.rusguardian.bot.command.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandDispatcher {

    private final List<Command> commands;
    private final Map<String, CommandName> viewEqualsMap = new HashMap<>();
    private final Map<String, CommandName> viewStartsWithMap = new HashMap<>();
    private final Map<String, CommandName> blindEqualsMap = new HashMap<>();
    private final Map<String, CommandName> blindStartsWithMap = new HashMap<>();

    @PostConstruct
    private void init() {
        for (Command command : commands) {
            CommandMapping commandMapping = AopProxyUtils.ultimateTargetClass(command).getAnnotation(CommandMapping.class);
            initView(commandMapping, command);
            initBlind(commandMapping, command);
        }
    }

    private void initView(CommandMapping commandMapping, Command command) {
        if (commandMapping == null) return;
        for (String viewText : commandMapping.viewCommands()) {
            if (commandMapping.isViewVariable()) {
                viewStartsWithMap.put(viewText, command.getType());
            } else {
                viewEqualsMap.put(viewText, command.getType());
            }
        }
    }

    private void initBlind(CommandMapping commandMapping, Command command) {
        String blindText = commandMapping == null || commandMapping.blindCommand().isEmpty() ? command.getType().name() : commandMapping.blindCommand();
        if (blindText.endsWith("BLIND_D")) {
            blindStartsWithMap.put(blindText, command.getType());
        } else {
            blindEqualsMap.put(blindText, command.getType());
        }
    }

    public Optional<CommandName> getByViewVariable(String view) {
        for (String value : viewStartsWithMap.keySet()) {
            if (view.startsWith(value)) {
                return Optional.of(viewStartsWithMap.get(value));
            }
        }
        return Optional.empty();
    }

    public Optional<CommandName> getByViewStatic(String view) {
        if (viewEqualsMap.containsKey(view)) {
            return Optional.of(viewEqualsMap.get(view));
        }
        return Optional.empty();
    }

    public Optional<CommandName> getByBlindVariable(String blind) {
        for (String value : blindStartsWithMap.keySet()) {
            if (blind.startsWith(value)) {
                return Optional.of(blindStartsWithMap.get(value));
            }
        }
        return Optional.empty();
    }

    public Optional<CommandName> getByBlindStatic(String blind) {
        if (blindEqualsMap.containsKey(blind)) {
            return Optional.of(blindEqualsMap.get(blind));
        }
        return Optional.empty();
    }
}
