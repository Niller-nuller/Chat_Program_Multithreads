import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    public static final int PORT = 5000;

    private final ExecutorService executorService;
    private final ClientRegistry clientRegistry;
    private final ChatRoomManager chatRoomManager;

    public ChatServer() {
        this.executorService = Executors.newCachedThreadPool();
        this.clientRegistry = new ClientRegistry();
        this.chatRoomManager = new ChatRoomManager();
    }

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for client connections...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostName());
                executorService.submit(new ClientHandler(socket, clientRegistry, chatRoomManager));
            }
        } catch (IOException e) {
            System.out.println("Serious Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
