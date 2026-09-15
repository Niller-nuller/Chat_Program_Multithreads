import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private final ClientRegistry clientRegistry;
    private final ChatRoomManager chatRoomManager;
    private String clientUsername;

    public ClientHandler(Socket socket, ClientRegistry clientRegistry, ChatRoomManager chatRoomManager) {
        this.socket = socket;
        this.clientRegistry = clientRegistry;
        this.chatRoomManager = chatRoomManager;
    }


    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            ) {

            System.out.println("Client connected to " + socket.getInetAddress().getHostName() + Thread.currentThread().getName());

            login(reader,writer);



        } catch (IOException e) {
            System.out.println("Error client connection failed");
            clientRegistry.removeClientFromActiveClients(clientUsername);
        }
    }

    public void login(BufferedReader reader,PrintWriter writer) throws IOException {

        try {
            while(true) {
                String username = receiveMessage(reader);

                if (checkUsernameMessageStatus(username)) {

                    sendMessage("EMPTY|Username is empty, please try again", writer);
                } else if (checkUsernameAvailable(username)) {

                    sendMessage("IN USE|Username already in use, please choose a different one", writer);
                } else {

                    clientUsername = username;

                    clientRegistry.registerClient(username, this);

                    sendMessage("ACCEPTED|Welcome to the server", writer);
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

    public void sendMessage(String message, PrintWriter writer) throws SocketException {
        writer.println(message);
    }

    public String receiveMessage(BufferedReader reader) throws IOException{
        try {
            return reader.readLine();

        }  catch (IOException e){
            throw new IOException("Failed to receive message");
        }
    }

    public void chatRoomLoop(){



    }

}
