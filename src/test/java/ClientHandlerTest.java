import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.net.Socket;

import org.junit.jupiter.api.Test;

class ClientHandlerTest {

    @Test
    void loginRegistersUserAndJoinsDefaultRoom() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);

        Method loginMethod = ClientHandler.class.getDeclaredMethod("login", String.class);
        loginMethod.setAccessible(true);
        loginMethod.invoke(handler, "alice");

        assertEquals("alice", handler.getUsername());
        assertEquals("default", handler.getCurrentRoom());
        assertTrue(registry.isUsernameTaken("alice"));
        assertTrue(roomManager.getRoom("default").getMembers().contains(handler));
    }

    @Test
    void blankUsernameIsRejected() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ClientHandler handler = new ClientHandler(new Socket(), registry, new ChatRoomManager());

        Method loginMethod = ClientHandler.class.getDeclaredMethod("login", String.class);
        loginMethod.setAccessible(true);
        loginMethod.invoke(handler, "   ");

        assertNull(handler.getUsername());
        assertTrue(registry.isUsernameTaken(""));
    }

    @Test
    void cleanupRemovesUserFromRegistryAndRoomMembership() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);

        Method loginMethod = ClientHandler.class.getDeclaredMethod("login", String.class);
        loginMethod.setAccessible(true);
        loginMethod.invoke(handler, "bob");

        handler.cleanup();

        assertFalse(registry.isUsernameTaken("bob"));
        assertFalse(roomManager.getRoom("default").getMembers().contains(handler));
    }
}
