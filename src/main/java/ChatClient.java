import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static final int PORT = 5000;
    private static final String HOST = "localhost";

    public ChatClient() {}

    public static void main(String[] args){
        new ChatClient().startClient();

    }
    public void startClient() {
        try (Socket socket = new Socket(HOST,PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner input = new Scanner(System.in);){

            System.out.println("Connecting to " + HOST + ":" + PORT);

            login(in,out,input);

            startServerListener(in);

            mainLoop();

        } catch (IOException e) {
            System.out.println("Connection failed");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void login(BufferedReader in, PrintWriter out, Scanner input) throws IOException {
        try {
            while (true) {

                String username = getClientUsername(input);

                sendMessage(username,out);

                String serverResponse = receiveMessage(in);

                switch (parseMessageType(serverResponse)) {
                    case "EMPTY", "IN USE" -> {
                        System.out.println(paraseMessagePayload(serverResponse));
                    }
                    case "ACCEPTED" -> {
                        System.out.println(paraseMessagePayload(serverResponse));
                        return;
                    }
                }
            }

        }  catch (IOException e) {
            throw new IOException("Server Connection lost");
        }finally {
            out.close();
            in.close();

        }
    }
    public String getClientUsername(Scanner scanner) throws IOException {

        System.out.println("Please enter your username: ");

        return scanner.nextLine();

    }
    public void sendMessage(String message, PrintWriter output) throws IOException {
        try {
            output.println(message);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    public String receiveMessage(BufferedReader in) throws IOException {
        return in.readLine();
    }

    public String parseMessageType(String message){
        String[] parts = message.split("\\|", 2);
        if(parts.length != 2 || parts[0].isBlank()){
            throw new IllegalArgumentException("ERROR message could not be parsed");
        }
        return parts[0];
    }
    public String paraseMessagePayload(String message){
        String[] parts = message.split("\\|", 2);
        if(parts.length != 2 || parts[0].isBlank()){
            throw new IllegalArgumentException("ERROR message could not be parsed");
        }
        return parts[1];
    }

    public void startServerListener(BufferedReader in){
        ServerListener serverListener = new ServerListener(in);
        Thread listenerThread = new Thread(serverListener, "ServerListener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void mainLoop(){
        while(true){
            System.out.println("Welcome to Chat Client");
        }
    }

}
