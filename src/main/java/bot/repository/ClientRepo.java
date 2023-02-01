package bot.repository;

import bot.models.Client;

import java.util.HashMap;
import java.util.Map;

public class ClientRepo {

    private Map<String, Client> clients = new HashMap<>();

    public Map<String, Client> getClients() {
        return clients;
    }

    public void setClients(Map<String, Client> clients) {
        this.clients = clients;
    }

    public void addClient(Client client) {
        clients.put(client.getChatId(), client);
    }
}
