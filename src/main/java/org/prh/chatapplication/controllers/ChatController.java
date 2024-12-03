package org.prh.chatapplication.controllers;


import org.apache.coyote.Response;
import org.prh.chatapplication.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Create a new group
    @PostMapping("/group/create")
    public ResponseEntity<String> createGroup(@RequestParam String groupName, @RequestParam String creator) {
        chatService.createGroup(groupName,creator);
        return ResponseEntity.ok("Group '"+ groupName +" created successfully");
    }

    // Join an existing group
    @PostMapping("/group/join")
    public ResponseEntity<String> joinGroup(@RequestParam String groupName, @RequestParam String username) {
        chatService.joinGroup(groupName,username);
        return ResponseEntity.ok(username + " joined group '"+ groupName + "'.");
    }

    // Send a broadcast message
    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastMessage(@RequestParam String sender, @RequestBody String message) {
        try {
            chatService.broadcastMessage(sender, message);
            return ResponseEntity.ok("Broadcast message sent successfully.");
        } catch(Exception e) {
            return ResponseEntity.status(500).body("Failed to broadcast message.");
        }
    }

    // Send a private message
    @PostMapping("/private")
    public ResponseEntity<String> sendPrivateMessage(@RequestParam String sender, @RequestParam String recipient, @RequestBody String message) {
        try {
            chatService.sendPrivateMessage(sender, recipient, message);
            return ResponseEntity.ok("Private message sent to '" + recipient + "'.");
        } catch(Exception e) {
            return ResponseEntity.status(500).body("Failed to send private message.");
        }
    }

    // send a group message
    public ResponseEntity<String> sendGroupMessage(@RequestParam String groupName, @RequestParam String sender, @RequestBody String message) {
        try {
            chatService.sendGroupMessage(groupName, sender, message);
            return ResponseEntity.ok("Message sent to group '"+ groupName +"'.");
        } catch(Exception e) {
            return ResponseEntity.status(500).body("Failed to send message to group '"+ groupName +"'.");
        }
    }

    // List all active groups
    @GetMapping("/group/list")
    public ResponseEntity<Set<String>> listActiveGroups() {
        return ResponseEntity.ok(chatService.listActiveGroups());
    }

    // Leave a group
    @PostMapping("/group/leave")
    public ResponseEntity<String> leaveGroup(@RequestParam String groupName, @RequestParam String username) {
        chatService.leaveGroup(groupName, username);
        return ResponseEntity.ok(username + " left group '"+ groupName +"'.");
    }
}
