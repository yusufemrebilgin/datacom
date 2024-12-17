package com.example.chatroom.app;

import com.example.chatroom.model.ChatRoom;
import com.example.chatroom.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomManager.class);

    private final ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    public ChatRoom create(String name, String password, User admin) {
        logger.info("Attempting to create a chat room: {}", name);
        if (chatRooms.containsKey(name)) {
            logger.error("Failed to create chat room. A room with the name '{}' already exists.", name);
            throw new IllegalArgumentException("A given room name is already exists");
        }

        ChatRoom room = new ChatRoom(name, password);
        chatRooms.put(name, room);

        logger.info("Chat room '{}' created successfully by admin: {}", name, admin);
        return room;
    }

    public ChatRoom get(String roomName) {
        ChatRoom room = chatRooms.get(roomName);
        if (room != null) {
            logger.debug("Chat room '{}' retrieved successfully.", roomName);
            return room;
        }

        logger.warn("Chat room '{}' not found.", roomName);
        return null;
    }

    public void remove(String roomName) {
        logger.info("Attempting to remove chat room: {}", roomName);
        Optional.ofNullable(chatRooms.remove(roomName)).ifPresentOrElse(
                removedRoom -> logger.info("Chat room '{}' removed successfully.", roomName),
                () -> logger.warn("Failed to remove chat room. Room '{}' does not exist.", roomName)
        );
    }

}
