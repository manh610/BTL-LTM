/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.Observable;

/**
 *
 * @author DUC
 */
public class RoomController extends Observable{
    private BufferedReader bufferReader;
    private DataOutputStream dataOutputStream;
    private static String DB_URL = "jdbc:mysql://localhost:3306/btl_ltm";
    private static String USER_NAME = "root";
    private static String PASSWORD = "123456";

    public RoomController(BufferedReader bufferReader, DataOutputStream dataOutputStream) {
        this.bufferReader = bufferReader;
        this.dataOutputStream = dataOutputStream;
    }
    
    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
    
}
