import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        try (Socket socket = new Socket(host, port);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            // Thread to print messages from server
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    // Connection closed or error
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            // Read from console and send to server
            String input;
            while ((input = console.readLine()) != null) {
                input = input.trim();
                if (input.isEmpty()) continue;
                if (input.equalsIgnoreCase("/quit") || input.equalsIgnoreCase("/exit")) {
                    break;
                }
                serverOut.println(input);
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }

        System.out.println("Client exiting");
    }
}
