package Server;

import Server.Services.DatabaseServiceImpl;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.ConcurrentLinkedDeque;


public class ServerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    public Button btnStart;
    public Button btnSend;
    public TextField txtPort;
    public TextArea txtLog;
    public TextField txtSend;
    public TextArea txtChat;
    public Button btnCreateScheme;
    public TextField txtJdbcUrl;
    private SerialHandler serialHandler;
    private boolean running;
    private final ConcurrentLinkedDeque<SerialHandler> clients = new ConcurrentLinkedDeque<>();

    public void serverStart() {
        txtLog.appendText(Message.of(ServerConstants.getServerUser(), DatabaseServiceImpl.getInstance().dbConnect()).getFormattedMessage());

        if (DatabaseServiceImpl.getInstance().isDbConnect()) {
            createServerThread();
            btnStart.setDisable(true);
        }
    }

    private void createServerThread() {
        Thread serverThread = new Thread(() -> {
            running = true;
            try (ServerSocket server = new ServerSocket(Integer.parseInt(txtPort.getText()))) {
                txtLog.appendText(Message.of(ServerConstants.getServerUser(), "Server started on port " + txtPort.getText()).getFormattedMessage());
                LOGGER.info("Server started on port: {}", txtPort.getText());
                while (running) {
                    txtLog.appendText(Message.of(ServerConstants.getServerUser(), "Waiting for connection").getFormattedMessage());
                    Socket socket = server.accept();
                    serialHandler = new SerialHandler(socket, this);
                    clients.add(serialHandler);
                    //new Thread(serialHandler).start();
                    ExecService.getInstance().getExecutorService().submit(serialHandler);
                    LOGGER.info("Client accepted. Client info: {}", socket.getInetAddress());
                    txtLog.appendText(Message.of(ServerConstants.getServerUser(), "Client accepted").getFormattedMessage());
                    txtLog.appendText(Message.of(ServerConstants.getServerUser(), "Client info: " + socket.getInetAddress()).getFormattedMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public TextArea getTxtChat() {
        return txtChat;
    }

    public void send() throws IOException {
        Message message = Message.of(ServerConstants.getSystemUser(), txtSend.getText());
        txtSend.clear();
        txtChat.appendText(message.getFormattedMessage());
        broadCast(message);
    }

    public void broadCast(Message message) throws IOException {
        for (SerialHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void sendMessageTo(Message message, String nickname) throws IOException {
        for (SerialHandler client : clients) {
            if (client.getNickname().equals(nickname)) {
                message.setUserName("");
                client.sendMessage(message);
            }
        }
    }

    public void kickClient(SerialHandler client) {
        clients.remove(client);
    }

    public void createScheme() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection(txtJdbcUrl.getText(), "CHAT_AUTH", "CHAT_AUTH");
        Statement statement = connection.createStatement();
        try {
            statement.execute(ServerConstants.getSqlCreateScheme());
            txtLog.appendText(Message.of(ServerConstants.getServerUser(), "Scheme has been successfully created").getFormattedMessage());
        } catch (SQLSyntaxErrorException e) {
            txtLog.appendText(Message.of(ServerConstants.getServerUser(), (e.getMessage())).getFormattedMessage());
        }
    }

}
