import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatRoom {
    private final String name;
    private final ConcurrentHashMap<String, ClientHandler> members = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<Message> history = new ConcurrentLinkedDeque<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean addMember(ClientHandler client) {
        ClientHandler previous = members.putIfAbsent(client.getUsername(), client);
        if (previous != null) {
            return false;
        }
        broadcastSystem(client.getUsername() + " joined the room");
        return true;
    }

    public boolean removeMember(String username) {
        ClientHandler removed = members.remove(username);
        if (removed != null) {
            broadcastSystem(username + " left the room");
            return true;
        }
        return false;
    }

    public void broadcast(Message message) {
        history.addLast(message);
        String formatted = message.toString();
        for (ClientHandler client : members.values()) {
            client.send(formatted);
        }
    }

    public void broadcastSystem(String text) {
        broadcast(new Message("SYSTEM", text, Instant.now()));
    }

    public List<Message> getRecentHistory(int maxMessages) {
        if (maxMessages <= 0 || history.isEmpty()) {
            return new ArrayList<>();
        }

        List<Message> allMessages = new ArrayList<>(history);
        int startIndex = Math.max(0, allMessages.size() - maxMessages);
        return new ArrayList<>(allMessages.subList(startIndex, allMessages.size()));
    }

    public int memberCount() {
        return members.size();
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public Set<String> listMembers() {
        return members.keySet();
    }
}
