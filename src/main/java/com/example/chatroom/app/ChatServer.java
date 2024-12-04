package com.example.chatroom.app;

import com.example.chatroom.command.CommandHandler;
import com.example.chatroom.command.CommandRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private final CommandRegistry registry;
    private final ChatRoomManager roomManager;
    private final CommandHandler commandHandler;
    private final ExecutorService executorService;

    private final int port;
    private static final int DEFAULT_PORT = 8888;
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;

    private final Set<Socket> activeClients = Collections.synchronizedSet(new HashSet<>());

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public ChatServer() {
        this(DEFAULT_PORT);
    }

    public ChatServer(int port) {
        this(port, DEFAULT_THREAD_POOL_SIZE);
    }

    public ChatServer(int port, int threadPoolSize) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        this.port = port;
        registry = new CommandRegistry();
        roomManager = new ChatRoomManager();
        commandHandler = new CommandHandler(registry);
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        registerCommands();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start() {
        logger.info("Starting server on port {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server is up and running on port {}", port);
            logger.info("Waiting for clients to connect...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected with address {}", clientSocket.getRemoteSocketAddress());
                activeClients.add(clientSocket);
                executorService.execute(new ClientHandler(clientSocket, commandHandler));
            }
        } catch (IOException ex) {
            logger.error("An error occurred at server side: {}", ex.getMessage(), ex);
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        logger.info("Shutting down the server...");

        synchronized (activeClients) {
            for (Socket clientSocket : activeClients) {
                try {
                    clientSocket.close();
                    logger.info("Closed connection with client: {}", clientSocket.getRemoteSocketAddress());
                } catch (IOException ex) {
                    logger.error("Failed to close client connection: {}", ex.getMessage(), ex);
                }
            }
            activeClients.clear();
        }

        executorService.shutdownNow();
        logger.info("Server shutdown complete.");
    }

    private void registerCommands() {
        // todo: register all commands
    }

}
