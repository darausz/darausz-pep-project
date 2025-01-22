package Controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
  ObjectMapper om = new ObjectMapper();
  AccountDAO accountDAO = new AccountDAO();
  MessageDAO messageDAO = new MessageDAO();

  /**
   * In order for the test cases to work, you will need to write the endpoints in
   * the startAPI() method, as the test
   * suite must receive a Javalin object from this method.
   * 
   * @return a Javalin app object which defines the behavior of the Javalin
   *         controller.
   */
  public Javalin startAPI() {
    Javalin app = Javalin.create();
    app.get("example-endpoint", this::exampleHandler);
    app.post("register", this::register);
    app.post("login", this::login);
    app.get("messages", this::getAllMessages);
    app.post("messages", this::createMessage);
    app.get("messages/{message_id}", this::getMessageById);
    app.patch("messages/{message_id}", this::updateMessage);
    app.delete("messages/{message_id}", this::deleteMessage);
    app.get("accounts/{account_id}/messages", this::getAllMessagesByUser);
    return app;
  }

  /**
   * This is an example handler for an example endpoint.
   * 
   * @param context The Javalin Context object manages information about both the
   *                HTTP request and response.
   */
  private void exampleHandler(Context context) {
    context.json("sample text");
  }

  private void register(Context context) throws JsonMappingException, JsonProcessingException {
    Account account = om.readValue(context.body(), Account.class);
    if (account.getUsername() == null || account.getUsername() == "" || account.getPassword().length() < 4) {
      context.status(400);
      return;
    }
    if (accountDAO.getAccountByUsername(account.getUsername()) == null) {
      Account accountToAdd = accountDAO.addAccount(account);
      context.status(200).json(accountToAdd);
      return;
    }
    context.status(400);
  }

  private void login(Context context) throws JsonMappingException, JsonProcessingException {
    Account account = om.readValue(context.body(), Account.class);
    Account loginAttempt = accountDAO.getAccountByUsername(account.getUsername());
    if (loginAttempt != null && account.getPassword().equals(loginAttempt.getPassword())) {
      context.json(loginAttempt);
    } else {
      context.status(401);
    }
  }

  private void getAllMessages(Context context) throws JsonMappingException, JsonProcessingException {
    context.json(messageDAO.getAllMessages());
  }

  private void getAllMessagesByUser(Context context) throws JsonMappingException, JsonProcessingException {
    int account_id = Integer.parseInt(context.pathParam("account_id"));
    context.json(messageDAO.getAllMessagesByUserId(account_id));
  }

  private void getMessageById(Context context) throws JsonMappingException, JsonProcessingException {
    int message_id = Integer.parseInt(context.pathParam("message_id"));
    Message message = messageDAO.getMessageById(message_id);
    if (message == null) {
      context.status(200);
    } else {
      context.json(message).status(200);
    }
  }

  private void createMessage(Context context) throws JsonMappingException, JsonProcessingException {
    Message message = om.readValue(context.body(), Message.class);
    Account account = accountDAO.getAccountById(message.getPosted_by());
    if (account == null) {
      context.status(400);
      return;
    }
    String message_text = message.getMessage_text();
    if (message_text == null || message_text == "" || message_text.length() > 255) {
      context.status(400);
      return;
    }
    Message messageCreated = messageDAO.createMessage(message);
    if (messageCreated != null) {
      context.json(messageCreated);
    } 
    else {
      context.status(400);
    }
  }

  private void updateMessage(Context context) throws JsonMappingException, JsonProcessingException {
    Message message = om.readValue(context.body(), Message.class);
    String message_text = message.getMessage_text();
    if (message_text == null || message_text == "" || message_text.length() > 255) {
      context.status(400);
      return;
    }
    int message_id = Integer.parseInt(context.pathParam("message_id"));
    Message messageCurrent = messageDAO.getMessageById(message_id);
    if (messageCurrent == null) {
      context.status(400);
      return;
    }
    message.setMessage_id(message_id);
    Message updatedMessage = messageDAO.updateMessage(message);
    if (updatedMessage != null) {
      context.json(messageDAO.getMessageById(message_id));
    }
    else {
      context.status(400);
    }
  }

  private void deleteMessage(Context context) throws JsonMappingException, JsonProcessingException {
    int message_id = Integer.parseInt(context.pathParam("message_id"));
    Message message = messageDAO.getMessageById(message_id);
    if (message == null) {
      context.status(200);
      return;
    }
    messageDAO.deleteMessage(message_id);
    context.json(message);
  }
}