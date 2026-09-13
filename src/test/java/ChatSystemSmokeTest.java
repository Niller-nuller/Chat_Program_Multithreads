import java.net.Socket;

public class ChatSystemSmokeTest {

    public static void main(String[] args) {
        testMessageParsing();
        testDuplicateUsernameGuard();
        testDefaultRoomsAndHistory();
        System.out.println("All smoke tests passed.");
    }

    private static void testMessageParsing() {
        Message joinRoom = MessageParser.parse("Join_Room|default");
        assertEquals("JOIN_ROOM", joinRoom.getType(), "join room command should be parsed as JOIN_ROOM");
        assertEquals("default", joinRoom.getPayload(), "join room payload should preserve the room name");

        Message listRooms = MessageParser.parse("List_Rooms|");
        assertEquals("LIST_ROOMS", listRooms.getType(), "list rooms command should be parsed as LIST_ROOMS");

        Message privateMessage = MessageParser.parse("Private_Message|alice|hello there");
        assertEquals("PRIVATE_MESSAGE", privateMessage.getType(), "private message command should be parsed as PRIVATE_MESSAGE");
        assertEquals("alice|hello there", privateMessage.getPayload(), "private message payload should contain target and message");

        Message privateMessageWithExtraPayload = MessageParser.parse("Private_Message|alice|hello|there");
        assertEquals("alice|hello|there", privateMessageWithExtraPayload.getPayload(), "private message payload should preserve additional pipe-delimited content");

        Message listUsers = MessageParser.parse("List_Users|");
        assertEquals("LIST_USERS", listUsers.getType(), "list users command should be parsed as LIST_USERS");
    }

    private static void testDuplicateUsernameGuard() {
        ClientRegistry registry = new ClientRegistry();
        ClientHandler firstClient = new ClientHandler(new Socket(), registry, new ChatRoomManager());
        ClientHandler secondClient = new ClientHandler(new Socket(), registry, new ChatRoomManager());

        assertTrue(registry.register("alice", firstClient), "first alice registration should succeed");
        assertTrue(registry.isUsernameTaken("alice"), "alice should be considered active");
        assertTrue(!registry.register("alice", secondClient), "duplicate alice registration should be rejected");

        registry.unregister("alice");
        assertTrue(!registry.isUsernameTaken("alice"), "alice should be removed after unregister");
    }

    private static void testDefaultRoomsAndHistory() {
        ChatRoomManager roomManager = new ChatRoomManager();

        assertEquals(3, roomManager.getRoomNames().size(), "exactly three rooms should exist at startup");
        assertTrue(roomManager.getRoomNames().contains("default"), "default room should exist");
        assertTrue(roomManager.getRoomNames().contains("The_Frist_Room"), "The_Frist_Room should exist");
        assertTrue(roomManager.getRoomNames().contains("Seconds"), "Seconds room should exist");

        ChatRoom defaultRoom = roomManager.getRoom("default");
        assertTrue(defaultRoom != null, "default room should be available by name");

        ClientHandler client = new ClientHandler(new Socket(), new ClientRegistry(), roomManager);
        roomManager.joinRoom(client, "default");

        assertEquals("default", client.getCurrentRoom(), "client should be assigned to default room");
        assertTrue(defaultRoom.getMembers().contains(client), "client should be added to the default room");
        assertTrue(!defaultRoom.getHistorySnapshot().isEmpty(), "room history should be populated when the client joins");

        roomManager.leaveRoom(client);
        assertTrue(!defaultRoom.getMembers().contains(client), "client should be removed from the room on leave");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " Expected=" + expected + " Actual=" + actual);
        }
    }
}
