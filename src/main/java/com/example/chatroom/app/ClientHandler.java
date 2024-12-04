package com.example.chatroom.app;

import com.example.chatroom.command.CommandHandler;
import com.example.chatroom.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public record ClientHandler(Socket clientSocket, CommandHandler commandHandler) implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static final String WELCOME_MESSAGE = """
            > Welcome to the chat-room application!
            > Here's how you can get started!:
                - Type `create` to create a new room
                - Usage: /create <room-name> <room-password>
            
                - Type `join` to join an existing room
                - Usage: /join <room-name> <room-password>
            
            > Need more help?
                - Type `/help` to see all commands.
                - Type `/help <command-name>` for details about a specific command.
            """;

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            User client = initializeClient(in, out);

            // Send welcome message to client
            out.println(WELCOME_MESSAGE);
            processMessages(client, in);

        } catch (IOException ex) {
            logger.error("An error occurred at client handler: {}", ex.getMessage(), ex);
        } finally {
            closeSocket();
        }
    }

    private User initializeClient(BufferedReader in, PrintWriter out) throws IOException {
        out.println("> Please enter your username:");
        String username;
        while ((username = in.readLine()) != null) {
            String validationMessage = validateUsername(username);
            if (validationMessage == null) {
                break; // username is valid
            }
            out.println(validationMessage);
            out.println("> Please try again:");
        }

        logger.info("User '{}' connected from {}", username, clientSocket.getRemoteSocketAddress());
        return new User(username, clientSocket);
    }

    private String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "> Username cannot be empty";
        }
        int length = username.length();
        if (length < 3 || length > 8) {
            return "> Username length must be between 3 and 8 characters";
        }
        return null; // No error message, username is valid
    }

    private void processMessages(User client, BufferedReader in) throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            if (message.trim().isEmpty()) continue;
            if (!message.startsWith("/")) {
                client.broadcast(String.format("%s:> %s", client, message));
            } else {
                commandHandler.handle(message, client);
            }
        }
    }

    private void closeSocket() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
                logger.info("Socket closed for {}", clientSocket.getRemoteSocketAddress());
            } catch (IOException ex) {
                logger.error("Failed to close socket: {}", clientSocket, ex);
            }
        }
    }

}
