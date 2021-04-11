package Client;

import Server.Message;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    public AnchorPane window;
    public TextArea txtChat;
    public TextField txtSend;
    public Button btnSend;

    HistoryServiceImpl historyService;

    public void send() {
        Message message = Message.of(Client.ClientConstants.getCurrentUser(), txtSend.getText());
        try {
            ServerService.getInstance().getOs().writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtSend.clear();
    }

    public void exit() throws Exception {
        new CreateWindow("auth.fxml", "NetChat - Authorization", false);
        window.getScene().getWindow().hide();
    }

    public void close() {
        window.getScene().getWindow().hide();
    }

    public void initialize(URL location, ResourceBundle resources) {
        try {
            historyService.getHistory(10).forEach(histMessage -> txtChat.appendText(histMessage + "\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        createReadThread();
    }

    private void createReadThread() {
        Thread readThread = new Thread(() -> {
            try {
                while (true) {
                    Message message = (Message) ServerService.getInstance().getIs().readObject();
                    historyService.saveMessage(message.getFormattedMessage());
                    txtChat.appendText(message.getFormattedMessage());
                }
            } catch (Exception e) {
                System.out.println("Server was broken");
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    @Autowired
    public void setHistoryService(HistoryServiceImpl historyService) {
        this.historyService = historyService;
    }
}