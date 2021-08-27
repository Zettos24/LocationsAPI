package de.zettos.locationsapi;

import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.*;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

@Getter
class MySQL {


    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private Connection connection;


    protected void setCredentials(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    protected final void createTable(String tableName, String columnNames) {
        String query = MessageFormat.format("CREATE TABLE IF NOT EXISTS {0}({1});", tableName, columnNames);
        this.update(query);
    }

    @SneakyThrows
    protected final void connect(Runnable runnable) {
        String url = MessageFormat.format("jdbc:mysql://{0}:{1}/{2}", this.host, String.valueOf(this.port).replace(".", ""), this.database);
        try {

            this.connection =  DriverManager.getConnection(url + "?autoReconnect=true", username, password);
            runnable.run();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    protected final void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    protected final void updateSync(String query) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected final void update(String query) {
        CompletableFuture.runAsync(() -> updateSync(query));
    }

    protected final ResultSet query(String query) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            return statement.executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
