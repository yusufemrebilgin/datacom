package com.example.chatroom.command;

import com.example.chatroom.model.User;

@FunctionalInterface
public interface Command {

    void execute(User user, String... args);

}
