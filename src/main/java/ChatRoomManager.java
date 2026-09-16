import java.awt.*;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {

    private final ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    public ChatRoomManager() {
        createDefaultChatRoom();
    }
    public void joinRoom(ClientHandler chatClient, String chatRoomName) {

        if (chatClient == null){
            return;
        }

        ChatRoom destination = chatRooms.get(chatRoomName);

        if (destination == null){
            return;
        }

        destination.addChatRoomMember(chatClient);

    }
    public void leaveRoom(ClientHandler chatClient, String chatRoomName) {
        if (chatClient != null){
            return;
        }
    }
    private ChatRoom createChatRoom(String chatRoomName) {
        if (chatRoomName == null) {
            return null;
        }
        else if(chatRooms.containsKey(chatRoomName)){
            return null;
        }
        ChatRoom chatRoom = new ChatRoom(chatRoomName);
        chatRooms.put(chatRoomName, chatRoom);
        return chatRooms.get(chatRoomName);
    }

    private void createDefaultChatRoom() {
        ChatRoom chatRoom = new ChatRoom("Default");
        chatRooms.put("Default", chatRoom);
    }
    public String getChatRooms(){
        return chatRooms.keySet().toString();
    }
    public ChatRoom getChatRoom(String name) {
        return chatRooms.get(name);
    }

    public boolean hasChatRoom(String name) {
        return chatRooms.containsKey(name);
    }

    public void broadcastToRoom(String roomName, String message) throws SocketException {
        ChatRoom chatRoom = chatRooms.get(roomName);
        if (chatRoom == null){
            return;
        }
        for (ClientHandler clientHandler : chatRoom.getClientMembers()){
            clientHandler.sendMessage(message);
        }
    }
}
