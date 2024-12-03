// ClientHandler.java
/**package org.prh.chatapplication.client;

import lombok.Getter;
import org.prh.chatapplication.server.ChatServer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

// ClientHandler class to handle each client in a separate thread
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final PrintWriter out;
    private final BufferedReader in;
    @Getter
    private String username;
    private final Set<String> groups = new HashSet<>();

    public ClientHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            // Ask for the client's username
            out.println("Enter your username:");
            username = in.readLine();
            System.out.println(username + " has joined the chat.");

            // Add the new client to the server's list
            ChatServer.addClient(username, this);

            ChatServer.broadcastMessage(username + " has joined the chat", this);

            ChatServer.sendPublicHistory(this);

            // Read and process messages sent by the client
            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleMessage(String message) {
        if (message.startsWith("/pm")) {
            handlePrivateMessage(message);
        } else if (message.startsWith("/creategroup")) {
            handleCreateGroup(message);
        } else if (message.startsWith("/joingroup")) {
            handleJoinGroup(message);
        } else if (message.startsWith("/leavegroup")) {
            handleLeaveGroup(message);
        } else if (message.startsWith("/groups")) {
            out.println(ChatServer.listActiveGroups());
        } else if (message.startsWith("group")) {
            handleGroupMessage(message);
        } else if (message.startsWith("/public")) {
            ChatServer.broadcastMessage(username + ": " + message.substring(8), this);
        } else {
            out.println("Invalid command. Use /pm, /public, /creategroup, /joingroup, /leavegroup, /groups, /group.");
        }
    }

    private void handlePrivateMessage(String message) {
        String[] splitMessage = message.split(" ", 3);
        if (splitMessage.length >= 3) {
            String recipientUsername = splitMessage[1];
            String privateMessage = splitMessage[2];
            ChatServer.sendPrivateMessage(recipientUsername, privateMessage, this);
        } else {
            out.println("Invalid private message format. Use: /pm <username> <message>");
        }
    }

    private void handleCreateGroup(String message) {
        String[] splitMessage = message.split(" ", 2);
        if (splitMessage.length == 2) {
            ChatServer.createGroup(splitMessage[1], this);
        } else {
            out.println("Invalid group creation format. Use: /creategroup <groupname>");
        }
    }

    private void handleJoinGroup(String message) {
        String[] splitMessage = message.split(" ", 2);
        if (splitMessage.length == 2) {
            ChatServer.joinGroup(splitMessage[1], this);
        } else {
            out.println("Invalid group join format. Use: /joingroup <groupname>");
        }
    }

    private void handleLeaveGroup(String message) {
        String[] splitMessage = message.split(" ", 2);
        if (splitMessage.length == 2) {
            ChatServer.leaveGroup(splitMessage[1], this);
        } else {
            out.println("Invalid group leave format. Use: /leavegroup <groupname>");
        }
    }

    private void handleGroupMessage(String message) {
        String[] splitMessage = message.split(" ", 3);
        if (splitMessage.length >= 3) {
            String groupName = splitMessage[1];
            String groupMessage = splitMessage[2];
            if (groups.contains(groupName)) {
                ChatServer.broadcastGroupMessage(groupName, username + ": " + groupMessage);
            } else {
                out.println("You are not part of the group: " + groupName);
            }
        } else {
            out.println("Invalid group message format. Use: group <groupname> <message>");
        }
    }

    private void cleanup() {
        ChatServer.removeClient(this);
        ChatServer.broadcastMessage(username + " has left the chat.", this);
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }

    public void sendFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // Send file metadata
            out.println("FILE:" + file.getName() + ":" + file.length());

            // Send file data
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                clientSocket.getOutputStream().write(buffer, 0, bytesRead);
            }
            clientSocket.getOutputStream().flush();
            System.out.println("File sent successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message to this client
    public void sendMessage(String message) {
        out.println(message);
    }

    public void addGroup(String groupName) {
        groups.add(groupName);
    }

    public void removeGroup(String groupName) {
        groups.remove(groupName);
    }
}**/