import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ClientRegisty clientRegisty;
    public ClientHandler(Socket socket, ClientRegisty clientRegisty) {
        this.socket = socket;
        this.clientRegisty = clientRegisty;
    }

    @Override
    public void run() {
        try (MessageParser messageParser = new MessageParser(); ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream socketInput = new ObjectInputStream(socket.getInputStream());) {
            System.out.println("Client connected to " + socket.getInetAddress().getHostName() + Thread.currentThread().getName());

            while (true) {
                Message message = receiveMessage(socketInput);
                handleMessage(message, messageParser, output);
            }
        } catch (IOException e) {
            System.out.println("Error client connection failed");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void username(String[] parts, ObjectOutputStream output)throws IOException {
        Message serverMessage = new Message();
        for(String username : clientRegisty.getClients())
            if(username.equals(parts[3])){
                serverMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
                serverMessage.setType("LOGIN_FAILURE");
                serverMessage.setSender("login service");
                serverMessage.setTarget(parts[3]);
                serverMessage.setPayload("This " + parts[3] + " is already in use");
                sendMessage(serverMessage, output);
            }
        clientRegisty.addClients(parts[3]);
        serverMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
        serverMessage.setType("LOGIN_SUCCESS");
        serverMessage.setSender("login service");
        serverMessage.setTarget(parts[3]);
        serverMessage.setPayload("Welcome " + parts[3]);
        sendMessage(serverMessage, output);
    }


    public void handleMessage(Message message, MessageParser messageParser, ObjectOutputStream output) throws IOException {
        String clientMessageString = messageParser.parseClientMessageToString(message);
        String[] parts = clientMessageString.split("\\|", 4);
        switch (parts[1]) {
            case "LOGIN":
                username(parts, output);
                break;
            case "TEXT":
                break;
            case "PRIVATE":
                break;
            case "CHATROOMS":
                break;
        }
    }

    public void sendMessage(Message message, ObjectOutputStream output) throws IOException {
        output.writeObject(message);
        output.flush();
        IO.println("Message sent");
    }

    public Message receiveMessage(ObjectInputStream socketInput) throws IOException, ClassNotFoundException {
        Message message = (Message) socketInput.readObject();
        return message;
    }
}
