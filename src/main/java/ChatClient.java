import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static final int PORT = 5000;
    private static final String HOST = "localhost";
    private ServerListener serverListener;
    private Thread listenerThread;


    public ChatClient() {}

    public static void main(String[] args) throws IOException {
        new ChatClient().startClient();

    }
    public void startClient() throws IOException {
        try (Socket socket = new Socket(HOST,PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner input = new Scanner(System.in)
             ;){

            System.out.println("Connecting to " + HOST + ":" + PORT);

            startServerListener(in);

            mainLoop(out,input);


        } catch (IOException e) {
            System.out.println("Connection failed");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally{
            stopServerListener();

        }
    }

    public void mainLoop(PrintWriter out, Scanner in) throws IOException {
        while(true){
            String clientMessage = in.nextLine();
            sendMessage(clientMessage, out);
            if(clientMessage.equals("exit")){
                break;
            }
        }
    }

    public void sendMessage(String message, PrintWriter output) throws IOException {
        try {
            output.println(message);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void startServerListener(BufferedReader in) throws IOException {
        serverListener = new ServerListener(in);
        listenerThread = new Thread(serverListener);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    public void stopServerListener(){
        if (listenerThread != null){
            listenerThread.interrupt();
            serverListener.stopServerListener();
        }
    }
}
