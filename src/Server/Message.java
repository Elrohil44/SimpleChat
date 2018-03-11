package Server;

import java.util.Scanner;

public class Message {
    private Client client;
    private String message;

    public Message(Client client, String message) {
        this.client = client;
        this.message = message;
    }

    public String getMessageWithClientData() {
        Scanner scanner = new Scanner(message);
        String clientData = String.format("%s[%s]",
                client.getNickname(),
                client.getAddress());
        StringBuilder msg = new StringBuilder();
        while (scanner.hasNextLine()) {
            msg.append(String.format("%s: %s\n", clientData, scanner.nextLine()));
        }
        return msg.toString();
    }

    public Client getClient() {
        return client;
    }
}
