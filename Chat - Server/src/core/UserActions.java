/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import flag.ActionFlags;
import flag.ResultFlags;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author APC-LTN
 */
public class UserActions {
    Socket socket;
    BufferedReader bufferReader;
    DataOutputStream dataOutputStream;
    public String nickName;
    public RoomManagement room;
    public Date timeConnect; 
    public boolean logined = false;   
    public UserActions(Socket socket) throws IOException
    {
        socket = socket;
        bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
        dataOutputStream = new DataOutputStream(socket.getOutputStream());  
    }
    
    public String read() throws IOException
    {
        if(bufferReader.ready())
        {
            return bufferReader.readLine();
        }
        return null;
    }
    
    public boolean ready() throws IOException
    {
        return bufferReader.ready();
    }

    public Boolean send(String actionType, String resultCode, String content)
    {
        try 
        {
            dataOutputStream.writeUTF(actionType + ";" + resultCode + ";" + content);
            System.out.println(resultCode);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(UserActions.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public Boolean isOnline()
    {
        return send(ActionFlags.CHECK_ONLINE, ResultFlags.OK, "");
    }
}
