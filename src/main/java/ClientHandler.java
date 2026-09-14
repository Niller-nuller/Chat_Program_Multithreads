import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private String username;
    private PrintWriter writer;
    private ChatRoom currentRoom;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            System.out.println("Client connected to " + socket.getInetAddress().getHostName() + " " + Thread.currentThread().getName());

            send("Enter username:");
            while (true) {
                String requested = receiveMessage(reader);
                if (requested == null) {
                    return;
                }
                requested = requested.trim();
                if (requested.isEmpty()) {
                    send("Username cannot be empty. Try again:");
                    continue;
                }
                boolean ok = ClientRegisty.getInstance().register(requested, this);
                if (ok) {
                    this.username = requested;
                    send("Welcome, " + requested + "!");
                    joinRoom("general");
                    send("Available commands: /join <room>, /rooms, /who, /msg <user> <message>, /history, /help, /quit");
                    break;
                }
                send("Username already taken. Try another:");
            }

            String line;
            while ((line = receiveMessage(reader)) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("/help")) {
                    send("Available commands: /join <room>, /rooms, /who, /msg <user> <message>, /history, /quit");
                    continue;
                }

                if (line.equalsIgnoreCase("/rooms")) {
                    List<String> roomSummaries = ChatRoomManager.getInstance().listRooms();
                    if (roomSummaries.isEmpty()) {
                        send("No active rooms.");
                    } else {
                        send("Available rooms:");
                        for (String roomSummary : roomSummaries) {
                            send(roomSummary);
                        }
                    }
                    continue;
                }

                if (line.equalsIgnoreCase("/who")) {
                    if (currentRoom == null) {
                        send("You are not in a room.");
                    } else {
                        send("Users in " + currentRoom.getName() + ": " + currentRoom.listMembers());
                    }
                    continue;
                }

                if (line.equalsIgnoreCase("/history")) {
                    if (currentRoom == null) {
                        send("You are not in a room.");
                    } else {
                        List<Message> history = currentRoom.getRecentHistory(20);
                        for (Message message : history) {
                            send(message.toString());
                        }
                    }
                    continue;
                }

                if (line.equalsIgnoreCase("/quit") || line.equalsIgnoreCase("/exit")) {
                    break;
                }

                if (line.startsWith("/join ")) {
                    String requestedRoom = line.substring("/join ".length()).trim();
                    if (requestedRoom.isEmpty()) {
                        send("Usage: /join <roomName>");
                    } else {
                        joinRoom(requestedRoom);
                    }
                    continue;
                }

                if (line.startsWith("/msg ")) {
                    String[] parts = line.substring("/msg ".length()).split("\\s+", 2);
                    if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        send("Usage: /msg <username> <message>");
                        continue;
                    }

                    String targetUsername = parts[0].trim();
                    String privateMessage = parts[1].trim();
                    ClientHandler target = ClientRegisty.getInstance().getHandler(targetUsername);
                    if (target == null) {
                        send("User '" + targetUsername + "' is not online.");
                    } else {
                        String outgoing = "[DM to " + targetUsername + "] " + privateMessage;
                        String incoming = "[DM from " + username + "] " + privateMessage;
                        send(outgoing);
                        target.send(incoming);
                    }
                    continue;
                }

                if (currentRoom == null) {
                    send("You are not in a room. Join one first with /join <roomName>.");
                    continue;
                }

                currentRoom.broadcast(new Message(username, line, Instant.now()));
            }
        } catch (IOException e) {
            System.out.println("Error client connection failed: " + e.getMessage());
        } finally {
            if (this.username != null) {
                ClientRegisty.getInstance().unregister(this.username);
            }
            leaveCurrentRoom();
            if (this.writer != null) {
                this.writer.close();
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void joinRoom(String roomName) {
        String normalizedName = ChatRoomManager.getInstance().normalizeRoomName(roomName);
        leaveCurrentRoom();
        ChatRoom room = ChatRoomManager.getInstance().getOrCreateRoom(normalizedName);
        room.addMember(this);
        this.currentRoom = room;
        send("You joined " + room.getName());
    }

    public void leaveCurrentRoom() {
        if (currentRoom == null) {
            return;
        }
        currentRoom.removeMember(username);
        ChatRoomManager.getInstance().removeRoomIfEmpty(currentRoom.getName());
        currentRoom = null;
    }

    public String getUsername() {
        return username;
    }

    public void send(String message) {
        if (this.writer != null) {
            this.writer.println(message);
        }
    }

    public String receiveMessage(BufferedReader reader) throws IOException {
        return reader.readLine();
    }
}

