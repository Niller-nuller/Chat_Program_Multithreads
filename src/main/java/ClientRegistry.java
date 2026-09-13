import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientRegistry {

    private final ConcurrentMap<String, ClientHandler> activeClients = new ConcurrentHashMap<>();
    private final Set<String> previousUsernames = ConcurrentHashMap.newKeySet();

    public synchronized boolean register(String username, ClientHandler clientHandler) {
        if (username == null || username.isBlank() || clientHandler == null) {
            return false;
        }

        String normalizedUsername = normalizeUsername(username);
        if (activeClients.containsKey(normalizedUsername)) {
            return false;
        }

        activeClients.put(normalizedUsername, clientHandler);
        previousUsernames.add(normalizedUsername);
        return true;
    }

    public synchronized void unregister(String username) {
        if (username == null || username.isBlank()) {
            return;
        }

        String normalizedUsername = normalizeUsername(username);
        activeClients.remove(normalizedUsername);
    }

    public synchronized boolean isUsernameTaken(String username) {
        if (username == null || username.isBlank()) {
            return true;
        }

        return activeClients.containsKey(normalizeUsername(username));
    }

    public synchronized Collection<ClientHandler> getActiveClients() {
        return activeClients.values();
    }

    public synchronized Collection<String> getActiveUsernames() {
        return activeClients.keySet();
    }

    public synchronized ClientHandler getClient(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return activeClients.get(normalizeUsername(username));
    }

    public synchronized Set<String> getPreviousUsernames() {
        return previousUsernames;
    }

    private String normalizeUsername(String username) {
        return username.trim();
    }
}
