package com.example.chatroom.command;

import com.example.chatroom.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class CommandHandler {

    private final CommandRegistry registry;

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    public CommandHandler(CommandRegistry registry) {
        this.registry = registry;
    }

    public void handle(String commandText, User user) {

        String[] parts = parseCommand(commandText);
        if (parts == null || parts.length == 0) {
            user.receiveMessage("> Command cannot be empty");
            logger.warn("Received an empty command from user '{}'", user.getUsername());
            return;
        }

        String command = parts[0].substring(1);
        String[] args  = Arrays.copyOfRange(parts, 1, parts.length);
        Optional.ofNullable(registry.getCommand(command)).ifPresentOrElse(
                cmd -> cmd.execute(user, args),
                () -> handleUnknownCommand(command, user)
        );
    }

    private String[] parseCommand(String commandText) {
        try {
            return commandText.split("\\s++");
        } catch (Exception ex) {
            logger.error("Error while parsing command: '{}'", commandText, ex);
            return null;
        }
    }

    private void handleUnknownCommand(String command, User user) {
        user.receiveMessage("> Unknown command: " + command);
        logger.warn("Unknown command '{}' received from user '{}'", command, user.getUsername());
    }

}
