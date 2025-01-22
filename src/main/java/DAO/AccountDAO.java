package DAO;

import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import Model.Account;


public class AccountDAO {

  public Account getAccountByUsername(String username) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM account WHERE username = ?");
      ps.setString(1, username);
      ResultSet result = ps.executeQuery();
      while (result.next()) {
        Account accountAdded = new Account();
        accountAdded.setAccount_id(result.getInt(1));
        accountAdded.setUsername(result.getString(2));
        accountAdded.setPassword(result.getString(3));
        return accountAdded;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Account getAccountById(int account_id) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM account WHERE account_id = ?");
      ps.setInt(1, account_id);
      ResultSet result = ps.executeQuery();
      while (result.next()) {
        Account accountAdded = new Account();
        accountAdded.setAccount_id(result.getInt(1));
        accountAdded.setUsername(result.getString(2));
        accountAdded.setPassword(result.getString(3));
        return accountAdded;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      
    }
    return null;
  }

  public Account addAccount(Account account) {
    try {
      Connection connection = ConnectionUtil.getConnection();
      PreparedStatement ps = connection.prepareStatement("INSERT INTO account (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, account.getUsername());
      ps.setString(2, account.getPassword());
      ps.executeUpdate();

      ResultSet result = ps.getGeneratedKeys();
      if (result.next()) {
        Account accountToAdd = new Account();
        accountToAdd.setAccount_id(result.getInt(1));
        accountToAdd.setUsername(account.getUsername());
        accountToAdd.setPassword(account.getPassword());
        return accountToAdd;
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return null;
  }
}
