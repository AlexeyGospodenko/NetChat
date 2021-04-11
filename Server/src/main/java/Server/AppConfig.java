package Server;

import Server.Services.DatabaseServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.sql.SQLException;

@Configuration
@ComponentScan("Server")
public class AppConfig {

//    @Bean
//    public DatabaseServiceImpl databaseService() throws SQLException, ClassNotFoundException {
//        return new DatabaseServiceImpl();
//    }
//
//    @Bean
//    public SerialHandler serialHandler() throws IOException, SQLException, ClassNotFoundException {
//        return new SerialHandler(databaseService());
//    }
}
