package com.example.chatroom.command.annotation;

import com.example.chatroom.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandInfoProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CommandInfoProcessor.class);

    public static CommandInfo getCommandInfo(Command command) {
        if (command == null) {
            logger.error("Null command provided to getCommandInfo");
            throw new IllegalArgumentException("Command is required");
        }
        Class<?> commandClass = command.getClass();
        if (!commandClass.isAnnotationPresent(CommandInfo.class)) {
            logger.error("Class {} does not have @CommandInfo annotation.", commandClass.getName());
            throw new IllegalArgumentException("@CommandInfo not defined for class " + commandClass.getName());
        }

        return commandClass.getAnnotation(CommandInfo.class);
    }

    public static Map<String, String> getCommandInfoArgs(Command command, String[] args) {
        if (command == null) {
            logger.error("Null command provided to getCommandInfoArgs");
            throw new IllegalArgumentException("Command is required");
        }


        Map<String, String> argMap = new HashMap<>();
        CommandArg[] commandArgs = getCommandInfo(command).args();
        logger.debug("Extracting command arguments for: {}", command.getClass().getName());
        for (int i = 0; i < commandArgs.length; i++) {
            String arg = commandArgs[i].name();
            String val = (i < args.length) ? args[i] : "";
            argMap.put(arg, val);
            logger.debug("Mapped argument '{}' to value '{}'.", arg, val);
        }

        return argMap;
    }

    public static String buildInfoMessage(CommandInfo info) {
        logger.info("Building info message for command: {}", info.cmd());

        StringBuilder builder = new StringBuilder();
        builder.append("> CMD\n\t").append(info.cmd()).append(": ").append(info.description()).append("\n");
        builder.append("> USAGE\n\t").append(info.usage()).append("\n");

        CommandArg[] args = info.args();
        if (args.length > 0) {
            builder.append("> DETAILS\n");
            for (CommandArg arg : args) {
                builder.append("\t");
                builder.append(String.format("%-25s %s\n", arg.name(), arg.description()));
            }
        }

        logger.debug("Info message built for command: {}", info.cmd());
        return builder.toString();
    }

}
