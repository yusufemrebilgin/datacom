package com.example.chatroom.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;

public final class User {

    private final String username;
    private ChatRoom currentRoom;

    // Network field
    private final Socket socket;

    public User(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public ChatRoom getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(ChatRoom currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void receiveMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException ex) {
            System.err.printf(
                    "[%s] - An error occurred during message sending at User class: %s\n",
                    Thread.currentThread().getName(),
                    ex.getMessage()
            );
        }
    }

    public void broadcast(String message) {
        Optional.ofNullable(currentRoom).ifPresent(room -> room.broadcast(message, this));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    @Override
    public String toString() {
        return username != null ? username : "anonymous";
    }

}
