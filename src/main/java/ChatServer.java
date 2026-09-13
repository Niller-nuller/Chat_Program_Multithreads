import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for a client connection...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket.getInetAddress().getHostName());
            socket.close();
        } catch (IOException e) {
            System.out.println("Serious Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
