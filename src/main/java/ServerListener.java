import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerListener implements Runnable {
    private final BufferedReader serverReader;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public ServerListener(BufferedReader serverReader) {
        this.serverReader = serverReader;
    }

    @Override
    public void run() {
        try {
            String incomingMessage;
            while (running.get() && (incomingMessage = serverReader.readLine()) != null) {
                System.out.println(incomingMessage);
            }
        } catch (IOException e) {
            if (running.get()) {
                System.out.println("Connection closed by server.");
            }
        }
    }

    public void stop() {
        running.set(false);
    }
}
