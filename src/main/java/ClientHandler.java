import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            ) {
            System.out.println("Client connected to " + socket.getInetAddress().getHostName() + Thread.currentThread().getName());


        } catch (IOException e){
            System.out.println("Error client connection failed");
        }
    }

    public void username(){

    }


    public void sendMessage(String message, PrintWriter writer){
        writer.println(message);
    }
    public String receiveMessage(BufferedReader reader) throws IOException{
        return reader.readLine();
    }
}
