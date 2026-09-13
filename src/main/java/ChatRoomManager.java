
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatRoomManager {

    public static final String DEFAULT_ROOM_NAME = "default";
    public static final String FIRST_EXTRA_ROOM_NAME = "The_Frist_Room";
    public static final String SECOND_EXTRA_ROOM_NAME = "Seconds";

    private final ConcurrentMap<String, ChatRoom> rooms = new ConcurrentHashMap<>();

    public ChatRoomManager() {
        createRoom(DEFAULT_ROOM_NAME);
        createRoom(FIRST_EXTRA_ROOM_NAME);
        createRoom(SECOND_EXTRA_ROOM_NAME);
    }

    public void joinRoom(ClientHandler clientHandler, String roomName) {
        if (clientHandler == null) {
            return;
        }

        String normalizedRoomName = normalizeRoomName(roomName);
        if (normalizedRoomName == null || normalizedRoomName.isBlank()) {
            clientHandler.sendError("Room name cannot be empty.");
            return;
        }

        ChatRoom destinationRoom = rooms.computeIfAbsent(normalizedRoomName, this::createRoom);
        String currentRoomName = clientHandler.getCurrentRoom();

        if (currentRoomName != null && currentRoomName.equals(normalizedRoomName)) {
            clientHandler.send("SERVER|You are already in room: " + normalizedRoomName);
            return;
        }

        if (currentRoomName != null) {
            leaveRoom(clientHandler);
        }

        destinationRoom.addMember(clientHandler);
        clientHandler.setCurrentRoom(normalizedRoomName);

        List<String> history = destinationRoom.getHistorySnapshot();
        if (!history.isEmpty()) {
            clientHandler.send("ROOM_HISTORY|" + normalizedRoomName + "|" + String.join("\n", history));
        }

        String joinMessage = "SERVER|" + clientHandler.getUsername() + " joined the room.";
        destinationRoom.addMessageToHistory(joinMessage);
        broadcastToRoom(normalizedRoomName, joinMessage);
        clientHandler.send("ROOM_JOINED|" + normalizedRoomName);
    }

    public void leaveRoom(ClientHandler clientHandler) {
        if (clientHandler == null) {
            return;
        }

        String currentRoomName = clientHandler.getCurrentRoom();
        if (currentRoomName == null) {
            return;
        }

        ChatRoom currentRoom = rooms.get(currentRoomName);
        if (currentRoom != null) {
            currentRoom.removeMember(clientHandler);
            String leaveMessage = "SERVER|" + clientHandler.getUsername() + " left the room.";
            currentRoom.addMessageToHistory(leaveMessage);
            broadcastToRoom(currentRoomName, leaveMessage);
        }

        clientHandler.setCurrentRoom(null);
    }

    public void broadcast(String roomName, String senderUsername, String message) {
        if (roomName == null || roomName.isBlank()) {
            return;
        }

        String formattedMessage =  getCurrentTime() + "|TEXT|" + senderUsername + "|" + message;
        ChatRoom room = rooms.get(roomName);
        if (room == null) {
            return;
        }

        room.addMessageToHistory(formattedMessage);
        broadcastToRoom(roomName, formattedMessage);
    }
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
        return now.format(formatter);
    }

    public List<String> getRoomNames() {
        List<String> roomNames = new ArrayList<>();
        rooms.keySet().stream().sorted().forEach(roomNames::add);
        return roomNames;
    }

    public ChatRoom getRoom(String roomName) {
        if (roomName == null) {
            return null;
        }
        return rooms.get(normalizeRoomName(roomName));
    }

    public void listRooms(ClientHandler clientHandler) {
        if (clientHandler == null) {
            return;
        }

        String roomList = String.join(",", getRoomNames());
        clientHandler.send("ROOMS|" + roomList);
    }

    private ChatRoom createRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom(roomName);
        rooms.putIfAbsent(roomName, chatRoom);
        return rooms.get(roomName);
    }

    private void broadcastToRoom(String roomName, String message) {
        ChatRoom room = rooms.get(roomName);
        if (room == null) {
            return;
        }

        for (ClientHandler clientHandler : room.getMembers()) {
            clientHandler.send(message);
        }
    }

    private String normalizeRoomName(String roomName) {
        if (roomName == null) {
            return null;
        }

        return roomName.trim().replace("\"", "");
    }
}
