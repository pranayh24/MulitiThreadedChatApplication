package org.prh.chatapplication.service;

import org.prh.chatapplication.chat_managers.BroadcastManager;
import org.prh.chatapplication.chat_managers.GroupManager;
import org.prh.chatapplication.chat_managers.PrivateManager;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ChatService {

    private final BroadcastManager broadcastManager;
    private final PrivateManager privateManager;
    private final GroupManager groupManager;

    public ChatService(BroadcastManager broadcastManager, PrivateManager privateManager, GroupManager groupManager) {
        this.broadcastManager = broadcastManager;
        this.privateManager = privateManager;
        this.groupManager = groupManager;
    }

    public void broadcastMessage(String sender, String message) throws Exception {
        broadcastManager.broadcastMessage(sender,message);
    }

    public void sendPrivateMessage(String sender, String recipient, String message) throws Exception {
        privateManager.sendPrivateMessage(sender, recipient, message);
    }

    public void createGroup(String groupName, String creator) {
        groupManager.createGroup(groupName, creator);
    }

    public void joinGroup(String groupName, String username) {
        groupManager.joinGroup(groupName, username);
    }

    public void leaveGroup(String groupName, String username) {
        groupManager.leaveGroup(groupName, username);
    }

    public void sendGroupMessage(String groupName, String sender, String message) {
        groupManager.broadcastGroupMessage(groupName, sender + ": " + message);
    }

    public Set<String> listActiveGroups() {
        return groupManager.listActiveGroups();
    }

    public Set<String> getActiveUsers() {
        return privateManager.getActiveUsers();
    }
}
