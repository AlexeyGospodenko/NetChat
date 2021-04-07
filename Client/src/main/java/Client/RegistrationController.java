package Client;

import Server.Message;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class RegistrationController {
    public AnchorPane window;
    public TextField txtLogin;
    public TextField txtPassword;
    public TextField txtNickname;
    public Button btnExit;
    private boolean checkLogin = false;
    private boolean checkNickname = false;

    public void register() throws Exception {
        if (txtLogin.getText().equals("") || txtPassword.getText().equals("") || txtNickname.getText().equals("")) {
            txtLogin.setStyle(ClientConstants.getActionFail());
            txtPassword.setStyle(ClientConstants.getActionFail());
            txtNickname.setStyle(ClientConstants.getActionFail());
            txtPassword.clear();
        } else {
            ServerService.getInstance().getOs().writeObject(Message.of(ClientConstants.getSystemUser(),
                    ClientConstants.getPrefixRegisterMessage() + txtLogin.getText() + " " + txtPassword.getText() + " " + txtNickname.getText()));
            txtPassword.setStyle(ClientConstants.getActionSuccess());

            Thread readThread = new Thread(() -> {
                try {
                    Message message = (Message) ServerService.getInstance().getIs().readObject();
                    if (message.getMessage().startsWith(ClientConstants.getPrefixRegisterMessage())) {
                        if (message.getMessage().contains(ClientConstants.getLoginBusyMessage())) {
                            txtLogin.setStyle(ClientConstants.getActionFail());
                            txtLogin.clear();
                            checkLogin = false;
                            txtLogin.setPromptText("Login is busy");
                        } else {
                            txtLogin.setStyle(ClientConstants.getActionSuccess());
                            checkLogin = true;
                        }
                        if (message.getMessage().contains(ClientConstants.getNicknameBusyMessage())) {
                            txtNickname.setStyle(ClientConstants.getActionFail());
                            txtNickname.clear();
                            checkNickname = false;
                            txtNickname.setPromptText("Nickname is busy");
                        } else {
                            txtNickname.setStyle(ClientConstants.getActionSuccess());
                            checkNickname = true;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Server was broken");
                }
            });

            readThread.setDaemon(true);
            readThread.start();
            readThread.join();
            if (checkLogin && checkNickname) {
                exit();
            }
        }
    }

    public void exit() throws IOException {
        ServerService.getInstance().destroyInstance();
        new CreateWindow("auth.fxml", "NetChat - Authorization", false);
        window.getScene().getWindow().hide();
    }
}