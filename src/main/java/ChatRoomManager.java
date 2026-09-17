import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {

    private static final String DEFAULT_ROOM_NAME = "general";
    private final Map<String, ChatRoom> rooms = new ConcurrentHashMap<>();

    private static final ChatRoomManager INSTANCE = new ChatRoomManager();

    private ChatRoomManager() {
        rooms.put(DEFAULT_ROOM_NAME, new ChatRoom(DEFAULT_ROOM_NAME));
    }

    public static ChatRoomManager getInstance() {
        return INSTANCE;
    }

    public ChatRoom getOrCreateRoom(String roomName) {
        String normalized = normalizeRoomName(roomName);
        return rooms.computeIfAbsent(normalized, ChatRoom::new);
    }

    public ChatRoom getRoom(String roomName) {
        return rooms.get(normalizeRoomName(roomName));
    }

    public List<String> listRooms() {
        List<String> roomList = new ArrayList<>();
        for (ChatRoom room : rooms.values()) {
            roomList.add(room.getName() + " (" + room.memberCount() + ")");
        }
        return roomList;
    }

    public void removeRoomIfEmpty(String roomName) {
        String normalized = normalizeRoomName(roomName);
        rooms.computeIfPresent(normalized, (name, room) -> room.isEmpty() ? null : room);
    }

    public List<Message> getRecentHistory(int maxMessages) {
        List<Message> recent = new ArrayList<>();
        for (ChatRoom room : rooms.values()) {
            List<Message> roomHistory = room.getRecentHistory(maxMessages);
            for (Message message : roomHistory) {
                recent.add(message);
            }
        }
        int startIndex = Math.max(0, recent.size() - maxMessages);
        return new ArrayList<>(recent.subList(startIndex, recent.size()));
    }

    public int getMemberCount(String roomName) {
        ChatRoom room = getRoom(roomName);
        return room == null ? 0 : room.memberCount();
    }

    public String normalizeRoomName(String roomName) {
        if (roomName == null) {
            return DEFAULT_ROOM_NAME;
        }
        String normalized = roomName.trim();
        return normalized.isEmpty() ? DEFAULT_ROOM_NAME : normalized;
    }
}
