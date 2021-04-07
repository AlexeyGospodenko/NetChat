package Client;

import javafx.application.Application;
import javafx.stage.Stage;

public class ChatApplication extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
       new CreateWindow("auth.fxml", "NetChat - Authorization", false);
    }
}