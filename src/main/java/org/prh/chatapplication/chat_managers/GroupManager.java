package org.prh.chatapplication.chat_managers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroupManager {

    private final Map<String, Set<String>> groupMembers = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    public synchronized  void createGroup(String groupName, String creater) {
        groupMembers.putIfAbsent(groupName, ConcurrentHashMap.newKeySet());
        groupMembers.get(groupName).add(creater);
    }

    public synchronized void joinGroup(String groupName, String username) {
        Set<String> members = groupMembers.get(groupName);
        if(members!=null) {
            members.add(username);
            broadcastGroupMessage(groupName, username + " joined the group.");
        }
    }

    public synchronized void leaveGroup(String groupName, String username) {
        Set<String> members = groupMembers.get(groupName);
        if(members!=null) {
            members.remove(username);
            broadcastGroupMessage(groupName, username + " left the group.");
        }
    }

    public synchronized void broadcastGroupMessage(String groupName, String message) {
        Set<String> members = groupMembers.get(groupName);
        if(members!=null) {
            for(String member:members) {
                WebSocketSession session = clients.get(member);
                if(session != null) {
                    try {
                        session.sendMessage(new TextMessage("[" + groupName + "] " + message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public synchronized Set<String> listActiveGroups() {
        return groupMembers.keySet();
    }
}
