package org.prh.chatapplication.chat_managers;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BroadcastManager {

    private static final int HISTORY_LIMIT = 20;
    private final List<String> messageHistory = new ArrayList<>();
    private final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();


    public synchronized void broadcastMessage(String sender, String message) throws Exception{
        if(messageHistory.size()==HISTORY_LIMIT) {
            messageHistory.removeFirst();
        }
        messageHistory.add(message);

        for(WebSocketSession session:clients.values()) {
            session.sendMessage(new TextMessage("[Public] " + sender + ": message"));
        }
    }

    public synchronized List sendMessageHistory() {
        return new ArrayList<>(messageHistory);
    }
}
