package Server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerApplication extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent serverWindow = FXMLLoader.load(ServerApplication.class.getResource("server.fxml"));
        Stage stage = new Stage();
        stage.setTitle("NetChat - Server");
        stage.setScene(new Scene(serverWindow));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        ExecService.getInstance().getExecutorService().shutdownNow();
    }
}
