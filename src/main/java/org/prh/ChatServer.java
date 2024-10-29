package org.prh;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {

    // Stores all active client handlers (threads)
    private static Map<String,ClientHandler> clientHandlers = new HashMap<>();
    private static Map<String,List<ClientHandler>> groupHandlers = new HashMap<>();
    private static final int PORT = 8188;

    // Stores the last 20 messages
    private static List<String> messageHistory = new ArrayList<>();
    private static final int HISTORY_LIMIT = 20;

    public static void main(String[] args){
        System.out.println("Chat Server started...");

        // Start the server socket
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");

                // Create a new ClientHandler for each connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket);



                // Start the new client's thread
                new Thread(clientHandler).start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Method to broadcast messages to all clients
    public static synchronized void broadcastMessage(String message,ClientHandler sender){

        // Add the message to the history, ensuring it doesn't exceed the limit
        if(messageHistory.size()==HISTORY_LIMIT){
            messageHistory.remove(0);
        }
        messageHistory.add(message);

        for(ClientHandler clientHandler:clientHandlers.values()){
            // Do not send the message back to the sender
            if(clientHandler.getGroupName()==null ){
                clientHandler.sendMessage(message);
            }
        }
    }

    // Method to send the message history to  a newly connected client
    public static synchronized  void sendHistory(ClientHandler clientHandler){
        for(String message: messageHistory){
            clientHandler.sendMessage("[History] "+message);
        }
    }

    // Method to handle private messaging
    public static synchronized void sendPrivateMessage(String recipientUsername,String message, ClientHandler sender){
        ClientHandler recipient = clientHandlers.get(recipientUsername);
        if(recipient!=null){
            recipient.sendMessage("Private from "+sender.getUsername()+": "+message);
            sender.sendMessage("Private to "+recipientUsername+"; "+message);
        }
        else{
            sender.sendMessage("User: "+recipientUsername+ " not found.");
        }
    }

    //
    public static synchronized  void addClient(String username, ClientHandler clientHandler){
        clientHandlers.put(username,clientHandler);
        //System.out.println(username+ "added to the chat.");
    }

    // Method to get a list of all active users
    public static synchronized String getActiveUsers(){
        StringBuilder userList = new StringBuilder();
        for(String username : clientHandlers.keySet()){
            userList.append(username).append(", ");
        }
        if(userList.length()>0){
            userList.setLength(userList.length()-2); // Remove the last comma and space
        }
        return userList.toString();
    }


    // Method to remove a client from the set when they disconnect
    public static synchronized  void removeClient(ClientHandler clientHandler){
        clientHandlers.remove(clientHandler.getUsername());
        System.out.println("Client "+ clientHandler.getUsername() +" disconnected");
    }

    //Method to create a group
    public static synchronized  void createGroup(String groupName, ClientHandler creater){
        if(groupHandlers.containsKey(groupName)){
            creater.sendMessage("Group "+groupName+" already exists.");
        }
        else{
            groupHandlers.put(groupName,new ArrayList<>());
            groupHandlers.get(groupName).add(creater);
            creater.sendMessage("Group "+groupName+" created.");
        }
    }

    //Method to join a group
    public static synchronized void joinGroup(String groupName, ClientHandler clientHandler){
        if(groupHandlers.containsKey(groupName)){
            groupHandlers.get(groupName).add(clientHandler);
            clientHandler.setGroupName(groupName);
            broadcastGroupMessage(groupName,clientHandler.getUsername()+" joined the group.");
        }
        else{
            clientHandler.sendMessage("Group "+groupName+" does not exist.");
        }
    }

    //Method to leave a group
    public static synchronized  void leaveGroup(String groupName, ClientHandler clientHandler){
        if(groupHandlers.containsKey(groupName)){
            groupHandlers.get(groupName).remove(clientHandler);
            clientHandler.setGroupName(null);
            broadcastGroupMessage(groupName,clientHandler.getUsername()+" left the group.");
        }
        else{
            clientHandler.sendMessage("You are not a member of group "+groupName);
        }
    }

    // Method to send a message to a specific group
    public static synchronized void broadcastGroupMessage(String groupName, String message){
        List<ClientHandler> groupMembers = groupHandlers.get(groupName);
        if(groupMembers!=null){
            for(ClientHandler clientHandler:groupMembers){
                clientHandler.sendMessage(message);
            }
        }
    }

    // Method to list active groups
    public static synchronized String listActiveGroups(){
        StringBuilder groupList = new StringBuilder();
        for(String groupName:groupHandlers.keySet()){
            groupList.append(groupName).append(", ");
        }
        if (groupList.length()>0){
            groupList.setLength(groupList.length()-2);
        }
        return groupList.toString();
    }
}
