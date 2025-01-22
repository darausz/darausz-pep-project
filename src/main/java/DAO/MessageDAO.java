package DAO;

import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Message;

public class MessageDAO {

  public List<Message> getAllMessages() {
    try {
      Connection connection = ConnectionUtil.getConnection();
      Statement statement = connection.createStatement();
      ResultSet results = statement.executeQuery("SELECT * FROM Message");

      List<Message> messages = new ArrayList<>();
      while (results.next()) {
        Message messageToAdd = new Message();
        messageToAdd.setMessage_id(results.getInt(1));
        messageToAdd.setPosted_by(results.getInt(2));
        messageToAdd.setMessage_text(results.getString(3));
        messageToAdd.setTime_posted_epoch(results.getLong(4));
        messages.add(messageToAdd);
      }
      return messages;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<Message> getAllMessagesByUserId(int account_id) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM Message WHERE posted_by = ?");
      ps.setInt(1, account_id);
      ResultSet results = ps.executeQuery();

      List<Message> messages = new ArrayList<>();
      while (results.next()) {
        Message messageToAdd = new Message();
        messageToAdd.setMessage_id(results.getInt(1));
        messageToAdd.setPosted_by(results.getInt(2));
        messageToAdd.setMessage_text(results.getString(3));
        messageToAdd.setTime_posted_epoch(results.getLong(4));
        messages.add(messageToAdd);
      }
      return messages;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Message getMessageById(int message_id) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM Message WHERE message_id = ?");
      ps.setInt(1, message_id);
      ResultSet result = ps.executeQuery();
      if (result.next()) {
        Message messageQueried = new Message();
        messageQueried.setMessage_id(result.getInt(1));
        messageQueried.setPosted_by(result.getInt(2));
        messageQueried.setMessage_text(result.getString(3));
        messageQueried.setTime_posted_epoch(result.getLong(4));
        return messageQueried;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Message createMessage(Message message) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, message.getPosted_by());
      ps.setString(2, message.getMessage_text());
      ps.setLong(3, message.getTime_posted_epoch());
      ps.executeUpdate();

      ResultSet result = ps.getGeneratedKeys();
      while (result.next()) {
        Message messageCreated = new Message();
        messageCreated.setMessage_id(result.getInt(1));
        messageCreated.setPosted_by(message.getPosted_by());
        messageCreated.setMessage_text(message.getMessage_text());
        messageCreated.setTime_posted_epoch(message.getTime_posted_epoch());
        return messageCreated;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Message updateMessage(Message message) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("UPDATE Message SET message_text =? WHERE message_id = ?");
      ps.setString(1, message.getMessage_text());
      ps.setInt(2, message.getMessage_id());
      int rowsUpdated = ps.executeUpdate();
      if (rowsUpdated > 0) {
        return message;
      }
    } 
    catch (Exception e) {
        e.printStackTrace();
    }
    return null;
  }

  public Boolean deleteMessage(int message_id) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("DELETE FROM Message WHERE message_id = ?");
      ps.setInt(1, message_id);
      int rowsAffected = ps.executeUpdate();
      if (rowsAffected > 0) {
        return true;
      }
    } 
    catch (Exception e) {
        e.printStackTrace();
    }
    return false;
  }
}
