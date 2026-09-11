import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatRoomManager {

    private final ConcurrentHashMap<String, ClientHandler> members = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<Message> messageHistory = new ConcurrentLinkedDeque<>();

    private static final ChatRoomManager INSTANCE = new ChatRoomManager();

    private ChatRoomManager() {}

    public static ChatRoomManager getInstance() {
        return INSTANCE;
    }

    public boolean addMember(String username, ClientHandler handler) {
        ClientHandler existing = members.putIfAbsent(username, handler);
        if (existing != null) return false;
        broadcastSystem(username + " joined the room");
        return true;
    }

    public void removeMember(String username) {
        if (members.remove(username) != null) {
            broadcastSystem(username + " left the room");
        }
    }

    public void broadcast(Message msg) {
        // record
        messageHistory.addLast(msg);
        String formatted = format(msg);
        // send to all
        for (ClientHandler ch : members.values()) {
            try {
                ch.send(formatted);
            } catch (Exception ignored) {}
        }
    }

    private void broadcastSystem(String text) {
        Message m = new Message("SYSTEM", text, Instant.now());
        broadcast(m);
    }

    private String format(Message m) {
        return m.toString();
    }

    public List<Message> getRecentHistory(int n) {
        List<Message> out = new ArrayList<>();
        int sizeEstimate = messageHistory.size();
        int skip = Math.max(0, sizeEstimate - n);
        int i = 0;
        for (Message m : messageHistory) {
            if (i++ < skip) continue;
            out.add(m);
        }
        return out;
    }

    public int memberCount() { return members.size(); }
}
