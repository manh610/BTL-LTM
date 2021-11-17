/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import core.Client;
import core.Result;
import entity.User;
import flag.ActionFlags;
import flag.ResultFlags;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author DUC
 */
public class UserController extends Observable{

    private final BufferedWriter bufferWriter;
    private Observer obs;
    public UserController(BufferedWriter bufferWriter, Observer obs) {
        this.bufferWriter = bufferWriter;
        this.obs = obs;
    }
    
    
    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
    
    private void send(String content){
        try {
            bufferWriter.write(content + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result(ex.toString(), ResultFlags.ERROR, "Không thể kết nối tới server");
            notifyObservers(result);
        }
    }
    
    public void login(String nickName, String password){
        String line = ActionFlags.LOGIN + ";" + nickName + ";" + password;
        send(line);
    }

    public void register(User user){
        String line = ActionFlags.REGISTER + ";" + user.getUsername() + ";" + user.getPassword() + ";" + user.getDisplayName();
        send(line);
    }
    
    public void logout() {
        String line = ActionFlags.LOGOUT + ";null";
        send(line);
    }
    public void sendMessage(String mess) {
        mess = mess.replaceAll("\\n", "<br>");
        String line = ActionFlags.SEND_MESSAGE + ";" + mess;
        send(line);
    }
    
    
    public void getListRoom() {
        String line = ActionFlags.GET_LIST_ROOM + ";";
        send(line);
    }

    public void createRoom(String roomName) {
        String line = ActionFlags.CREATE_ROOM + ";" + roomName;
        send(line);
    }

    public void joinRoom(String maPhong) {
        String line = ActionFlags.JOIN_ROOM + ";" + maPhong;
        send(line);
    }

    public void leaveRoom() {
        String line = ActionFlags.LEAVE_ROOM + ";null";
        send(line);
    }
}
