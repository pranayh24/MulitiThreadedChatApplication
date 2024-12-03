package org.prh.chatapplication.controllers;

import org.prh.chatapplication.chat_managers.BroadcastManager;
import org.prh.chatapplication.chat_managers.GroupManager;
import org.prh.chatapplication.chat_managers.PrivateManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final BroadcastManager broadcastManager;
    private final PrivateManager privateManager;
    private final GroupManager groupManager;
    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(BroadcastManager broadcastManager, PrivateManager privateManager, GroupManager groupManager) {
        this.broadcastManager = broadcastManager;
        this.privateManager = privateManager;
        this.groupManager = groupManager;
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        // The username is passed as a query parameter (e.g., /chat?username=pranay)
        String username = getUsernameFromSession(session);
        activeSessions.put(username, session);
        privateManager.addClient(username, session);
        System.out.println("User connected: " + username);
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String[] parts = payload.split(" ", 3);

        if (parts.length < 2) {
            session.sendMessage(new TextMessage("Invalid message format"));
            return;
        }

        String type = parts[0].toUpperCase();
        String username = getUsernameFromSession(session);

        switch (type) {
            case "BROADCAST":
                broadcastManager.broadcastMessage(username, parts[1]);
                break;

            case "PRIVATE":
                if (parts.length < 3) {
                    session.sendMessage(new TextMessage("Invalid message format"));
                    return;
                }
                privateManager.sendPrivateMessage(username, parts[1], parts[2]);
                break;

            case "GROUP":
                if (parts.length < 3) {
                    session.sendMessage(new TextMessage("Invalid group message format."));
                } else {
                    groupManager.broadcastGroupMessage(parts[1], username + ": " + parts[2]);
                }
                break;

            default:
                session.sendMessage(new TextMessage("Invalid message type"));
                break;
        }
    }
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String username = getUsernameFromSession(session);
        activeSessions.remove(username);
        privateManager.removeClient(username);
        System.out.println("User disconnected: " + username);
    }

    private String getUsernameFromSession(WebSocketSession session) {
        // Extract username from query parameters or attributes.
        // Example: Assume query parameter is used, e.g., /chat?username=JohnDoe
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("username=")) {
            return query.split("=")[1];
        }
        return "Unknown";
    }
}

