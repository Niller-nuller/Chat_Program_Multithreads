import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegisty {

    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    private static final ClientRegisty INSTANCE = new ClientRegisty();

    private ClientRegisty() {}

    public static ClientRegisty getInstance() {
        return INSTANCE;
    }

    /**
     * Register a username with its handler. Returns true if registration succeeded (username was free).
     */
    public boolean register(String username, ClientHandler handler) {
        return clients.putIfAbsent(username, handler) == null;
    }

    public void unregister(String username) {
        clients.remove(username);
    }

    public ClientHandler getHandler(String username) {
        return clients.get(username);
    }

    public Set<String> listUsers() {
        return clients.keySet();
    }
}
