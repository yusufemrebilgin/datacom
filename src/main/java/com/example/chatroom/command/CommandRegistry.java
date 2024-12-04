package com.example.chatroom.command;

import com.example.chatroom.command.annotation.CommandInfo;
import com.example.chatroom.command.annotation.CommandInfoProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, String> commandInfoCache = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(CommandRegistry.class);

    public void register(Command command) {
        Class<?> commandClass = command.getClass();
        logger.info("Attempting to register command class {}", commandClass);

        if (!commandClass.isAnnotationPresent(CommandInfo.class)) {
            logger.error("Command class {} does not have @CommandInfo annotation.", commandClass);
            throw new IllegalArgumentException("Command class must have @CommandInfo annotation.");
        }

        CommandInfo info = commandClass.getAnnotation(CommandInfo.class);
        commands.put(info.cmd(), command);
        commandInfoCache.put(info.cmd(), CommandInfoProcessor.buildInfoMessage(info));

        logger.info("Command '{}' registered successfully with description: {}", info.cmd(), info.description());
    }

    public Command getCommand(String commandName) {
        logger.info("Fetching command for: {}", commandName);
        Command command = commands.get(commandName);
        if (command != null) {
            logger.debug("Command '{}' retrieved successfully.", commandName);
            return command;
        }

        logger.warn("Command '{}' not found in registry.", commandName);
        return null;
    }

    public String getCommandInfo(String commandName) {
        logger.info("Fetching command info for: {}", commandName);
        if (!commands.containsKey(commandName)) {
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }
        return commandInfoCache.get(commandName);
    }

}
