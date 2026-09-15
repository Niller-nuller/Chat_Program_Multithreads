import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {

    private final static String defaultChatRoom = "Default";

    private final ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    public ChatRoomManager() {

    }
    public void joinRoom(ClientHandler chatClient, String chatRoomName) {
        if (chatClient != null){
            return;
        }
        String parsedChatRoomName;

    }
    public void leaveRoom(ClientHandler chatClient, String chatRoomName) {
        if (chatClient != null){
            return;
        }
    }
//    public List<String> getChatRoomNames(){
//
//        List<String> chatRoomNames = new ArrayList<>();
//
//        return chatRoomNames;
//    }
}
