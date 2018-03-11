package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private MessagingService messagingService;

    public ClientHandler(Socket clientSocket, MessagingService messagingService) {
        this.socket = clientSocket;
        this.messagingService = messagingService;
    }

    private Client registerClient(String applicationForm) throws IOException {
        if (applicationForm == null) return null;
        if (applicationForm.matches("^Register client: .*$")) {
            Client client = new Client(socket, applicationForm.substring(17));
            messagingService.addClient(client);
            System.out.println(String.format("Client %s successfully registered", client.getNickname()));
            return client;
        }

        return null;
    }

    @Override
    public void run() {
        System.out.println(String.format("Received connection from %s:%d",
                socket.getInetAddress().getHostAddress(), socket.getPort()));

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Client client;
            if ((client = registerClient(in.readLine())) != null) {
                String message;
                while ((message = in.readLine()) != null) {
                    messagingService.addMessage(new Message(client, message));
                }
            } else {
                System.out.println(String.format("Refusing connection for client from %s:%d",
                        socket.getInetAddress().getHostAddress(), socket.getPort()));
                socket.close();
                return;
            }

            System.out.println(String.format("Client %s disconnected", client.getNickname()));
            messagingService.removeClient(client);
            messagingService.addMessage(
                    new Message(
                            client,
                            "(Connection closed)"
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
