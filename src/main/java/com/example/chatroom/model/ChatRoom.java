package com.example.chatroom.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {

    private final String name;
    private final String password;

    private final User admin;
    private final Set<User> users;

    private static final Logger logger = LoggerFactory.getLogger(ChatRoom.class);

    public ChatRoom(String name, String password, User admin) {
        this.name = name.trim();
        this.password = password.trim();
        this.admin = admin;
        users = ConcurrentHashMap.newKeySet();
    }

    public String getName() {
        return name;
    }

    public void join(String password, User user) {
        if (password == null || !password.equals(this.password)) {
            logger.warn("User '{}' failed to join room '{}': Incorrect password", user.getUsername(), name);
            user.receiveMessage("> Incorrect password");
            return;
        }

        if (users.add(user)) {
            user.setCurrentRoom(this);
            broadcast(String.format("> %s has joined the chat!", user.getUsername()), user);
            user.receiveMessage("> Joined room successfully!");
            logger.info("User '{}' joined to '{}'", user.getUsername(), name);
        } else {
            logger.warn("User '{}' is already in room '{}'", user.getUsername(), name);
        }
    }

    public void leave(User user) {
        if (user == null || !users.contains(user)) {
            logger.warn("User '{}' attempted to leave room '{}' but was not present", user, name);
            return;
        }
        users.remove(user);
        user.setCurrentRoom(null);
        broadcast(String.format("> %s has left the chat!", user.getUsername()), user);
        logger.info("User '{}' left from '{}'", user, name);
        user.receiveMessage("> You've left the current chat.");
    }

    public void broadcast(String message, User sender) {
        users.forEach(user -> {
            if (!user.equals(sender)) {
                user.receiveMessage(message);
            }
        });
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

}
