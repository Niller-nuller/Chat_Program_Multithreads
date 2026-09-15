import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 3;
    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(PORT);){
            ExecutorService chatClientPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            System.out.println("Server started on port " + PORT);
            ClientRegisty clientRegisty = new ClientRegisty();
            acceptsClients(serverSocket, chatClientPool, clientRegisty);
        } catch (IOException e){
            System.out.println("Serious Error " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void acceptsClients(ServerSocket serverSocket, ExecutorService chatClientPool, ClientRegisty clientRegisty) {

        try {
            while(true) {
                Socket socket = serverSocket.accept();
                chatClientPool.submit(new ClientHandler(socket, clientRegisty));

            }
        }catch (IOException e){
            System.out.println("Error client connection failed");
        }
    }
}
