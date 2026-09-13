import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Socket;
import java.util.List;

import org.junit.jupiter.api.Test;

class ChatRoomManagerTest {

    @Test
    void startsWithThreeDefaultRooms() {
        ChatRoomManager roomManager = new ChatRoomManager();
        List<String> roomNames = roomManager.getRoomNames();

        assertEquals(3, roomNames.size());
        assertTrue(roomNames.contains("default"));
        assertTrue(roomNames.contains("The_Frist_Room"));
        assertTrue(roomNames.contains("Seconds"));
    }

    @Test
    void joiningRoomAddsMemberAndRoomHistory() {
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), new ClientRegistry(), roomManager);

        roomManager.joinRoom(handler, "default");

        ChatRoom room = roomManager.getRoom("default");
        assertNotNull(room);
        assertEquals("default", handler.getCurrentRoom());
        assertTrue(room.getMembers().contains(handler));
        assertFalse(room.getHistorySnapshot().isEmpty());
    }

    @Test
    void broadcastOnlyReachesMembersOfTheSameRoom() {
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientRegistry registry = new ClientRegistry();
        ClientHandler alpha = new ClientHandler(new Socket(), registry, roomManager);
        ClientHandler beta = new ClientHandler(new Socket(), registry, roomManager);
        ClientHandler gamma = new ClientHandler(new Socket(), registry, roomManager);

        roomManager.joinRoom(alpha, "default");
        roomManager.joinRoom(beta, "default");
        roomManager.joinRoom(gamma, "The_Frist_Room");

        roomManager.broadcast("default", "alpha", "hello everyone");

        ChatRoom defaultRoom = roomManager.getRoom("default");
        ChatRoom fridayRoom = roomManager.getRoom("The_Frist_Room");

        assertTrue(defaultRoom.getHistorySnapshot().stream().anyMatch(entry -> entry.contains("hello everyone")));
        assertFalse(fridayRoom.getHistorySnapshot().stream().anyMatch(entry -> entry.contains("hello everyone")));
        assertEquals(2, defaultRoom.getMembers().size());
    }
}
