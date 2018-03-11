package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPListener implements Runnable {
    private Socket socket;
    private BufferedReader in = null;

    public TCPListener(Socket socket, BufferedReader in) {
        this.socket = socket;
        this.in = in;
    }

    @Override
    public void run() {
        if (in == null) {
            return;
        }
        while (!socket.isClosed()) {
            try {
                String msg = in.readLine();
                if (msg == null) {
                    break;
                }
                System.out.println(msg);
                System.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
