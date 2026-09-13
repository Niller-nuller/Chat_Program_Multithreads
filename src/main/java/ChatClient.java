import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", ChatServer.PORT);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            String username = promptForUsername(consoleReader, socketWriter, socketReader);
            if (username == null) {
                return;
            }

            ServerListener serverListener = new ServerListener(socketReader);
            Thread listenerThread = new Thread(serverListener, "ServerListener");
            listenerThread.setDaemon(true);
            listenerThread.start();

            System.out.println("Commands: Join_Room|RoomName, List_Rooms|, or Quit|");
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
               String trimmedInput = userInput.trim();
               if (trimmedInput.isEmpty()) {
                   continue;
               }

               if (isQuitCommand(trimmedInput)) {
                   socketWriter.println("Quit|");
                   break;
               }

               if (trimmedInput.startsWith("Join_Room|") || trimmedInput.equals("List_Rooms|") || trimmedInput.equals("List_Rooms") || trimmedInput.startsWith("List_Users|") || trimmedInput.equals("List_Users")) {
                   socketWriter.println(trimmedInput);
                   continue;
               }

               socketWriter.println(trimmedInput);
            }

            serverListener.stop();
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    private static boolean isQuitCommand(String input) {
        return input.equalsIgnoreCase("Quit|")
               || input.equalsIgnoreCase("Quit")
               || input.equalsIgnoreCase("QUIT|");
    }

    private static String promptForUsername(BufferedReader consoleReader, PrintWriter socketWriter, BufferedReader socketReader) throws IOException {
        while (true) {
            System.out.print("Choose username: ");
            String username = consoleReader.readLine();
            if (username == null || username.isBlank()) {
                System.out.println("Username cannot be empty.");
                continue;
            }

            socketWriter.println("LOGIN|" + username.trim());
            String response = socketReader.readLine();
            if (response == null) {
                return null;
            }

            if (response.startsWith("LOGIN_OK|")) {
                return username.trim();
            }

            if (response.startsWith("ERROR|")) {
                String[] errorParts = response.split("\\|", 4);
                String errorText = errorParts.length >= 4 ? errorParts[3] : (errorParts.length == 2 ? errorParts[1] : response);
                System.out.println("Error: " + errorText);
            }
        }
    }
}
