package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Optional;

public class UdpService implements Runnable{
    private DatagramSocket socket;
    private MessagingService messagingService;

    public UdpService(DatagramSocket socket, MessagingService messagingService) {
        this.socket = socket;
        this.messagingService = messagingService;
    }

    @Override
    public void run() {
        byte[] buf = new byte[2048];
        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                socket.receive(receivePacket);
                Optional<Client> client = messagingService.findClientByAddress(
                        receivePacket.getAddress(), receivePacket.getPort());
                if (client.isPresent()) {
                    messagingService.sendMessageOverUdp(new Message(
                            client.get(), new String(Arrays.copyOfRange(
                                    receivePacket.getData(),
                            0, receivePacket.getLength()), "UTF-8")), socket);
                } else {
                    System.out.println("Client not found!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
