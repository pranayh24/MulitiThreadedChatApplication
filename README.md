# Chat Application with Group Messaging and WebSocket

This is a simple multi-client chat application implemented in Java using WebSocket for real-time communication. It supports both private messaging and group chat functionalities. The server manages multiple clients, allowing them to communicate with each other in real-time through different chat rooms (groups) or private messages.

## Features
- 1. Private Messaging
  - Users can send direct messages to each other in a one-on-one conversation.
  - Messages sent privately are only visible to the sender and receiver.
- 2. Group Messaging
  - Users can join existing chat groups or create new groups.
  - Once a user joins a group, they can send messages that are broadcast to all members of the group.
  - Users can leave a group anytime.
- 3. Broadcasting
  - The server broadcasts messages to all connected clients.
  - Any client can send a broadcast message visible to all connected users, regardless of their group membership.
- 4. WebSocket for Real-Time Communication
  - Real-time messaging is handled using WebSocket, ensuring low-latency communication between clients and the server.
  - The server listens for messages from clients and immediately relays them to the intended recipients.
## Technology Stack
- **Java**: Core programming language for server-side logic.
- **WebSocket**: Used for real-time, low-latency communication between clients and the server.
- **Maven**: Dependency and build management.
- **Multi-threading**: Each client is handled by a separate thread for simultaneous message handling.
