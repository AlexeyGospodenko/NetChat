package Server.Services;

import java.io.Closeable;
import java.sql.*;

public class DatabaseServiceImpl implements DatabaseService, Closeable {
    private static DatabaseServiceImpl instance;

    private boolean isDbConnect = false;
    private Connection connection;
    private Statement statement;

    private DatabaseServiceImpl() {
    }

    public String dbConnect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "CHAT_AUTH", "CHAT_AUTH");
            statement = connection.createStatement();
            isDbConnect = true;
            return "DB connection successfully created";
        } catch (ClassNotFoundException | SQLException e) {
            return e.getMessage();
        }
    }

    public static DatabaseServiceImpl getInstance() {
        if (instance == null) {
            instance = new DatabaseServiceImpl();
        }
        return instance;
    }

    public boolean isDbConnect() {
        return isDbConnect;
    }

    public void addUser(String login, String pass, String nickName) {
        String sql = String.format("INSERT INTO USER_DAO (LOGIN,\n" +
                "                      PASSWORD,\n" +
                "                      NICKNAME)\n" +
                "VALUES ('%s', '%s', '%s')", login, pass, nickName);
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoginExists(String login) throws SQLException {
        String sql = String.format("SELECT 1 FROM USER_DAO WHERE LOGIN = '%s'", login);
        ResultSet rs = statement.executeQuery(sql);
        return rs.next();
    }

    public boolean isNicknameExists(String nickName) throws SQLException {
        String sql = String.format("SELECT 1 FROM USER_DAO WHERE NICKNAME = '%s'", nickName);
        ResultSet rs = statement.executeQuery(sql);
        return rs.next();
    }

    public void changeNickname(String oldNickname, String newNickname) {
        String sql = String.format("UPDATE USER_DAO SET NICKNAME = '%s' WHERE NICKNAME = '%s'", newNickname, oldNickname);
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String auth(String login, String pass) throws SQLException {
        int userId = 0;
        String nickName = null;

        String sql = String.format("SELECT USER_ID, NICKNAME FROM USER_DAO WHERE LOGIN = '%s' AND PASSWORD = '%s'", login, pass);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            userId = rs.getInt("USER_ID");
            nickName = rs.getString("NICKNAME");
        }
        return nickName;
    }

    @Override
    public void close() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}