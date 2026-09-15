import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 3;
    private final ClientRegistry clientRegistry;
    private final ChatRoomManager chatRoomManager;
    private final ExecutorService chatMaids;

    public ChatServer() {
        this.chatMaids = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.clientRegistry = new ClientRegistry();
        this.chatRoomManager = new ChatRoomManager();
    }

    public static void main(String[] args) {
        new ChatServer().startServer();

    }

    public void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(PORT);){
            System.out.println("Server started on port " + PORT);

            acceptsClients(serverSocket, chatMaids);
        } catch (IOException e){
            System.out.println("Serious Error " + e.getMessage());
            e.printStackTrace();
        } finally {
            chatMaids.shutdown();
        }
    }
    public void acceptsClients(ServerSocket serverSocket, ExecutorService chatClientPool) {

        try {
            while(true) {
                Socket socket = serverSocket.accept();
                chatClientPool.submit(new ClientHandler(socket, clientRegistry, chatRoomManager));

            }
        }catch (IOException e){
            System.out.println("Error client connection failed");
        }
    }

}
