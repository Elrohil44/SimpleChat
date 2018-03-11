package Server;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static int SERVER_PORT = 12345;
    private ExecutorService pool = Executors.newCachedThreadPool();


    public static void main(String[] args) {
        Server server = new Server();

        server.start();
    }

    public void start() {
        try {
            ServerSocket serverTcpSocket = new ServerSocket(SERVER_PORT);
            DatagramSocket serverUdpSocket = new DatagramSocket(SERVER_PORT);

            MessagingService messagingService = new MessagingService();
            pool.execute(messagingService);

            UdpService udpService = new UdpService(serverUdpSocket, messagingService);
            pool.execute(udpService);


            System.out.println(String.format("Server started on %s:%d",
                    serverTcpSocket.getInetAddress().getHostAddress(),
                    serverTcpSocket.getLocalPort()));

            while (true) {
                Socket clientSocket = serverTcpSocket.accept();
                pool.execute(new ClientHandler(clientSocket, messagingService));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
