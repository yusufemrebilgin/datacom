package com.example.chatroom.command;

import com.example.chatroom.app.ChatRoomManager;
import com.example.chatroom.command.annotation.CommandInfo;
import com.example.chatroom.model.ChatRoom;
import com.example.chatroom.model.User;

@CommandInfo(
        cmd = "leave",
        description = "Leaves the current chat room.",
        usage = "/leave"
)
public class LeaveCommand implements Command {

    private final ChatRoomManager chatRoomManager;

    public LeaveCommand(ChatRoomManager chatRoomManager) {
        this.chatRoomManager = chatRoomManager;
    }

    @Override
    public void execute(User user, String... args) {
        if (args.length != 0) {
            user.receiveMessage("> Invalid usage of /leave command. For more details /help leave");
            return;
        }

        ChatRoom currentRoom = user.getCurrentRoom();
        String currentRoomName = currentRoom.getName();

        currentRoom.leave(user);
        if (currentRoom.isEmpty()) {
            chatRoomManager.remove(currentRoomName);
        }
    }
}
