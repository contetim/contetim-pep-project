package Controller;

import java.sql.SQLException;

import Model.*;
import Service.*;
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
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        // System.out.println("Initializing Javalin...");
        Javalin app = Javalin.create();
        // System.out.println("Javalin started successfully!");

        //all endpoint handlers (specific handler defined in each method call parameter)
        app.get("/example-endpoint", this::exampleHandler);
        app.post("/register", this::createAccountHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserIdHandler);

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

    /**
     * createAccount endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException 
     */
    private void createAccountHandler(Context ctx) throws SQLException {
        System.out.println("createAccount request received!");
        Account account = ctx.bodyAsClass(Account.class);
        Account newAccount = accountService.createAccount(account);
        if (newAccount != null) {
            ctx.json(newAccount);
        } else {
            ctx.status(400);
        }
    }

    /**
     * Login endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException 
     */
    private void loginHandler(Context ctx) throws SQLException { 
        System.out.println("Login request received!");
        Account loginRequest = ctx.bodyAsClass(Account.class);
        Account account = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (account != null) {
            ctx.json(account);
        } else {
            ctx.status(401);
        }
    }

    /**
     * createMessage endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException 
     */
    private void createMessageHandler(Context ctx) throws SQLException {
        System.out.println("createMessage request received!");
        Message message = ctx.bodyAsClass(Message.class);
        Message newMessage = messageService.createMessage(message);
        if (newMessage != null) {
            ctx.json(newMessage);
        } else {
            ctx.status(400);
        }
    }

    /**
     * getAllMessages endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException
     */
    private void getAllMessagesHandler(Context ctx) throws SQLException {
        System.out.println("getAllMessages request received!");
        ctx.json(messageService.getAllMessages());
    }

    /**
     * getMessageById endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException 
     */
    private void getMessageByIdHandler(Context ctx) throws SQLException{
        System.out.println("getMessageById request received!");
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            ctx.json(message);
        } else {
            ctx.status(404).json("");
        }
    }

    /**
     * deleteMessage endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException 
     */
    private void deleteMessageHandler(Context ctx) throws SQLException{
        System.out.println("deleteMessage request received!");
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.getMessageById(messageId);
        if (deletedMessage != null && messageService.deleteMessage(messageId)) {
            ctx.json(deletedMessage);
        } else {
            ctx.json("");
        }
    }

    /**
     * updateMessage endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException
     */
    private void updateMessageHandler(Context ctx) throws SQLException{
        System.out.println("updateMessage request received!");
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
    }
    
    /**
     * getMessagesByUserId endpoint handler
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException
     */
    private void getMessagesByUserIdHandler(Context ctx) throws SQLException{
        System.out.println("getMessagesByUserId request received!");
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        ctx.json(messageService.getMessagesByUserId(accountId));
    }
}