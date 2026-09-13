import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static String username;

    public static void main(String[] args) throws IOException {
        startConnection();
    }
    private static void startConnection() throws IOException {
        IO.println("Trying to connect to Chatserver");
        try(Socket socket = new Socket(HOST, PORT); Scanner input = new Scanner(System.in); MessageParser messageParser = new MessageParser(); ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); ) {
            IO.println("Connected to Chatserver");
            IO.println("Command structure looks like this TYPE|TARGET|PAYLOAD ");
            IO.println("If you wish to exit write EXIT||");
            while(true) {
                Message loginMessage = Login(input, messageParser);
                sendMessage(loginMessage, output);
                break;
            }
            while(true) {
                loggedAndConnected(input, messageParser, output);
            }

        } catch (Exception e){
            IO.println(e.getMessage());
        }
    }
    private static Message Login(Scanner input, MessageParser messageParser) {
        IO.println("To continue you have to log in");
        IO.println("Please write LOGIN||*Your username*");
        String stringMessage = input.nextLine();
        checkForExit(stringMessage);
        username = Arrays.toString(stringMessage.split("\\|", 1));
        return messageParser.parseClientStringToMessage(stringMessage, null);
    }
    private static void checkForExit(String stringMessage) {
        String[] parts = stringMessage.split("\\|", 3);
        if (parts[0].equals("EXIT")) {
            if (parts.length != 3) {
                System.out.println("Wrong command format");
            } else {
                System.exit(0);
            }
        }
    }
    private static void loggedAndConnected(Scanner input, MessageParser messageParser, ObjectOutputStream output) {
        welcomeToChatServer();
        String stringMessage = input.nextLine();
        checkForExit(stringMessage);
        try{
        sendMessage(messageParser.parseClientStringToMessage(stringMessage, null), output);} catch (IOException e){
            IO.println(e.getMessage());
        }
    }
    private static void welcomeToChatServer() {
        IO.println("Welcome to Chatserver");
        IO.println("Command available");
        IO.println("----------------------");
        IO.println("TEXT|target|your text");
        IO.println("TEXT signifies the command, target is the chatroom you wish to enter and write to and your text is what you wish to write to the chatroom");
        IO.println("---------------------");
        IO.println("CHATROOMS||");
        IO.println("CHATROOMS signifies the command and gets the server to send you a list of all public chatrooms");
        IO.println("---------------------");
        IO.println("PRIVATE|target|your text");
        IO.println("PRIVATE signifies the command, target is the username fo the person you wish to send a private text to and your text is the text you wish to write to the target");
        IO.println("---------------------");
    }


    private static void sendMessage(Message message, ObjectOutputStream output) throws IOException {
        output.writeObject(message);
        output.flush();
    }

}
