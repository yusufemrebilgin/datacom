package com.example.chatroom.command;

import com.example.chatroom.command.annotation.CommandArg;
import com.example.chatroom.command.annotation.CommandInfo;
import com.example.chatroom.model.User;

@CommandInfo(
        cmd = "help",
        description = "Displays help information for commands.",
        usage = "/help [command]",
        args = {
                @CommandArg(name = "command", description = "The name of the command to display help for")
        }
)
public class HelpCommand implements Command {

    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(User user, String... args) {
        if (args.length > 1) {
            user.receiveMessage("> Invalid arguments. Usage: /help [command]");
        }

        if (args.length == 0) {
            user.receiveMessage(registry.getCommandInfo("help"));
            return;
        }

        // If command arg given
        String command = args[0];
        user.receiveMessage(registry.getCommandInfo(command));
    }

}
