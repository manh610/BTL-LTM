/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import core.Client;
import core.Result;
import entity.Message;
import entity.User;
import flag.ResultFlags;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author DUC
 */
public class RoomController extends Observable{

    private final BufferedWriter bufferWriter;

    public RoomController(BufferedWriter bufferWriter, Observer obs) {
        this.addObserver(obs);
        this.bufferWriter = bufferWriter;
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
    
    public List<User> getListUserRoom(){
        return null;
    }
    
    public List<Message> getAllMessage(){
        return null;
    }
}
