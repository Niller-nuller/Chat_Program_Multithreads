import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private final ArrayList<String> clients;

    public ClientHandler(Socket socket, ArrayList<String> clients) {
        this.socket = socket;
        this.clients = clients;
    }


    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            ) {
            System.out.println("Client connected to " + socket.getInetAddress().getHostName() + Thread.currentThread().getName());
            login(reader,writer);

        } catch (IOException e) {
            System.out.println("Error client connection failed");

        } catch (SocketException e){
            System.out.println(e.getMessage());
        }
    }

    public void login(BufferedReader reader,PrintWriter writer) throws IOException {
        try {
            while (true) {
                sendMessage("Please enter your username", writer);
                String username = receiveMessage(reader);


                switch (reader.readLine()) {

                }


            }
        } catch (IOException e){
            throw new IOException("Login failed");
        }
    }


    public void sendMessage(String message, PrintWriter writer) throws SocketException {
        try {
            writer.println(message);
        } catch (SocketException e){
            throw new SocketException("Message send failed");
        }
    }
    public String receiveMessage(BufferedReader reader) throws IOException{
        try {
            return reader.readLine();
        }  catch (IOException e){
            throw new IOException("Failed to receive message");
        }

    }
}
