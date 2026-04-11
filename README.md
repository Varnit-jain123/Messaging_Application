# Messaging Application (Message Board)

A real-time, WebSocket-based messaging application featuring a Java Swing client and a Java Servlet server. This application allows multiple users to log in, see who else is online, and exchange messages in a shared chat room.

## 🚀 Key Features

- **Real-time Messaging**: Instant message delivery using WebSockets.
- **User Authentication**: Secure login system with predefined user credentials.
- **Live Online Users**: See a dynamic list of currently connected users.
- **Message Broadcasting**: Messages posted by any user are instantly broadcast to all active participants.
- **Graphical User Interface**: A clean and intuitive Swing-based desktop client.

## 🛠️ Technology Stack

- **Java**: Core language for both client and server components.
- **WebSockets (JSR 356)**: Protocol for full-duplex, low-latency communication.
- **Java Swing**: Framework for the desktop GUI.
- **Google Gson**: Library for JSON serialization and deserialization of communication packets.
- **Apache Tomcat**: Web server/container used to host the WebSocket server.

## 🏗️ Project Architecture & Working

The project follows a **Client-Server model** powered by WebSockets:

1.  **Handshake**: The client initiates a connection to the WebSocket endpoint at `ws://localhost:8081/wsmboard/messageBoardServer`.
2.  **Custom Protocol**: Communication happens via "Cards." A `Card` is a JSON object containing:
    - `type`: Either `REQUEST` or `RESPONSE`.
    - `action`: The specific operation (e.g., `LOGIN`, `POST_MESSAGE`, `ADD_USER`).
    - `object`: The payload containing data relevant to the action.
3.  **Server Logic**: The server (`MessageBoardServer`) manages active sessions and broadcasts events (like a new user joining or a new message) to all connected clients.
4.  **Data Management**: An in-memory `DataModel` stores registered users, active sessions, and recent messages.

## 📁 Directory Structure

```text
MessageApplication/
├── client/           # Client-side Java code (Swing GUI & logic)
├── server/           # Tomcat server installation and webapp logic
│   └── webapps/wsmboard/
│       └── WEB-INF/classes/com/varnit/jain/mboard/server/ # Server logic
├── common/           # Shared POJOs and Protocol models (Card, User, Message)
└── gson/             # Google Gson library files
```

## ⚙️ Setup & Installation

### 1. Start the Server
- Ensure **Apache Tomcat** is configured to run on port `8081` (or update the client connection URL).
- Deploy the `wsmboard` web application to Tomcat's `webapps` directory.
- Start the Tomcat server.

### 2. Run the Client
To run the client, use the following classpath configuration:
```bash
java -classpath "..\..\..\server\lib\*;..\lib\*;..\..\..\gson\*;..\common;." MSBClient
```
*Note: Adjust paths based on your current working directory relative to the project root.*

## 👤 Predefined Users
The system comes with several hardcoded users for testing (Password is the same as the Username):
- `varnit`
- `tubhyam`
- `alice`
- `bob`
- `charlie`
- ... and more.

---
Developed by **Varnit Jain**