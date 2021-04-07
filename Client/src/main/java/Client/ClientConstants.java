package Client;

import Server.ServerConstants;

public class ClientConstants extends ServerConstants {
    private final static String ACTION_FAIL = "-fx-border-radius: 5; -fx-border-color: red; -fx-background-insets: 0;";
    private final static String ACTION_SUCCESS = "-fx-border-radius: 5; -fx-border-color: lightgreen; -fx-background-insets: 0;";

    public static String getActionFail() {
        return ACTION_FAIL;
    }

    public static String getActionSuccess() {
        return ACTION_SUCCESS;
    }
}
