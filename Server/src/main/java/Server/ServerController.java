package Server;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
    private boolean running;
    private ConcurrentLinkedDeque<SerialHandler> clients = new ConcurrentLinkedDeque<>();

    public void serverStart() {
            createServerThread();
            btnStart.setDisable(true);
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
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    SerialHandler serialHandler = context.getBean("serialHandler", SerialHandler.class);
                    serialHandler.setServerController(this); //Плохо, не понятно как передать текущий инстанс в спринге
                    serialHandler.setSocket(socket);         //Плохо, не понятно как передать сокет в спринге
                    clients.add(serialHandler);
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
