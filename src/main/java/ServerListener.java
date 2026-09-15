import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListener implements Runnable {
    ObjectInputStream socketInput;
    MessageParser messageParser;
    String username;
    String chatRoom;

    public ServerListener(ObjectInputStream socketInput, MessageParser messageParser) {
        this.socketInput = socketInput;
        this.messageParser = messageParser;
    }

    @Override
    public void run() {
        listen();
    }

    public void listen() {
        try {
            while (true) {
                    Message message = (Message) socketInput.readObject();
                    if (message.getTarget().equals(username) || message.getTarget().equals(chatRoom) || message.getTarget().equals("ALL")) {
                        IO.println(handleMessage(message));
                    }
            }
        } catch (ClassNotFoundException e) {
            IO.println("Error in ServerListener: " + e.getMessage());

        } catch (IOException e) {
            IO.println("Error in ServerListener: " + e.getMessage());
        }
    }

    private String handleMessage(Message message) {
        String messageInString = messageParser.parseServerMessageToString(message);
        return messageInString;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }
}
