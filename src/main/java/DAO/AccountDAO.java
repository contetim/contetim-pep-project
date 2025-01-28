package DAO;

import java.sql.*;
import Model.Account;

public class AccountDAO {
    private final Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public Account createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, account.getUsername());
        statement.setString(2, account.getPassword());
        int rows = statement.executeUpdate();

        if (rows > 0) {
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                account.setAccount_id(keys.getInt(1)); // Auto-incremented ID
                return account;
            }
        }
        return null; // Return null if insertion fails
    }

    public Account getAccountByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Account(
                resultSet.getInt("account_id"),
                resultSet.getString("username"),
                resultSet.getString("password")
            );
        }
        return null;
    }

    public boolean accountExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM account WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next(); // Return true if the username exists
    }

    public Account getAccountById(int accountId) {
        try (Connection conn = this.connection) {
            String sql = "SELECT * FROM Account WHERE account_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
    
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Account does not exist
    }    
}