import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoom {

    private final String roomName;
    private final List<ClientHandler> members = new CopyOnWriteArrayList<>();
    private final List<String> chatHistory = new CopyOnWriteArrayList<>();

    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<ClientHandler> getMembers() {
        return members;
    }

    public List<String> getChatHistory() {
        return chatHistory;
    }

    public void addMember(ClientHandler clientHandler) {
        if (clientHandler != null && !members.contains(clientHandler)) {
            members.add(clientHandler);
        }
    }

    public void removeMember(ClientHandler clientHandler) {
        if (clientHandler != null) {
            members.remove(clientHandler);
        }
    }

    public void addMessageToHistory(String message) {
        if (message != null && !message.isBlank()) {
            chatHistory.add(message);
        }
    }

    public List<String> getHistorySnapshot() {
        return new CopyOnWriteArrayList<>(chatHistory);
    }
}
