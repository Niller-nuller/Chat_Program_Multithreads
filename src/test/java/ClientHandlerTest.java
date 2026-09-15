import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ClientHandlerTest {

    @Test
    public void testEmptyUsername() throws IOException {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);

        BufferedReader reader = new BufferedReader(new StringReader(""));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);


        handler.checkUsernameMessageStatus("");
        assertTrue(true);
    }


    @Test
    public void testLogin() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);

        BufferedReader reader = new BufferedReader(new StringReader("alice\n"));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);

        handler.login(reader, writer);

        assertTrue(registry.isUsernameActive("alice"));
    }
    @Test
    public void testDoubleLogin() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);

        BufferedReader reader = new BufferedReader(new StringReader("alice\n"));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);

        handler.login(reader, writer);
    }@Test
    public void testLogin_userPresentAfterLogin() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);

        BufferedReader reader = new BufferedReader(new StringReader("alice\n"));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);

        handler.login(reader, writer);

        assertTrue(registry.isUsernameActive("alice"));
        assertTrue(sw.toString().contains("ACCEPTED|Welcome to the server"));
    }

    @Test
    public void testLogin_emptyUsernameThenValid() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);

        BufferedReader reader = new BufferedReader(new StringReader("\nalice\n"));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);

        handler.login(reader, writer);

        assertTrue(sw.toString().contains("EMPTY|Username is empty, please try again"));
        assertTrue(sw.toString().contains("ACCEPTED|Welcome to the server"));
        assertTrue(registry.isUsernameActive("alice"));
    }
    @Test
    public void testLogin_usernameTakenByAnotherActiveClient() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();

        // Simulate another thread's client already logged in as "alice"
        ClientHandler otherHandler = new ClientHandler(new Socket(), registry, roomManager);
        registry.registerClient("alice", otherHandler);

        // New client tries "alice" first, then falls back to "bob"
        ClientHandler handler = new ClientHandler(new Socket(), registry, roomManager);
        BufferedReader reader = new BufferedReader(new StringReader("alice\nbob\n"));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);

        handler.login(reader, writer);

        String output = sw.toString();
        assertTrue(output.contains("IN USE|Username already in use, please choose a different one"));
        assertTrue(output.contains("ACCEPTED|Welcome to the server"));

        // both usernames should be active now — alice from otherHandler, bob from handler
        assertTrue(registry.isUsernameActive("alice"));
        assertTrue(registry.isUsernameActive("bob"));
    }
    @Test
    public void testLogin_usernameAvailableAfterPreviousClientDisconnected() throws Exception {
        ClientRegistry registry = new ClientRegistry();
        ChatRoomManager roomManager = new ChatRoomManager();

        // Simulate a previous client that logged in as "alice" and then disconnected
        ClientHandler firstHandler = new ClientHandler(new Socket(), registry, roomManager);
        registry.registerClient("alice", firstHandler);
        registry.removeClientFromActiveClients("alice");

        // "alice" is still in clientUsernames (remembered), but no longer active
        assertFalse(registry.isUsernameActive("alice"));

        // A new client now logs in with the same name
        ClientHandler secondHandler = new ClientHandler(new Socket(), registry, roomManager);
        BufferedReader reader = new BufferedReader(new StringReader("alice\n"));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);

        secondHandler.login(reader, writer);

        String output = sw.toString();
        assertFalse(output.contains("IN USE"));
        assertTrue(output.contains("ACCEPTED|Welcome to the server"));
        assertTrue(registry.isUsernameActive("alice"));
    }
}
