package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessagingService implements Runnable {
    private ConcurrentLinkedDeque<Message> messageQueue = new ConcurrentLinkedDeque<>();
    private CopyOnWriteArrayList<Client> registeredClients = new CopyOnWriteArrayList<>();

    private ReentrantLock messagesLock = new ReentrantLock();
    private Condition notEmpty = messagesLock.newCondition();

    private boolean running = true;

    public MessagingService() {}

    public void addClient(Client c) {
        registeredClients.add(c);
    }

    public void removeClient(Client c) {
        registeredClients.remove(c);
    }

    public void addMessage(Message msg) {
        messagesLock.lock();
        messageQueue.addLast(msg);
        notEmpty.signalAll();
        messagesLock.unlock();
    }

    public Optional<Client> findClientByAddress(InetAddress address, int portNumber) {
        String addr = String.format("%s:%d", address.getHostAddress(), portNumber);
        return registeredClients
                .stream()
                .filter(client -> client.getAddress().equals(addr))
                .findFirst();
    }

    public void sendMessageOverUdp(Message msg, DatagramSocket socket) {
        byte[] buffer = msg.getMessageWithClientData().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        registeredClients.forEach(client -> {
            if (client != msg.getClient()) {
                packet.setSocketAddress(client.getSocketAddress());
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    System.out.println("Couldn't send message over UDP");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run() {
        while (running) {
            messagesLock.lock();
            while (messageQueue.isEmpty()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted!");
                    running = false;
                }
            }

            while (!messageQueue.isEmpty()) {
                Message msg = messageQueue.pollFirst();
                registeredClients.forEach(client -> {
                    if (client != msg.getClient()) {
                        client.write(msg.getMessageWithClientData());
                    }
                });
            }

            messagesLock.unlock();
        }
    }
}
