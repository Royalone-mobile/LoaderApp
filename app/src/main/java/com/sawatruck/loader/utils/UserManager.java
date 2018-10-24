package com.sawatruck.loader.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Balance;
import com.sawatruck.loader.entities.User;

/**
 * Created by royalone on 2017-01-19.
 */

public class UserManager {
  Context _context;

  static UserManager instance;

  public UserManager(Context context) {
    this._context = context;
  }

  public static UserManager with(@NonNull Context context) {
    if (instance == null) {
      instance = new UserManager(context);
    }
    return instance;
  }
  /////  0 - guest 1 - loader

  public void setUserType(int userType) {
    AppSettings.with(_context).setUserType(userType);
  }

  public int getUserType() {
    return AppSettings.with(_context).getUserType();
  }


  public User getCurrentUser() {
    String jsonString = AppSettings.with(_context).getUser();
    User user = new User();
    if(jsonString!="")
      user = Serializer.getInstance().deserializeUser(jsonString);
    return user;
  }

  public Balance getBalance() {
    String jsonString = AppSettings.with(_context).getUser();
    Balance balance = new Balance();
    if(jsonString!="")
      balance = Serializer.getInstance().deserializeBalance(jsonString);
    return balance;
  }


  public void setCurrentUser(User user){
    String prefUser = Serializer.getInstance().serializeUser(user);
    AppSettings.with(_context).setUser(prefUser);
  }

  public void setBalance(Balance balance){
    String prefUser = Serializer.getInstance().serializeBalance(balance);
    AppSettings.with(_context).setBalance(prefUser);
  }
}
