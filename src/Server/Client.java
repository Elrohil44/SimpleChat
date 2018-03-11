package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private String nickname;

    public Client(Socket socket, String nickname) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.nickname = nickname;
    }

    public void write(String msg) {
        out.println(msg);
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return String.format("%s:%d", socket.getInetAddress().getHostAddress(), socket.getPort());
    }

    public SocketAddress getSocketAddress() {
        return socket.getRemoteSocketAddress();
    }
}
