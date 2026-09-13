import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ClientRegistry clientRegistry;
    private final ChatRoomManager chatRoomManager;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private String currentRoom;
    private boolean running = true;

    public ClientHandler(Socket socket, ClientRegistry clientRegistry, ChatRoomManager chatRoomManager) {
        this.socket = socket;
        this.clientRegistry = clientRegistry;
        this.chatRoomManager = chatRoomManager;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            System.out.println("Client connected to " + socket.getInetAddress().getHostName() + " " + Thread.currentThread().getName());
            readIncomingMessages();
        } catch (IOException e) {
            System.out.println("Client disconnected unexpectedly: " + socket.getRemoteSocketAddress());
        } finally {
            cleanup();
        }
    }

    private void readIncomingMessages() throws IOException {
        String input;
        while (running && (input = reader.readLine()) != null) {
            Message message = MessageParser.parse(input);
            if (handleMessage(message)) {
                break;
            }
        }
    }

    private boolean handleMessage(Message message) {
        if (message == null) {
            return false;
        }

        switch (message.getType()) {
            case "LOGIN":
                login(message.getPayload());
                return false;
            case "JOIN_ROOM":
                if (username == null) {
                    sendError("You must log in before joining a room.");
                    return false;
                }
                chatRoomManager.joinRoom(this, message.getPayload());
                return false;
            case "LIST_ROOMS":
                if (username == null) {
                    sendError("You must log in before listing rooms.");
                    return false;
                }
                chatRoomManager.listRooms(this);
                return false;
            case "LIST_USERS":
                if (username == null) {
                    sendError("You must log in before listing users.");
                    return false;
                }
                listOnlineUsers();
                return false;
            case "PRIVATE_MESSAGE":
                if (username == null) {
                    sendError("You must log in before sending private messages.");
                    return false;
                }
                handlePrivateMessage(message.getPayload());
                return false;
            case "QUIT":
                send("GOODBYE|");
                return true;
            case "CHAT":
                if (username == null) {
                    sendError("You must log in before sending messages.");
                    return false;
                }
                if (currentRoom == null) {
                    sendError("Join a room before sending messages.");
                    return false;
                }
                chatRoomManager.broadcast(currentRoom, username, message.getPayload());
                return false;
            case "ERROR":
                sendError(message.getPayload());
                return false;
            default:
                sendError("Unknown message type.");
                return false;
        }
    }

    private void listOnlineUsers() {
        String users = String.join(",", clientRegistry.getActiveUsernames());
        send("USERS|" + users);
    }

    private void handlePrivateMessage(String privatePayload) {
        if (privatePayload == null || privatePayload.isBlank()) {
            sendError("Private message requires a recipient and payload.");
            return;
        }

        String[] privateFields = privatePayload.split("\\|", 2);
        if (privateFields.length < 2) {
            sendError("Private message format must be Private_Message|username|payload.");
            return;
        }

        String recipientUsername = privateFields[0].trim();
        String messagePayload = privateFields[1].trim();
        if (recipientUsername.isBlank() || messagePayload.isBlank()) {
            sendError("Private message requires a recipient and a message.");
            return;
        }

        ClientHandler recipient = clientRegistry.getClient(recipientUsername);
        if (recipient == null) {
            sendError("User '" + recipientUsername + "' is not online.");
            return;
        }

        String formattedMessage = "PRIVATE|" + username + "|" + messagePayload;
        recipient.send(formattedMessage);
        send("PRIVATE_SENT|" + recipientUsername + "|" + messagePayload);
    }

    private void login(String requestedUsername) {
        if (requestedUsername == null || requestedUsername.isBlank()) {
            sendError("Username cannot be empty.");
            return;
        }

        String normalizedUsername = requestedUsername.trim().replace("\"", "");
        if (clientRegistry.isUsernameTaken(normalizedUsername)) {
            sendError("Username already in use.");
            return;
        }

        boolean registered = clientRegistry.register(normalizedUsername, this);
        if (!registered) {
            sendError("Username already in use.");
            return;
        }

        this.username = normalizedUsername;
        send("LOGIN_OK|" + normalizedUsername);
        chatRoomManager.joinRoom(this, ChatRoomManager.DEFAULT_ROOM_NAME);
        System.out.println("User logged in: " + normalizedUsername);
    }

    public void send(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }

    public void sendError(String errorMessage) {
        String targetUsername = username == null ? "unknown" : username;
        send("ERROR|server|" + targetUsername + "|" + errorMessage);
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void cleanup() {
        running = false;

        if (username != null && clientRegistry != null) {
            clientRegistry.unregister(username);
        }

        if (chatRoomManager != null) {
            chatRoomManager.leaveRoom(this);
        }

        closeResources();
    }

    private void closeResources() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Socket close warning: " + e.getMessage());
        }
    }
}
