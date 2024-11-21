package org.prh.client;

import org.prh.server.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

// ClientHandler class to handle each client in a separate thread
public class ClientHandler implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Set<String> groups = new HashSet<>();

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try{
            // Ask for the client's username
            out.println("Enter your username:");
            username = in.readLine();
            System.out.println(username+" has joined the chat.");

            // Add the new client to the server's list
            ChatServer.addClient(username,this);

            ChatServer.broadcastMessage(username+" has joined the chat",this);

            ChatServer.sendPublicHistory(this);

            // Read and process messages sent by the client
            String message;
            while((message = in.readLine())!=null){
                if(message.equalsIgnoreCase("/users")){
                    out.println(ChatServer.getActiveUsers());
                }
                else if(message.startsWith("/pm")){
                    // Private Messaging
                    String[] splitMessage = message.split(" ",3);
                    if(splitMessage.length>=3){
                        String recipientUsername = splitMessage[1];
                        String privateMesssage = splitMessage[2];
                        ChatServer.sendPrivateMessage(recipientUsername,privateMesssage,this);
                    }
                    else{
                        out.println("Invalid private messsage format. Use: /pm <username> <message>");
                    }
                }
                else if(message.startsWith("/creategroup")){
                    String[] splitMessage = message.split(" ",2);
                    if(splitMessage.length==2){
                        ChatServer.createGroup(splitMessage[1],this);
                    }
                    else{
                        out.println("Invalid group creation format. Use: /creategroup <groupname>");
                    }
                }
                else if(message.startsWith("/joingroup")){
                    String[] splitMessage = message.split(" ",2);
                    if(splitMessage.length==2){
                        ChatServer.joinGroup(splitMessage[1],this);
                    }
                    else{
                        out.println("Invalid group join format. Use: /joingroup <groupname>");
                    }
                }
                else if(message.startsWith("/leavegroup")){
                    String[] splitMessage = message.split(" ",2);
                    if(splitMessage.length==2){
                        ChatServer.leaveGroup(splitMessage[1],this);
                    }
                    else{
                        out.println("Invalid group leave format. Use: /leavegroup <groupname>");
                    }
                }
                else if(message.startsWith("/groups")){
                    out.println(ChatServer.listActiveGroups());
                }
                else if(message.startsWith("group")){
                    // Group Messaging
                    String[] splitMessage = message.split(" ",3);
                    if(splitMessage.length>=3){
                        String groupName = splitMessage[1];
                        String groupMessage = splitMessage[2];
                        if(groups.contains(groupName)){
                            ChatServer.broadcastGroupMessage(groupName,username+": "+groupMessage);
                        }
                        else{
                            out.println("You are not part of the group: "+groupName);
                        }
                    }
                    else{
                        out.println("Invalid group message format. Use: group <groupname> <message>");
                    }
                }
                else if(message.startsWith("/public")){
                    String publicMessage = message.substring(8);
                    ChatServer.broadcastMessage(username + ": " + publicMessage, this); //Broadcast message to other clients
                }
                else{
                    out.println("Invalid command. Use /pm, /public, /creategroup, /joingroup, /leavegroup, /groups, /group.");
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally {
            // Remove this client from the set of active clients
            ChatServer.removeClient(this);
            ChatServer.broadcastMessage(username+" has left the chat.",this);
            try{
                socket.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }


    // Method to send a message to this client
    public void sendMessage(String message){
        out.println(message);
    }

    public void addGroup(String groupName){
         groups.add(groupName);
    }
    public void removeGroup(String groupName){
        groups.remove(groupName);
    }
    // Getter for username
    public String getUsername(){
        return username;
    }
}
