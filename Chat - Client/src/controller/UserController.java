/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Request;
import entity.User;
import flag.ActionFlags;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author DUC
 */
public class UserController{

    private final ObjectOutputStream objectOutputStream;
    public UserController(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }
    
    private void send(Request request){
        try {
           objectOutputStream.writeObject(request);
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }
    
    public void login(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Request request = new Request(ActionFlags.LOGIN, user);
        send(request);
    }

    public void register(User user){
        Request request = new Request(ActionFlags.REGISTER, user);
        send(request);
    }
    
    public void logout(User user) {
        Request request = new Request(ActionFlags.LOGOUT, user);
        send(request);
    }
//    public void sendMessage(String mess) {
//        mess = mess.replaceAll("\\n", "<br>");
//        String line = ActionFlags.SEND_MESSAGE + ";" + mess;
//        send(line);
//    }
//    
//    
    public void getListRoom(User user) {
        Request request = new Request(ActionFlags.GET_LIST_ROOM, user);
        send(request);
    }
//
//    public void createRoom(String roomName) {
//        String line = ActionFlags.CREATE_ROOM + ";" + roomName;
//        send(line);
//    }
//
//    public void joinRoom(String maPhong) {
//        String line = ActionFlags.JOIN_ROOM + ";" + maPhong;
//        send(line);
//    }
//
//    public void leaveRoom() {
//        String line = ActionFlags.LEAVE_ROOM + ";null";
//        send(line);
//    }
}
