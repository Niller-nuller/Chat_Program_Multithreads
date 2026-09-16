import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ChatRoomManagerTest {

    @Test
    public void testDefaultChatRoomCreation() {
        ChatRoomManager roomManager = new ChatRoomManager();

        assertTrue(roomManager.getChatRooms().contains("Default"));
    }
    @Test
    public void testDefaultChatRoomCreatedOnInit() {
        ChatRoomManager roomManager = new ChatRoomManager();

        assertTrue(roomManager.hasChatRoom("Default"));
        assertNotNull(roomManager.getChatRoom("Default"));
    }
    @Test
    public void testJoinRoom_clientAddedToDefaultRoom() throws Exception {
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientRegistry registry = new ClientRegistry();
        ClientHandler client = new ClientHandler(new Socket(), registry, roomManager);

        roomManager.joinRoom(client, "Default");

        ChatRoom defaultRoom = roomManager.getChatRoom("Default");
        assertNotNull(defaultRoom);
        assertTrue(defaultRoom.getClientMembers().contains(client));
    }
    @Test
    public void testJoinRoom_nullClientIsIgnored() {
        ChatRoomManager roomManager = new ChatRoomManager();

        // should not throw, and Default room should remain empty
        roomManager.joinRoom(null, "Default");

        ChatRoom defaultRoom = roomManager.getChatRoom("Default");
        assertTrue(defaultRoom.getClientMembers().isEmpty());
    }

    @Test
    public void testJoinRoom_nonexistentRoomIsIgnored() throws Exception {
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientRegistry registry = new ClientRegistry();
        ClientHandler client = new ClientHandler(new Socket(), registry, roomManager);

        // "Nonexistent" was never created, so this should silently no-op
        roomManager.joinRoom(client, "Nonexistent");

        ChatRoom defaultRoom = roomManager.getChatRoom("Default");
        assertFalse(defaultRoom.getClientMembers().contains(client));
    }

    @Test
    public void testJoinRoom_duplicateJoinDoesNotAddTwice() throws Exception {
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientRegistry registry = new ClientRegistry();
        ClientHandler client = new ClientHandler(new Socket(), registry, roomManager);

        roomManager.joinRoom(client, "Default");
        roomManager.joinRoom(client, "Default"); // join again

        ChatRoom defaultRoom = roomManager.getChatRoom("Default");
        long count = defaultRoom.getClientMembers().stream().filter(c -> c == client).count();
        assertEquals(1, count);
    }
}
