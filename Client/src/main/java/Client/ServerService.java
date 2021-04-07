package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerService {

    private static ServerService instance;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    public ServerService() {
        createStreams();
    }

    public static ServerService getInstance() {
        if (instance == null) {
            instance = new ServerService();
        }
        return instance;
    }

    private void createStreams() {
        try {
            os = new ObjectOutputStream(ChatSocket.getSocket().getOutputStream());
            is = new ObjectInputStream(ChatSocket.getSocket().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectOutputStream getOs() {
        return os;
    }

    public ObjectInputStream getIs() {
        return is;
    }

    public void destroyInstance() throws IOException {
        is.close();
        os.close();
        ChatSocket.getSocket().close();
        instance = null;
    }
}
