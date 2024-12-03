package org.prh.chatapplication.chat_managers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.out;

@Component
public class PrivateManager {
    private final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();


    public synchronized void addClient(String username, WebSocketSession session) {
        clients.put(username, session);
    }

    public synchronized void removeClient(String username) {
        clients.remove(username);
    }

    public synchronized  void sendPrivateMessage(String sender, String recipient, String message) throws Exception{
        WebSocketSession recipientSession = clients.get(recipient);
        if(recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage("Private from "+ sender +": "+message));
        } else {
            WebSocketSession senderSession = clients.get(sender);
            if(senderSession != null) {
                senderSession.sendMessage(new TextMessage("User: "+recipient+" is not available."));
            }
        }
    }

    public synchronized Set<String> getActiveUsers() {
        return clients.keySet();
    }
}
