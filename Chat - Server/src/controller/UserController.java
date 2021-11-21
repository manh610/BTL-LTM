/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.RoomDAO;
import dao.UserDAO;
import entity.Response;
import entity.Room;
import entity.User;
import flag.ActionFlags;
import flag.ResultFlags;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author DUC
 */
public class UserController {

    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    private static String DB_URL = "jdbc:mysql://localhost:3306/btl_ltm";
    private static String USER_NAME = "root";
    private static String PASSWORD = "123456";
    public UserDAO userDAO;
    public RoomDAO roomDAO;
    public Socket socket;

    public UserController(Socket socket) throws SQLException, IOException {
        this.socket = socket;
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.userDAO = new UserDAO(DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD));
        this.roomDAO = new RoomDAO(DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD));
//        System.out.println(objectInputStream);
//        System.out.println(objectOutputStream);
    }

    public void send(Response response) {
        try {
            objectOutputStream.writeObject(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void logOut(User user, String actionType) {
        user.setActive("offline");
        userDAO.update(user);
    }

    public boolean checkLogin(User objUser, String actionType) {

        User user = userDAO.checkLogin(objUser.getUsername(), objUser.getPassword());
        if (user != null) {
            user.setActive("online");
            userDAO.update(user);
            Response response = new Response(actionType, ResultFlags.OK, "OK", user);
            send(response);
            return true;
        } else {
            Response response = new Response(actionType, ResultFlags.ERROR, "Sai thông tin đăng nhập", null);
            send(response);
            return false;
        }
    }

    public void getListRoom(User user) {
        List<Room> listRoom = roomDAO.getListRoomByUserId(user);
        if (listRoom != null) {
            Response response = new Response(ActionFlags.GET_LIST_ROOM, ResultFlags.OK, "OK", listRoom);
            send(response);
        }
    }

    public void getAllUser() {
        List<User> listUser = userDAO.selectAll();
        if (listUser != null) {
            Response response = new Response(ActionFlags.GET_ALL_USER, ResultFlags.OK, "", listUser);
            send(response);
        }
    }

    public void checkRegister(User user, String actionType) {
        if (userDAO.checkExistsUser(user.getUsername()) == 1) {
            Response response = new Response(actionType, ResultFlags.OK, "OK", null);
            userDAO.insert(user);
            send(response);
        } else {
            Response response = new Response(actionType, ResultFlags.ERROR, "Tài khoản đã tồn tại", null);
            send(response);
        }
    }

}
