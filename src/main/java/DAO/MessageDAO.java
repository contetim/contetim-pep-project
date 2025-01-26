package DAO;

import Model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private final Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public Message createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, message.getPosted_by()); // Foreign key reference to account_id
        statement.setString(2, message.getMessage_text());
        statement.setLong(3, message.getTime_posted_epoch());
        int rows = statement.executeUpdate();

        if (rows > 0) {
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                message.setMessage_id(keys.getInt(1)); // Auto-incremented ID
                return message;
            }
        }
        return null; // Return null if insertion fails
    }

    public List<Message> getAllMessages() throws SQLException {
        String sql = "SELECT * FROM message";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        List<Message> messages = new ArrayList<>();

        while (resultSet.next()) {
            messages.add(new Message(
                resultSet.getInt("message_id"),
                resultSet.getInt("posted_by"), // Reference to account_id
                resultSet.getString("message_text"),
                resultSet.getLong("time_posted_epoch")
            ));
        }
        return messages;
    }

    public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, messageId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Message(
                resultSet.getInt("message_id"),
                resultSet.getInt("posted_by"),
                resultSet.getString("message_text"),
                resultSet.getLong("time_posted_epoch")
            );
        }
        return null;
    }

    public boolean deleteMessageById(int messageId) throws SQLException {
        String sql = "DELETE FROM message WHERE message_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, messageId);
        return statement.executeUpdate() > 0; // Return true if deletion succeeds
    }

    public boolean updateMessageText(int messageId, String newMessageText) throws SQLException {
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, newMessageText);
        statement.setInt(2, messageId);
        return statement.executeUpdate() > 0; // Return true if update succeeds
    }

    public List<Message> getMessagesByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userId); // Foreign key to account_id
        ResultSet resultSet = statement.executeQuery();
        List<Message> messages = new ArrayList<>();

        while (resultSet.next()) {
            messages.add(new Message(
                resultSet.getInt("message_id"),
                resultSet.getInt("posted_by"),
                resultSet.getString("message_text"),
                resultSet.getLong("time_posted_epoch")
            ));
        }
        return messages;
    }
}