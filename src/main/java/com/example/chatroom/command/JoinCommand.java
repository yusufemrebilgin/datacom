package com.example.chatroom.command;

import com.example.chatroom.app.ChatRoomManager;
import com.example.chatroom.command.annotation.CommandArg;
import com.example.chatroom.command.annotation.CommandInfo;
import com.example.chatroom.model.ChatRoom;
import com.example.chatroom.model.User;

@CommandInfo(
        cmd = "join",
        description = "Joins an existing chat room.",
        usage = "/join <name> <password>",
        args = {
                @CommandArg(name = "name", description = "The name of the chat room to join"),
                @CommandArg(name = "password", description = "The password for the chat room")
        }
)
public class JoinCommand implements Command {

    private final ChatRoomManager roomManager;

    public JoinCommand(ChatRoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public void execute(User user, String... args) {
        if (args.length < 1) {
            user.receiveMessage("> Invalid usage of /join command. For more details /help join");
            return;
        }

        String roomName = args[0];
        String roomPassword = args.length >= 2 ? args[1] : "";

        ChatRoom room = roomManager.get(roomName);
        if (room == null) {
            user.receiveMessage("> Chat room not found.");
            return;
        }

        room.join(roomPassword, user);
    }

}
