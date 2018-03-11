package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPListener implements Runnable {
    private DatagramSocket datagramSocket;
    private byte[] receiveBuffer = new byte[2048];
    private DatagramPacket receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);

    public UDPListener(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        while (!datagramSocket.isClosed()) {
            try {
                datagramSocket.receive(receivePacket);
                System.out.println(new String(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength())));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
