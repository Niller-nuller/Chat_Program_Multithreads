import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {

    private final ConcurrentHashMap<String, ClientHandler> activeClients = new ConcurrentHashMap<>();
    private final Set<String> clientUsernames =  ConcurrentHashMap.newKeySet();


    public synchronized void registerClient(String username, ClientHandler clientHandler) {

        activeClients.put(username, clientHandler);
        clientUsernames.add(username);
    }

    public synchronized boolean checkUsernameAvailability(String username){
        if(clientUsernames.contains(username)){
            if(activeClients.containsKey(username)){
                return true;
            }
            return false;
        }
        return false;
    }
    public synchronized void removeClientFromActiveClients(String username){
        activeClients.remove(username);
    }
    public synchronized boolean isUsernameActive(String username) {
        return activeClients.containsKey(username);
    }
}
