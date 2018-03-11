package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MulticastListener implements Runnable {
    private MulticastSocket multicastSocket;
    private byte[] receiveBuffer = new byte[2048];
    private DatagramPacket receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);

    public MulticastListener(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        while (!multicastSocket.isClosed()) {
            try {
                multicastSocket.receive(receivePacket);
                System.out.println("Multicast message:");
                System.out.println(new String(
                        Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength()),
                        "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
