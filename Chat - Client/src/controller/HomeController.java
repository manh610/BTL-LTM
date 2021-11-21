/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Message;
import entity.Request;
import flag.ActionFlags;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 *
 * @author DUC
 */
public class HomeController{

    private final ObjectOutputStream objectOutputStream;
    public HomeController(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }
    
    
    private void send(Request request){
        try {
           objectOutputStream.writeObject(request);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void getAllUser(){
        Request request = new Request(ActionFlags.GET_ALL_USER, null);
        send(request);
        System.out.println("Gui thanh cong");
    }
    
//    public void getListRoom(){
//        
//    }
    public List<Message> getAllMessage(){
        return null;
    }
}
