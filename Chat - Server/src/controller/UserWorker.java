/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.MessageDAO;
import dao.RoomDAO;
import dao.UserDAO;
import dao.impl.MessageDAOImpl;
import dao.impl.RoomDAOImpl;
import dao.impl.UserDAOImpl;
import entity.Message;
import entity.Response;
import entity.Room;
import entity.User;
import entity.UserRoom;
import flag.ActionFlags;
import flag.ResultFlags;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DUC
 */
public class UserWorker {

    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    private static String DB_URL = "jdbc:mysql://localhost:3306/btl_ltm";
    private static String USER_NAME = "root";
    private static String PASSWORD = "123456";
    public UserDAO userDAO;
    public RoomDAO roomDAO;
    public MessageDAO messageDAO;
    public Socket socket;
    public User userc;
    
    public UserWorker(Socket socket) throws SQLException, IOException {
        this.socket = socket;
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.userDAO = new UserDAOImpl();
        this.roomDAO = new RoomDAOImpl();
        this.messageDAO = new MessageDAOImpl();
        this.userc = new User();
    }

    public void send(Response response) {
        try {
            objectOutputStream.writeObject(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void logout() {
        userc.setActive("offline");
        userDAO.update(userc);
    }

    public void checkLogin(String username, String password, String actionType) {
        User user = userDAO.checkLogin(username, password);
        if (user != null) {
            user.setActive("online");
            userDAO.update(user);
            this.userc = user;
            Response response = new Response(actionType, ResultFlags.OK, "OK", user);
            send(response);
        } else {
            Response response = new Response(actionType, ResultFlags.ERROR, "Sai thông tin đăng nhập", null);
            send(response);
        }
    }

    public void getListRoom() {
        List<Room> listRoom = roomDAO.getListRoomByUserId(userc);
        if (listRoom != null) {
            Response response = new Response(ActionFlags.GET_LIST_ROOM, ResultFlags.OK, "OK", listRoom);
            send(response);
        }
    }
    
    public Room getRoom(Room room){
        return roomDAO.getRoomById(room.getId());
    }
    
    public void getAllUser() {
        List<User> listUser = userDAO.selectAll();
        if (listUser != null) {
            Response response = new Response(ActionFlags.GET_ALL_USER, ResultFlags.OK, "", listUser);
            send(response);
        }
    }
    
    public void createRoomByUser(Room room, String actionType){
        List<User> listUser = new ArrayList<>();
        listUser.add(userc);
        if(roomDAO.createRoomByUsers(room, listUser) != null){
            getListRoom();
        }
    }
    
    public void checkRegister(User user, String actionType) {
        if (!userDAO.checkExistsUser(user.getUsername())) {
            userDAO.insert(user);
            Response response = new Response(actionType, ResultFlags.OK, "OK", null);
            send(response);
        } else {
            Response response = new Response(actionType, ResultFlags.ERROR, "Tài khoản đã tồn tại", null);
            send(response);
        }
    }
    
    public void openRoomChat(Room room, String actionType){
        Response response = new Response(actionType, ResultFlags.OK, "OK", room);
        send(response);
    }
    
    public void sendMessage(Message message, String actionType){
        Response response = new Response(actionType, ResultFlags.OK, "OK", message);
        send(response);
    }
    
    public Room checkExistsPrivateRoom(User user){
        List<Room> listRoom = roomDAO.getListRoomByUserId(userc);
        if (listRoom != null) {
            for(Room room: listRoom){
                List<UserRoom> lur = room.getListUserRoom();
                if(lur.size() == 2 && (lur.get(0).getUser().getId() == user.getId() || lur.get(1).getUser().getId() == user.getId())){
                    // da ton tai -> send
                    System.out.println("Da ton tai");
                    return room;
                }
            }
            // chua ton tai -> create
            List<User> listUser = new ArrayList<>();
            listUser.add(userc);
            listUser.add(user);
            Room room = new Room();
            room.setDescription("Room chat cua: " + user.getDisplayName() + " va" + userc.getDisplayName());
            room = roomDAO.createRoomByUsers(room, listUser);
            return room;
        }
        return null;
    }
    
    public Message createMessage(Message message, int roomId){
        System.out.println(message.toString() + " " + roomId);
        return messageDAO.insertMessage(message, userc.getId(), roomId);
    }
    
}
