package Server;

public class ServerConstants {
    private final static String SYSTEM_USER = "SYSTEM";
    private final static String SERVER_USER = "SERVER";
    private final static String CURRENT_USER = "USER";

    private final static String PREFIX_REGISTER_MESSAGE = "/REGISTER ";
    private final static String PREFIX_AUTH_MESSAGE = "/AUTH ";
    private final static String PREFIX_PRIVATE_MESSAGE = "/w ";
    private final static String PREFIX_CHANGE_NICKNAME_MESSAGE = "/changenickname ";

    private final static String AUTH_FAILED_MESSAGE = "login or password is not valid\n";
    private final static String LOGIN_BUSY_MESSAGE = "Login or password is \n";
    private final static String NICKNAME_BUSY_MESSAGE = "Nickname is busy\n";
    private final static String IS_REGISTRATION_MESSAGE = "true";

    private final static String SQL_CREATE_SCHEME = "DECLARE \n" +
            "   V_SQL VARCHAR2(4000);\n" +
            "\n" +
            "BEGIN\n" +
            "  \n" +
            "  V_SQL := 'CREATE TABLE USER_DAO (USER_ID NUMBER, \n" +
            "                      LOGIN VARCHAR2(30) NOT NULL UNIQUE, \n" +
            "                      PASSWORD VARCHAR2(30) NOT NULL, \n" +
            "                      NICKNAME VARCHAR2(30) NOT NULL UNIQUE,\n" +
            "                      CONSTRAINT USER_ID_PK PRIMARY KEY (USER_ID))';\n" +
            "                      \n" +
            "   EXECUTE IMMEDIATE V_SQL;\n" +
            "   \n" +
            "   V_SQL := 'CREATE SEQUENCE USER_ID_SEQ START WITH 1';\n" +
            "\n" +
            "   EXECUTE IMMEDIATE V_SQL;\n" +
            "   \n" +
            "   V_SQL := 'CREATE OR REPLACE TRIGGER USER_DAO_USER_ID_TRG \n" +
            "   BEFORE INSERT ON USER_DAO \n" +
            "   FOR EACH ROW\n" +
            "   BEGIN\n" +
            "     SELECT USER_ID_SEQ.NEXTVAL\n" +
            "       INTO   :NEW.USER_ID\n" +
            "       FROM   DUAL;\n" +
            "     END;';\n" +
            "     \n" +
            "   EXECUTE IMMEDIATE V_SQL;\n" +
            "\n" +
            "END;";

    public static String getSystemUser() {
        return SYSTEM_USER;
    }

    public static String getServerUser() {
        return SERVER_USER;
    }

    public static String getCurrentUser() {
        return CURRENT_USER;
    }

    public static String getPrefixRegisterMessage() {
        return PREFIX_REGISTER_MESSAGE;
    }

    public static String getPrefixAuthMessage() {
        return PREFIX_AUTH_MESSAGE;
    }

    public static String getPrefixPrivateMessage() {
        return PREFIX_PRIVATE_MESSAGE;
    }

    public static String getPrefixChangeNicknameMessage() {
        return PREFIX_CHANGE_NICKNAME_MESSAGE;
    }

    public static String getAuthFailedMessage() {
        return AUTH_FAILED_MESSAGE;
    }

    public static String getLoginBusyMessage() {
        return LOGIN_BUSY_MESSAGE;
    }

    public static String getNicknameBusyMessage() {
        return NICKNAME_BUSY_MESSAGE;
    }

    public static String getIsRegistrationMessage() {
        return IS_REGISTRATION_MESSAGE;
    }

    static String getSqlCreateScheme() {
        return SQL_CREATE_SCHEME;
    }
}
