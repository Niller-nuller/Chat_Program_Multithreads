import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerListener implements Runnable {

    private final BufferedReader in;
    private final boolean running = true;

    public ServerListener(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String serverMessage;
            while (running && (serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException("ServerListener Error: " + e.getMessage());
        }
    }
}
