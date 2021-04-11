package Client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("Client")
public class AppConfig {

//    @Bean(name = "historyServiceImpl")
//    public HistoryServiceImpl historyService() {
//        return new HistoryServiceImpl();
//    }
//
//    @Bean(name = "AuthController")
//    public AuthController authController(HistoryServiceImpl historyService) {
//        AuthController authController = new AuthController();
//        authController.setHistoryService(historyService);
//        return authController;
//    }

//    @Bean(name = "ChatController")
//    public ChatController chatController(HistoryServiceImpl historyService) {
//        ChatController chatController = new ChatController();
//        chatController.setHistoryService(historyService);
//        return chatController;
//    }

}
