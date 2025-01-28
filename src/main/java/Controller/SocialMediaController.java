package Controller;

import java.sql.Connection;
import Model.*;
import Service.*;
import Util.ConnectionUtil;
import DAO.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;
    public SocialMediaController() {
        Connection connection = ConnectionUtil.getConnection();
        AccountDAO accountDAO = new AccountDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        this.accountService = new AccountService(accountDAO);
        this.messageService = new MessageService(messageDAO);
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        //all endpoint handlers (specific handler defined in each method call parameter)
        app.get("/example-endpoint", this::exampleHandler);
        
        // Register account
        app.post("/register", ctx -> {
            Account account = ctx.bodyAsClass(Account.class);
            Account newAccount = accountService.createAccount(account);
            if (newAccount != null) {
                ctx.json(newAccount);
            } else {
                ctx.status(400);
            }
        });

        // Login
        app.post("/login", ctx -> {
            Account loginRequest = ctx.bodyAsClass(Account.class);
            Account account = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (account != null) {
                ctx.json(account);
            } else {
                ctx.status(401);
            }
        });

        // Create message
        app.post("/messages", ctx -> {
            try {
                Message message = ctx.bodyAsClass(Message.class);
                Message createdMessage = messageService.createMessage(message);
        
                if (createdMessage == null) {
                    ctx.status(400); // Bad request if validation or creation fails;
                } else {
                    ctx.status(200); // Success
                    ctx.json(createdMessage); // Return created message
                }
            } catch (Exception e) {
                e.printStackTrace(); // Print stack trace for debugging
                ctx.status(500).result("Internal server error");
            }
        });

        // Get all messages
        app.get("/messages", ctx -> {
            ctx.json(messageService.getAllMessages());
        });

        // Get message by ID
        app.get("/messages/{message_id}", ctx -> {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMessageById(messageId);
            if (message != null) {
                ctx.json(message);
            } else {
                ctx.status(404).json("");
            }
        });

        // Delete message
        app.delete("/messages/{message_id}", ctx -> {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message deletedMessage = messageService.getMessageById(messageId);
            if (deletedMessage != null && messageService.deleteMessage(messageId)) {
                ctx.json(deletedMessage);
            } else {
                ctx.json("");
            }
        });

        // Update message
        app.patch("/messages/{message_id}", ctx -> {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message existingMessage = messageService.getMessageById(messageId);
            if (existingMessage != null) {
                String newText = ctx.bodyAsClass(Message.class).getMessage_text();
                if (messageService.updateMessage(messageId, newText)) {
                    existingMessage.setMessage_text(newText);
                    ctx.json(existingMessage);
                } else {
                    ctx.status(400);
                }
            } else {
                ctx.status(400);
            }
        });

        // Get messages by user ID
        app.get("/accounts/{account_id}/messages", ctx -> {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            ctx.json(messageService.getMessagesByUserId(accountId));
        });
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        System.out.println("Request received!");
        context.json("sample text");
    }
}