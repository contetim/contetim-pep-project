package Service;

import Model.Message;
import Util.ConnectionUtil;
import DAO.MessageDAO;
import java.sql.*;
import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    private boolean userExists(int userId) throws SQLException {
        String sql = "SELECT 1 FROM account WHERE account_id = ?";
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    
    public Message createMessage(Message message) throws SQLException {
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            return null;
        }
        // Check if the user exists before inserting the message
        if (!userExists(message.getPosted_by())) {
            return null;
        }

        if (message.getPosted_by() <= 0) {
            return null; // Return null if invalid user ID
        }

        try {
            System.out.println("Attempting to create message: " + message);
            Message createdMessage = messageDAO.createMessage(message);
            System.out.println("Message created: " + createdMessage);
            return createdMessage;
        } catch (SQLException e) {
            System.err.println("Error in DAO: " + e.getMessage());
            throw e;
        }
    }

    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int messageId) throws SQLException {
        return messageDAO.getMessageById(messageId);
    }

    public boolean deleteMessage(int messageId) throws SQLException {
        return messageDAO.deleteMessageById(messageId);
    }

    public boolean updateMessage(int messageId, String newText) throws SQLException {
        return newText != null && !newText.isBlank() && newText.length() <= 255 && messageDAO.updateMessageText(messageId, newText);
    }

    public List<Message> getMessagesByUserId(int userId) throws SQLException {
        return messageDAO.getMessagesByUserId(userId);
    }
}