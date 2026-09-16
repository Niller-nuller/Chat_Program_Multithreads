import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private final ClientRegistry clientRegistry;
    private final ChatRoomManager chatRoomManager;
    private String clientUsername;
    private String currentChatRoom;
    private PrintWriter writer;
    private String currentSyntax;
    private boolean running = true;

    public ClientHandler(Socket socket, ClientRegistry clientRegistry, ChatRoomManager chatRoomManager) {
        this.socket = socket;
        this.clientRegistry = clientRegistry;
        this.chatRoomManager = chatRoomManager;
    }


    @Override
    public void run() {
        try
             {
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 setWriter(new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true));
                 System.out.println("Client connected to " + socket.getInetAddress().getHostName() + Thread.currentThread().getName());
                login(reader);
                chatRoomLoop(reader);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Error client connection failed");
            clientRegistry.removeClientFromActiveClients(clientUsername);
        }
    }


    public void login(BufferedReader reader) throws IOException {
        sendMessage("You need to login, please send your username");
        try {
            while(true) {
                String username = receiveMessage(reader);

                if (checkUsernameMessageStatus(username)) {

                    sendMessage("EMPTY|Username is empty, please try again");
                } else if (checkUsernameAvailable(username)) {

                    sendMessage("IN USE|Username already in use, please choose a different one");
                } else {

                    clientUsername = username;

                    clientRegistry.registerClient(username, this);

                    sendMessage("ACCEPTED|Welcome to the server");
                    break;
                }

            }
        } catch (IOException e){
            throw new IOException("Login failed");
        }
    }

    public boolean checkUsernameMessageStatus(String username) throws IOException {
        if (username == null || username.equals("") || username.isEmpty()){
            return true;
        }
        return false;
    }

    public boolean checkUsernameAvailable(String username) throws IOException {

        return clientRegistry.checkUsernameAvailability(username);
    }

    public void sendMessage(String message) throws SocketException {
        writer.println(message);
    }

    public String receiveMessage(BufferedReader reader) throws IOException{
        try {
            return reader.readLine();

        }  catch (IOException e){
            throw new IOException("Failed to receive message");
        }
    }

    public void chatRoomLoop(BufferedReader reader) throws IOException {

        setNewJoins();
        String input;
        while(running && (input = reader.readLine()) != null) {
            input = receiveMessage(reader);
            switch(input){
                case "1" ->
            }
        }
    }
    private void defaultCommands(BufferedReader reader) throws IOException {
        String listOfCommands = "Press 1 for command list. rooms to see list of available chat rooms";
    }
    private void setNewJoins() throws SocketException {
        try {
            chatRoomManager.joinRoom(this, "Default");
            setCurrentChatRoom("Default");
            setCurrentSyntax("LOGIN| " + clientUsername);
            chatRoomManager.broadcastToRoom(currentChatRoom, getCurrentSyntax());
        } catch(SocketException e){
            throw new SocketException("Failed to connect client to default chat room");
        }
    }
    public void setCurrentChatRoom(String currentChatRoom) {
        this.currentChatRoom = currentChatRoom;
    }
    public void setCurrentSyntax(String currentSyntax){
        this.currentSyntax = currentSyntax;
    }
    public String getCurrentSyntax(){
        return currentSyntax;
    }
    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
}
