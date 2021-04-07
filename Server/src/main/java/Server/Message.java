package Server;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

    private Date sendTime;
    private String userName;
    private String message;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static Message of(String userName, String message) {
        Message m = new Message();
        m.setUserName(userName);
        m.setMessage(message);
        m.setSendTime(new Date());
        return m;
    }

    public Message() {
    }

    public void setSendTime(Date time) {
        this.sendTime = time;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getUserName() {
        return userName;
    }

    public String getFormattedMessage() {
        return formatter.format(sendTime) + " " +
                ((!userName.equals("")) ? userName + ": " : "") +
                message + '\n';
    }

    @Override
    public String toString() {
        return "Message{" +
                "sendTime=" + sendTime +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
