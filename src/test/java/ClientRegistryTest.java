import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Socket;

import org.junit.jupiter.api.Test;

class ClientRegistryTest {

    @Test
    void registersUserAndRejectsDuplicates() {
        ClientRegistry registry = new ClientRegistry();
        ClientHandler firstHandler = new ClientHandler(new Socket(), registry, new ChatRoomManager());
        ClientHandler secondHandler = new ClientHandler(new Socket(), registry, new ChatRoomManager());

        assertTrue(registry.register("alice", firstHandler));
        assertTrue(registry.isUsernameTaken("alice"));
        assertFalse(registry.register("alice", secondHandler));
    }

    @Test
    void removesUserWhenUnregistering() {
        ClientRegistry registry = new ClientRegistry();
        ClientHandler handler = new ClientHandler(new Socket(), registry, new ChatRoomManager());

        registry.register("bob", handler);
        assertTrue(registry.isUsernameTaken("bob"));

        registry.unregister("bob");
        assertFalse(registry.isUsernameTaken("bob"));
        assertEquals(0, registry.getActiveUsernames().size());
    }

    @Test
    void returnsRegisteredClientByUsername() {
        ClientRegistry registry = new ClientRegistry();
        ClientHandler handler = new ClientHandler(new Socket(), registry, new ChatRoomManager());

        registry.register("charlie", handler);

        assertEquals(handler, registry.getClient("charlie"));
        assertTrue(registry.getActiveUsernames().contains("charlie"));
    }
}
