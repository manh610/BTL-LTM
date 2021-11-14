/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.util.ArrayList;

/**
 *
 * @author APC-LTN
 */
public class RoomManagement {
    public String roomID;
    public String roomName;
    public int numberUsers;
    
    ArrayList<UserActions> listUser = new ArrayList<>();
    
    public void addUser(UserActions user)
    {
        listUser.add(user);
    }
    
    public void removeUser(UserActions user)
    {
        listUser.remove(user);
    }
    
    public int countUser()
    {
        return listUser.size();
    }
    
    public void sendToAllUser(String sender, String content)
    {
        int size = listUser.size();
        for (int i = 0; i < size; i++) 
        {
            UserActions user = listUser.get(i);
            if(user.send(ActionFlags.SEND_MESSAGE, ResultFlags.OK, sender + ";" + content)==false)
            {
                notifyJustLeaveRoom(user);
            }
        }
    }
    
    public void updateNumberUser()
    {
        int size = listUser.size();
        for (int i = 0; i < size; i++) 
        {
            UserActions user = listUser.get(i);
            if(user.send(ActionFlags.UPDATE_NUMBER_USER, ResultFlags.OK, size + "")==false)
            {
                notifyJustLeaveRoom(user);
            }
        }
    }
    
    public void notifyJustJoinRoom(UserActions userJoin)
    {
        int size = listUser.size();
        for (int i = 0; i < size; i++) 
        {
            UserActions user = listUser.get(i);
            if(user!=userJoin)
            {
                user.send(ActionFlags.NOTIFY_JUST_JOIN_ROOM, ResultFlags.OK, userJoin.nickName);
            }
        }
    }
    public void notifyJustLeaveRoom(UserActions userLeave)
    {
        int size = listUser.size();
        for (int i = 0; i < size; i++) 
        {
            UserActions user = listUser.get(i);
            if(user!=userLeave)
            {
                user.send(ActionFlags.NOTIFY_JUST_LEAVE_ROOM, ResultFlags.OK, userLeave.nickName);
            }
        }
    }
}
