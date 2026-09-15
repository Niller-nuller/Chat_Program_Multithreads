import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConcurrentChatBehaviorTest {

    private final List<Socket> socketsToClose = new CopyOnWriteArrayList<>();

    @After
    public void tearDown() throws Exception {
        for (Socket socket : socketsToClose) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        socketsToClose.clear();

        clearRegistryUsers();
        clearRoomMembers("general");
    }

    @Test
    public void concurrentDuplicateUsernameRegistration_AllowsExactlyOne() throws Exception {
        String username = "same-user";
        ClientRegisty registry = ClientRegisty.getInstance();

        final int attempts = 2;
        CountDownLatch ready = new CountDownLatch(attempts);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(attempts);
        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < attempts; i++) {
            pool.submit(() -> {
                ready.countDown();
                start.await();
                ClientHandler handler = new ClientHandler(new Socket());
                boolean ok = registry.register(username, handler);
                results.add(ok);
                return null;
            });
        }

        assertTrue("Workers did not become ready in time", ready.await(2, TimeUnit.SECONDS));
        start.countDown();
        pool.shutdown();
        assertTrue("Workers did not finish in time", pool.awaitTermination(5, TimeUnit.SECONDS));

        long successCount = results.stream().filter(Boolean::booleanValue).count();
        assertEquals("Exactly one registration should win", 1, successCount);

        ClientHandler stored = registry.getHandler(username);
        assertNotNull("Registered handler should be present", stored);

        registry.unregister(username);
    }

    @Test
    public void twoUsersConnectedInSameRoom_ConcurrentBroadcasts_AppearInHistory() throws Exception {
        ChatRoom room = new ChatRoom("race-room");

        TestClient clientA = createConnectedClient("alice");
        TestClient clientB = createConnectedClient("bob");

        assertTrue(room.addMember(clientA.handler));
        assertTrue(room.addMember(clientB.handler));

        Message msgA = new Message("alice", "hello from alice", Instant.now());
        Message msgB = new Message("bob", "hello from bob", Instant.now());

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.submit(() -> {
            ready.countDown();
            start.await();
            room.broadcast(msgA);
            return null;
        });

        pool.submit(() -> {
            ready.countDown();
            start.await();
            room.broadcast(msgB);
            return null;
        });

        assertTrue("Broadcasters did not become ready in time", ready.await(2, TimeUnit.SECONDS));
        start.countDown();
        pool.shutdown();
        assertTrue("Broadcast tasks did not finish in time", pool.awaitTermination(5, TimeUnit.SECONDS));

        List<Message> history = room.getRecentHistory(10);

        long aliceMsgs = history.stream()
                .filter(m -> "alice".equals(m.getSender()) && "hello from alice".equals(m.getText()))
                .count();
        long bobMsgs = history.stream()
                .filter(m -> "bob".equals(m.getSender()) && "hello from bob".equals(m.getText()))
                .count();

        assertEquals("Alice message must be present exactly once", 1, aliceMsgs);
        assertEquals("Bob message must be present exactly once", 1, bobMsgs);
    }

    private TestClient createConnectedClient(String username) throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        Socket clientSocket = new Socket("127.0.0.1", serverSocket.getLocalPort());
        Socket serverSide = serverSocket.accept();

        socketsToClose.add(clientSocket);
        socketsToClose.add(serverSide);
        serverSocket.close();

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(serverSide.getOutputStream()), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        ClientHandler handler = new ClientHandler(serverSide);

        java.lang.reflect.Field usernameField = ClientHandler.class.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(handler, username);

        java.lang.reflect.Field writerField = ClientHandler.class.getDeclaredField("writer");
        writerField.setAccessible(true);
        writerField.set(handler, writer);

        return new TestClient(handler, reader);
    }

    private void clearRegistryUsers() {
        Set<String> users = ClientRegisty.getInstance().listUsers();
        for (String user : new ArrayList<>(users)) {
            ClientRegisty.getInstance().unregister(user);
        }
    }

    private void clearRoomMembers(String roomName) {
        ChatRoom room = ChatRoomManager.getInstance().getRoom(roomName);
        if (room == null) {
            return;
        }

        for (String member : new ArrayList<>(room.listMembers())) {
            room.removeMember(member);
        }
    }

    private static class TestClient {
        private final ClientHandler handler;
        private final BufferedReader reader;

        private TestClient(ClientHandler handler, BufferedReader reader) {
            this.handler = handler;
            this.reader = reader;
        }
    }
}
