/**package org.prh.chatapplication.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader in; // To recieve messages from the server
    private PrintWriter out; // To send messages to the server
    private BufferedReader keyboardInput; // To read input from the console

    public ChatClient(String serverAddress, int port) {
        try {
            // Connect to the chat server
            socket = new Socket(serverAddress, port);
            System.out.println("Connected to the chat server!");

            // Set up streams for communication
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            keyboardInput = new BufferedReader(new InputStreamReader(System.in));

            // Start a thread to continuously listen for messages from the server
            new Thread(new IncomingMessageListener()).start();

            String message;
            while (true) {
                message = keyboardInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting chat...");
                    break;
                }
                out.println(message); // Send the message to the server
            }

            // Clean up resources after exiting
            closeConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to close connections
    private void closeConnections() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (keyboardInput != null) keyboardInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle incoming messages from the server
    private class IncomingMessageListener implements Runnable {

        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("FILE:")) {
                        // Handle file reception
                        String[] parts = message.split(":");
                        String fileName = parts[1];
                        long fileSize = Long.parseLong(parts[2]);

                        try (FileOutputStream fos = new FileOutputStream("downloaded_" + fileName)) {
                            byte[] buffer = new byte[4096];
                            long bytesReceived = 0;

                            while (bytesReceived < fileSize) {
                                int bytesRead = socket.getInputStream().read(buffer);
                                fos.write(buffer, 0, bytesRead);
                                bytesReceived += bytesRead;
                            }
                            fos.flush();
                            System.out.println("File downloaded: " + fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Server: " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        new ChatClient("localhost", 8188);
    }
}**/

