package com.example.chatroom.command;

import com.example.chatroom.app.ChatRoomManager;
import com.example.chatroom.command.annotation.CommandArg;
import com.example.chatroom.command.annotation.CommandInfo;
import com.example.chatroom.command.annotation.CommandInfoProcessor;
import com.example.chatroom.model.ChatRoom;
import com.example.chatroom.model.User;

import java.util.Map;

@CommandInfo(
        cmd = "create",
        description = "Creates a new chat room.",
        usage = "/create <name> <password>",
        args = {
                @CommandArg(name = "name", description = "The name of the chat room"),
                @CommandArg(name = "password", description = "The password for the chat room")
        }
)
public class CreateCommand implements Command {

    private final ChatRoomManager chatRoomManager;

    public CreateCommand(ChatRoomManager chatRoomManager) {
        this.chatRoomManager = chatRoomManager;
    }

    @Override
    public void execute(User user, String... args) {
        if (args.length < 2) {
            user.receiveMessage("> Invalid usage of /create command. For more details /help create");
            return;
        }

        Map<String, String> m = CommandInfoProcessor.getCommandInfoArgs(this, args);
        String name = m.get("name");
        String password = m.get("password");

        try {
            ChatRoom room = chatRoomManager.create(name, password, user);
            room.join(password, user);
        } catch (IllegalArgumentException ex) {
            user.receiveMessage(String.format("> Failed to create room: %s", ex.getMessage()));
        }
    }

}


