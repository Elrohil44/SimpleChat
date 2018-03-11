package Client;

import Server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static int SERVER_PORT = 12345;
    private static String MULTICAST_GROUP = "230.30.30.17";
    private static int MULTICAST_PORT = 54321;
    private String nickname = null;
    private String address = null;

    public Client() {}



    public Client(String nickname, String address) {
        this.nickname = nickname;
        this.address = address;
    }

    public static void main(String[] args) {
        String nickname;
        String address;
        Client client;
        if (args.length == 2) {
            nickname = args[0];
            address = args[1];
            client = new Client(nickname, address);
        } else {
            client = new Client();
        }
        client.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        if (address == null || nickname == null) {
            System.out.print("Set your nickname:\t");
            System.out.flush();
            this.nickname = scanner.nextLine();

            System.out.print("Set server address:\t");
            System.out.flush();
            this.address = scanner.nextLine();
        }

        try {
            System.out.println(String.format("Using server address %s:%d",
                    address, SERVER_PORT));

            Socket tcpSocket = new Socket(InetAddress.getByName(address), SERVER_PORT);
            DatagramSocket datagramSocket = new DatagramSocket(tcpSocket.getLocalSocketAddress());
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);

//            multicastSocket.setLoopbackMode(true);
            multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_GROUP));

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

            TCPListener tcp = new TCPListener(tcpSocket, in);
            UDPListener udp = new UDPListener(datagramSocket);
            MulticastListener multicast = new MulticastListener(multicastSocket);

            Thread tcpListener = new Thread(tcp);
            tcpListener.start();
            Thread udpListener = new Thread(udp);
            udpListener.start();
            Thread multicastListener = new Thread(multicast);
            multicastListener.start();

            PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);

            out.println(String.format("Register client: %s", nickname));

            System.out.println("Registered to the server");

            label:
            while (scanner.hasNextLine()) {
                String cmd = scanner.nextLine();
                switch (cmd) {
                    case "/U": {
                        System.out.println("Write your datagram message, finish with /FINISH\\");
                        byte[] msg = getData(scanner).getBytes();
                        DatagramPacket packet = new DatagramPacket(msg, msg.length, tcpSocket.getRemoteSocketAddress());

                        datagramSocket.send(packet);
                        break;
                    }
                    case "/M": {
                        System.out.println("Write your multicast message, finish with /FINISH\\");
                        byte[] msg = getData(scanner).getBytes();
                        DatagramPacket packet = new DatagramPacket(
                                msg,
                                msg.length,
                                InetAddress.getByName(MULTICAST_GROUP), MULTICAST_PORT
                        );

                        multicastSocket.send(packet);
                        break;
                    }
                    case "/quit":
                        break label;
                    default:
                        out.println(cmd);
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getData(Scanner in) {
        String line;
        StringBuilder msg = new StringBuilder();
        while (!(line = in.nextLine()).equals("/FINISH\\")) {
            msg.append(line).append('\n');
        }
        return msg.toString();
    }
}
