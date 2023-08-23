package com.minefh.cardcharge.databases;

import com.minefh.cardcharge.objects.Card;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.utils.PluginUtils;
import lombok.Getter;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQL {

    private static MySQL __instance;
    private final String url;
    private final String username;
    private final String password;
    @Getter
    private Connection connection;

    public MySQL(String hostname, String database, String username, String password) {
        __instance = this;

        this.url = "jdbc:mysql://" + hostname + "/" + database +
                "?autoReconnect=true&maxReconnects=30&reconnectInterval=10000";
        this.username = username;
        this.password = password;
    }

    public static MySQL getInstance() {
        return __instance;
    }

    public void connect() {
        try {
            Class.forName("com.minefh.cardcharge.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, this.username, this.password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createDonateSuccessTable() {
        PreparedStatement statement = null;
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS success_donate (id INT AUTO_INCREMENT NOT NULL, transaction_id VARCHAR(255) NOT NULL, player_name VARCHAR(65) NOT NULL, amount INT NOT NULL, serial VARCHAR(255) NOT NULL, pin VARCHAR(255) NOT NULL, telco VARCHAR(25) NOT NULL, update_time DATETIME NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB";
        try {
            statement = getConnection().prepareStatement(CREATE_TABLE);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PluginUtils.cleanMySQL(statement, null);
        }
    }

    public void insertDonateSuccess(Transaction transaction) {
        String INSERT = "INSERT INTO success_donate (transaction_id, player_name, amount, serial, pin, telco, update_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(INSERT);

            //TRANSACTION INFORMATION
            statement.setString(1, transaction.getId());
            statement.setString(2, transaction.getPlayerName());

            //CARD INFORMATION
            Card card = transaction.getCard();
            statement.setInt(3, card.getAmount().getAsInt());
            statement.setString(4, card.getSerial());
            statement.setString(5, card.getPin());
            statement.setString(6, card.getType());

            //TIMESTAMP
            statement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error when inserting " + transaction.toString());
        } finally {
            PluginUtils.cleanMySQL(statement, null);
        }
    }

    public void debugDonate(String playerName, int amount) {
        String INSERT = "INSERT INTO success_donate (transaction_id, player_name, amount, serial, pin, telco, update_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(INSERT);

            //TRANSACTION INFORMATION
            statement.setString(1, "123124123");
            statement.setString(2, playerName);

            //CARD INFORMATION
            statement.setInt(3, amount);
            statement.setString(4, "12312412312312");
            statement.setString(5, "12312312312313");
            statement.setString(6, "DebugCard");

            //TIMESTAMP
            statement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error when logging player " + playerName);
        } finally {
            PluginUtils.cleanMySQL(statement, null);
        }
    }

    public Map<String, Integer> getTopTen() {
        HashMap<String, Integer> topTen = new HashMap<>();
        String QUERY_STRING = "SELECT player_name, SUM(amount) AS total_amount FROM success_donate GROUP BY player_name ORDER BY total_amount DESC LIMIT 10";
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = getConnection().prepareStatement(QUERY_STRING);
            rs = statement.executeQuery();
            while (rs.next()) {
                String player_name = rs.getString("player_name");
                int total_amount = rs.getInt("total_amount");
                topTen.put(player_name, total_amount);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error when trying to display top ten donator!");
        } finally {
            PluginUtils.cleanMySQL(statement, rs);
        }
        return topTen;
    }

    public void purgeData(String playerName) {
        PreparedStatement statement = null;
        String QUERY_STRING = "DELETE FROM success_donate WHERE player_name = ?";
        try {
            statement = connection.prepareStatement(QUERY_STRING);
            statement.setString(1, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error when trying to purge data of " + playerName);
        } finally {
            PluginUtils.cleanMySQL(statement, null);
        }
    }

}
