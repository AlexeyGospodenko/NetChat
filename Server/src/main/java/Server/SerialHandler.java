package Server;

import Server.Services.DatabaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

@Component
@Scope("prototype")
public class SerialHandler implements Runnable, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialHandler.class);

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private boolean running;
    private ServerController serverController;
    private String nickname;
    private String login;
    private DatabaseServiceImpl databaseService;
    private Socket socket;

    public SerialHandler(DatabaseServiceImpl databaseService) throws IOException {
        this.databaseService = databaseService;
        running = true;
        System.out.println("DB:" + databaseService.isDbConnect());
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        os = new ObjectOutputStream(socket.getOutputStream());
        is = new ObjectInputStream(socket.getInputStream());
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public void sendMessage(Message message) throws IOException {
        os.writeObject(message);
        os.flush();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Message message = (Message) is.readObject();

                //Управляющие сообщения
                if (message.getUserName().equals(ServerConstants.getSystemUser())) {

                    //Регистрация
                    if (message.getMessage().startsWith(ServerConstants.getPrefixRegisterMessage())) {
                        String[] data = message.getMessage().split(" ", 4);
                        if (data.length == 4) {
                            boolean isLoginExists = databaseService.isLoginExists(data[1]);
                            boolean isNicknameExists = databaseService.isNicknameExists(data[3]);
                            String registrationMessage = "";
                            registrationMessage = registrationMessage + ((isLoginExists) ? ServerConstants.getLoginBusyMessage() : "");
                            registrationMessage = registrationMessage + ((isNicknameExists) ? ServerConstants.getNicknameBusyMessage() : "");
                            if (registrationMessage.equals("")) {
                                registrationMessage = ServerConstants.getIsRegistrationMessage();
                                databaseService.addUser(data[1], data[2], data[3]);
                            }
                            os.writeObject(Message.of(ServerConstants.getSystemUser(), ServerConstants.getPrefixRegisterMessage() + registrationMessage));
                            LOGGER.debug(registrationMessage);
                            continue;
                        }
                    }

                    //Аутентификация и сообщение о присоединение к чату
                    if (message.getMessage().startsWith(ServerConstants.getPrefixAuthMessage())) {
                        String[] data = message.getMessage().split(" ", 3);
                        if (data.length == 3) {
                            nickname = databaseService.auth(data[1], data[2]);
                            if (nickname == null) {
                                os.writeObject(Message.of(ServerConstants.getSystemUser(),
                                        ServerConstants.getPrefixAuthMessage() + ServerConstants.getAuthFailedMessage()));
                                LOGGER.info("Auth failed login={{}}", data[1]);
                            } else {
                                os.writeObject(Message.of(ServerConstants.getSystemUser(), ServerConstants.getPrefixAuthMessage() + nickname));
                                login = data[1];
                                LOGGER.info("Auth success login={{}}", data[1]);
                                Message messageJoin = Message.of(ServerConstants.getSystemUser(), "User \"" + nickname + "\" has joined the channel");
                                serverController.broadCast(messageJoin);
                                serverController.getTxtChat().appendText(messageJoin.getFormattedMessage());
                            }
                        }
                        continue;
                    }
                }

                //Личные сообщения
                if (message.getMessage().startsWith(ServerConstants.getPrefixPrivateMessage())) {
                    String[] data = message.getMessage().substring(ServerConstants.getPrefixPrivateMessage().length()).split(" ", 2);
                    if (data.length == 2) {
                        Message messageTo = Message.of(data[0], "PM from " + nickname + ": " + data[1]);
                        serverController.sendMessageTo(messageTo, data[0]);
                        serverController.getTxtChat().appendText(messageTo.getFormattedMessage());

                        Message messageFrom = Message.of(nickname, "PM to " + data[0] + ": " + data[1]);
                        serverController.sendMessageTo(messageFrom, nickname);
                        serverController.getTxtChat().appendText(messageFrom.getFormattedMessage());

                        LOGGER.debug("Private message from {{}}, to {{}}, message={{}}", nickname, data[0], data[1]);
                        continue;
                    }
                }

                //Смена ника
                if (message.getMessage().toLowerCase().startsWith(ServerConstants.getPrefixChangeNicknameMessage())) {
                    String[] data = message.getMessage().substring(ServerConstants.getPrefixChangeNicknameMessage().length()).split(" ", 2);
                    if (data.length == 1) {
                        if (databaseService.isNicknameExists(data[0])) {
                            Message messageNicknameReserved = Message.of(ServerConstants.getSystemUser(), "Nickname \"" + data[0] + "\" is reserved");
                            os.writeObject(messageNicknameReserved);
                        } else {
                            databaseService.changeNickname(nickname, data[0]);
                            Message messageChangeNickname = Message.of(ServerConstants.getSystemUser(),
                                    "User \"" + nickname + "\" change nickname to \"" + data[0] + "\"");
                            LOGGER.info("User {{}} change nickname to {{}}", nickname, data[0]);
                            nickname = data[0];
                            serverController.broadCast(messageChangeNickname);
                            serverController.getTxtChat().appendText(messageChangeNickname.getFormattedMessage());
                        }
                    }
                    continue;
                }

                //Если обычное сообщение то бродкастим его
                if (message.getUserName().equals(ServerConstants.getCurrentUser())) {
                    message.setUserName(nickname);
                    serverController.broadCast(message);
                    serverController.getTxtChat().appendText(message.getFormattedMessage());
                    LOGGER.info("User {{}} send message for all", nickname);
                    LOGGER.debug("User {{}} send message for all = {{}}", nickname, message.getMessage());
                }
            } catch (IOException | ClassNotFoundException e) {
                if (nickname != null) {
                    Message messageLeft = Message.of(ServerConstants.getSystemUser(), "User \"" + nickname + "\" has left the channel");
                    serverController.getTxtChat().appendText(messageLeft.getFormattedMessage());
                    try {
                        serverController.broadCast(messageLeft);
                    } catch (IOException ioException) {
                        running = false;
                        LOGGER.error(ioException.getMessage() + " login={}", login);
                        for (int i = 0; i < ioException.getStackTrace().length; i++) {
                            LOGGER.debug(String.valueOf(ioException.getStackTrace()[i]));
                        }
                    }
                }
                serverController.kickClient(this);
                break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void close() throws IOException {
        os.close();
        is.close();
    }
}