package Server;

public class Message {
    private Client client;
    private String message;

    public Message(Client client, String message) {
        this.client = client;
        this.message = message;
    }

    public String getMessageWithClientData() {
        return String.format("%s[%s]: %s",
                client.getNickname(),
                client.getAddress(),
                message);
    }

    public Client getClient() {
        return client;
    }
}
