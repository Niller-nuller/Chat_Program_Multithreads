import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class ClientHandler implements Runnable{

    private Socket socket;
    private String username;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            System.out.println("Client connected to " + socket.getInetAddress().getHostName() + " " + Thread.currentThread().getName());

            // Ask for username and register
            send("Enter username:");
            while (true) {
                String requested = receiveMessage(reader);
                if (requested == null) {
                    // client disconnected before providing username
                    return;
                }
                requested = requested.trim();
                if (requested.isEmpty()) {
                    send("Username cannot be empty. Try again:");
                    continue;
                }
                boolean ok = ClientRegisty.getInstance().register(requested, this);
                if (ok) {
                    this.username = requested;
                    send("Welcome, " + requested + "!");
                    // register with chatroom manager
                    ChatRoomManager.getInstance().addMember(this.username, this);
                    // send recent history
                    List<Message> history = ChatRoomManager.getInstance().getRecentHistory(10);
                    if (!history.isEmpty()) {
                        send("--- Recent messages ---");
                        for (Message m : history) {
                            send(m.toString());
                        }
                        send("--- End of history ---");
                    }
                    break;
                } else {
                    send("Username already taken. Try another:");
                }
            }

            // Main loop: read and broadcast
            String line;
            while ((line = receiveMessage(reader)) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("/history")) {
                    List<Message> history = ChatRoomManager.getInstance().getRecentHistory(20);
                    for (Message m : history) send(m.toString());
                    continue;
                }
                Message msg = new Message(this.username, line, Instant.now());
                ChatRoomManager.getInstance().broadcast(msg);
            }

        } catch (IOException e){
            System.out.println("Error client connection failed: " + e.getMessage());
        } finally {
            // cleanup
            if (this.username != null) {
                ClientRegisty.getInstance().unregister(this.username);
                ChatRoomManager.getInstance().removeMember(this.username);
            }
            if (this.writer != null) this.writer.close();
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    public void username(){

    }

    public void send(String message){
        if (this.writer != null) {
            this.writer.println(message);
        }
    }

    public void sendMessage(String message, PrintWriter writer){
        writer.println(message);
    }
    public String receiveMessage(BufferedReader reader) throws IOException{
        return reader.readLine();
    }
}
