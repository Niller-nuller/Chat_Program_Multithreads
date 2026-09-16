import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoom {



    private final List<Message> chatRoomMessages = new CopyOnWriteArrayList<>();
    private final List<ClientHandler> clientMembers = new CopyOnWriteArrayList<>();
    private final String chatRoomName;

    public ChatRoom(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public List<ClientHandler> getClientMembers() {
        return clientMembers;
    }

    public List<Message> getChatRoomMessages() {
        return chatRoomMessages;
    }
    public void addChatRoomMember(ClientHandler chatMember){
        if(chatMember != null && !clientMembers.contains(chatMember)){
            clientMembers.add(chatMember);
        }
    }
    public void removeChatRoomMember(ClientHandler chatMember){
        if(chatMember != null) {
            clientMembers.remove(chatMember);
        }
    }
    public void addChatRoomMessage(Message message){
        if(message!=null){
            chatRoomMessages.add(message);
        }
    }
    //WIP
    //Make it so that the returned value is of strings
    public List<String > getChatRoomHistory(){
        return new CopyOnWriteArrayList<>();
    }

}
