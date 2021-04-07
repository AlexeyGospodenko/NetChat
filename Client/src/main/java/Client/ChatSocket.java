package Client;

import java.net.Socket;

public class ChatSocket {
    private static Socket socket;

    public ChatSocket(Socket socket) {
        ChatSocket.socket = socket;
    }

    public static Socket getSocket() {
        return socket;
    }
}
