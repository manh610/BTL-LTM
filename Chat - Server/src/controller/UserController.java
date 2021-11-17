/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.UserDAO;
import entity.User;
import flag.ActionFlags;
import flag.ResultFlags;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DUC
 */
public class UserController extends Observable {

    private BufferedReader bufferReader;
    private DataOutputStream dataOutputStream;
    private static String DB_URL = "jdbc:mysql://localhost:3306/btl_ltm";
    private static String USER_NAME = "root";
    private static String PASSWORD = "123456";
    private UserDAO userDAO;

    public UserController(BufferedReader bufferReader, DataOutputStream dataOutputStream, Observer obs) throws SQLException {
        this.addObserver(obs);
        this.bufferReader = bufferReader;
        this.dataOutputStream = dataOutputStream;
        userDAO = new UserDAO(DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD));
    }
    
    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
    
    public void send(String actionType, String resultCode, String content) {
        try {
            dataOutputStream.writeUTF(actionType + ";" + resultCode + ";" + content);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void checkLogin(String username, String password, String actionType) {
        System.out.println(username + password);
        User user = userDAO.checkLogin(username, password);
        if (user != null) {
            send(actionType, ResultFlags.OK, "OK");
            userDAO.update(user); // update logged
            notifyObservers(" vừa đăng nhập thành công");
        } else {
            send(actionType, ResultFlags.ERROR, "Sai thông tin đăng nhập");
        }
    }


    public void checkRegister(String username, String password, String displayName, String actionType) {
        if(userDAO.checkExistsUser(username) == 1){
            User user = new User(username, password, displayName, 0);
            send(actionType, ResultFlags.OK, "OK");
            userDAO.insert(user);
        }else{
            send(actionType, ResultFlags.ERROR, "Tài khoản đã tồn tại");
        }
    }

}
