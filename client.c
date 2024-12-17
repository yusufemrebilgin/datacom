#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <pthread.h>

#define MAX_BUFFER_SIZE 1024
#define SERVER_PORT 8888
#define SERVER_IP "127.0.0.1"

void* receive_messages(void* socket_fd) {
    int sockfd = *((int*)socket_fd);
    char buffer[MAX_BUFFER_SIZE];
    int bytes_received;

    while ((bytes_received = recv(sockfd, buffer, sizeof(buffer) - 1, 0)) > 0) {
        buffer[bytes_received] = '\0';  // Null-terminate the received message
        printf("%s", buffer);
    }

    if (bytes_received == 0) {
        fprintf(stderr, "Server disconnected\n");
    } else {
        perror("Receive error");
    }

    return NULL;
}

int main() {
    int sockfd;
    struct sockaddr_in server_addr;
    char message[MAX_BUFFER_SIZE];
    pthread_t receive_thread;

    // Create socket
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == -1) {
        perror("Socket creation failed");
        exit(1);
    }

    // Configure server address
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(SERVER_PORT);
    if (inet_pton(AF_INET, SERVER_IP, &server_addr.sin_addr) <= 0) {
        perror("Invalid address");
        close(sockfd);
        exit(1);
    }

    // Connect server
    if (connect(sockfd, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("Connection failed");
        close(sockfd);
        exit(1);
    }

    // Start the message receiving thread
    if (pthread_create(&receive_thread, NULL, receive_messages, &sockfd) != 0) {
        perror("Thread creation failed");
        close(sockfd);
        exit(1);
    }

    // Continuously read and send messages from the user
    while (1) {
        // Clear the message buffer
        memset(message, 0, sizeof(message));

        // Get input from the user
        if (fgets(message, sizeof(message), stdin) == NULL) {
            break;
        }

        // Do not send empty messages
        if (strlen(message) == 0) {
            continue;
        }

         // Send the message
        if (send(sockfd, message, strlen(message), 0) < 0) {
            perror("Send failed");
            break;
        }
    }

    // Terminate the thread and close socket
    pthread_cancel(receive_thread);
    pthread_join(receive_thread, NULL);
    close(sockfd);

    return 0;
}