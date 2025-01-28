package Service;

import Model.Message;
import DAO.MessageDAO;

import java.sql.SQLException;
import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public Message createMessage(Message message) throws SQLException {
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            return null;
        }
        return messageDAO.createMessage(message);
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