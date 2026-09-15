import java.util.ArrayList;

public class ClientRegisty {
    private ArrayList<String> clients;

    public ClientRegisty() {
        this.clients = new ArrayList<>();
    }
    public ArrayList<String> getClients() {
        return clients;
    }
    public void addClients(String username) {
        clients.add(username);
    }
    public void removeClients(String username) {
        clients.remove(username);
    }
}
