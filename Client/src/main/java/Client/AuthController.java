package Client;

import Server.Message;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class AuthController {

    public TextField txtLogin;
    public TextField txtPassword;
    public AnchorPane window;
    public TextField txtHost;
    public TextField txtPort;
    private boolean isAuth = false;

    public void enter() throws Exception {
        createConnect();

        ServerService.getInstance().getOs().writeObject(Message.of(ClientConstants.getSystemUser(),
                ClientConstants.getPrefixAuthMessage() + txtLogin.getText() + " " + txtPassword.getText()));

        Thread readThread = new Thread(() -> {
            try {
                Message message = (Message) ServerService.getInstance().getIs().readObject();
                if (message.getMessage().startsWith(ClientConstants.getPrefixAuthMessage())) {
                    if (message.getMessage().contains(ClientConstants.getAuthFailedMessage())) {
                        txtLogin.setStyle(ClientConstants.getActionFail());
                        txtPassword.setStyle(ClientConstants.getActionFail());
                        txtPassword.clear();
                    } else {
                        txtLogin.setStyle(ClientConstants.getActionSuccess());
                        txtPassword.setStyle(ClientConstants.getActionSuccess());
                        isAuth = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Server was broken");
            }
        });

        readThread.setDaemon(true);
        readThread.start();
        readThread.join();

        if (isAuth) {
            Client.HistoryServiceImpl.getInstance().setLogin(txtLogin.getText());
            new CreateWindow("chat.fxml", "NetChat - Login: " + txtLogin.getText(), true);
            window.getScene().getWindow().hide();
        }
    }

    public void register() throws IOException {
        createConnect();
        new CreateWindow("registration.fxml", "NetChat - Registration", false);
        window.getScene().getWindow().hide();
    }

    private void createConnect() throws IOException {
        try {
            new ChatSocket(new Socket(txtHost.getText(), Integer.parseInt(txtPort.getText())));
            ServerService.getInstance();
        } catch (ConnectException e) {
            txtHost.setStyle(ClientConstants.getActionFail());
            txtPort.setStyle(ClientConstants.getActionFail());
        }
    }

}