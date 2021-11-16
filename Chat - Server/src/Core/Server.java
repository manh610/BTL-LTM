/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;
import DAO.IDAO;
import DAO.UserDAO;
import Entity.User;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Observer;
import java.util.Random;
/**
 *
 * @author APC-LTN
 */
public class Server extends Observable
{
    int port = 11000;
    private static String DB_URL = "jdbc:mysql://localhost:3306/btl_ltm";
    private static String USER_NAME = "root";
    private static String PASSWORD = "123456";
    ServerSocket serverSocket;
    Thread threadAccept, threadProcess;
    ArrayList<UserActions> listUser = new ArrayList<>();
    ArrayList<RoomManagement> listRoom = new ArrayList<>();
    ArrayList<UserActions> listUserWaitLogout = new ArrayList<>();
    UserDAO userDAO;
    public Server(Observer obs)  
    {
        this.addObserver(obs);
    }
    public Server(ServerSocket serverSocket, Observer obs)   
    {
        this.addObserver(obs);
        this.serverSocket = serverSocket;
    }
    
    public void dispose() throws IOException
    {
        if(threadAccept!=null)
        {
            threadAccept.stop();
            threadProcess.stop();
            serverSocket.close(); 
        }
    }
    
    public boolean startServer() throws SQLException 
            
    {
        try 
        {
            serverSocket = new ServerSocket(port);
            startThreadAccept();
            startThreadProcess();
            notifyObservers("Khởi động server thành công");
            userDAO = new UserDAO(DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD));
            return true;
        } catch (IOException ex) 
        {
            notifyObservers("Không thể khởi động server");
            return false;
        }
    }
    void startThreadAccept()  
    {
        threadAccept = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                try 
                {
                    while(true)
                    {
                        Socket socket = serverSocket.accept();
                        UserActions newUser = new UserActions(socket);
                        newUser.timeConnect = new Date();
                        listUser.add(newUser);
                    }
                }catch (IOException ex) {
                    notifyObservers("Lỗi kết nối");
                }
            }
        });
        threadAccept.start();
    }
    
    void startThreadProcess()  
    {
        threadProcess = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                try 
                {
                    while(true)
                    {
                        checkRequest();
                        checkTimeConnect();
                        
                        if(listUserWaitLogout.size()>0)  
                            removeUserLoggedOut();
                        
                        Thread.sleep(0);  
                    }
                    
                }catch (IOException ex) {
                    notifyObservers("Lỗi kết nối");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        });
        threadProcess.start();
    }
    
    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
    
    void checkRequest() throws IOException
    {
        int size = listUser.size();
        for(int i=0; i<size; i++)
        {
            UserActions user = listUser.get(i);
            String request = user.read();
            if(request!=null)
                processRequest(user, request);
        }
    }
    
    void checkTimeConnect()
    {
        Date now = new Date();
        int size = listUser.size();
        long second = 0;
        for(int i=0; i<size; i++)
        {
            UserActions user = listUser.get(i);
            if(user.logined==false)
            {
                second = (now.getTime() - user.timeConnect.getTime()) / 1000;
                if(second>10) 
                {
                    listUserWaitLogout.add(user);
                }
            }
        }
    }
    
    void removeUserLoggedOut()
    {
        int size = listUserWaitLogout.size();
        for(int i=0; i<size; i++)
        {
            UserActions user = listUserWaitLogout.get(i);
            listUser.remove(user);
        }
        listUserWaitLogout.clear();
    }
    
    void processRequest(UserActions user, String request)
    {
        String[] lines = request.split(";");
        String actionType = lines[0];
        switch (actionType)
        {
            case ActionFlags.LOGIN:
            {
                String username = lines[1];
                String password = lines[2];
                System.out.println(username + password);
                if(checkUser(username, password))
                {
                    user.nickName = username;
                    user.logined = true;
                    user.send(actionType, ResultFlags.OK, "OK");
                    notifyObservers(user.nickName + " vừa đăng nhập thành công");
                }else
                {
                    user.send(actionType, ResultFlags.ERROR, "Không tồn tại tài khoản này!");
                }
                break;
            }
            case ActionFlags.REGISTER:
            {
                String username = lines[1];
                String password = lines[2];
                String displayName = lines[3];
                if(checkRegister(username, password, displayName))
                {
                    user.send(actionType, ResultFlags.OK, "OK");
                    notifyObservers(username + " vừa đăng ký thành công");
                }else
                {
                    user.send(actionType, ResultFlags.ERROR, "Trùng tài khoản hoặc chứa ký tự không thể xử lý!");
                }
                break;
            }
            case ActionFlags.CREATE_ROOM:
            {
                String roomName = lines[1]; 
                RoomManagement room = generalRoom(roomName);
                listRoom.add(room);
                user.room = room;
                if(user.send(actionType, ResultFlags.OK, room.roomID))
                    room.addUser(user);
                notifyObservers(user.nickName + " vừa tạo phòng " + roomName);
                break;
            }
            case ActionFlags.GET_LIST_ROOM:
            {
                int size = listRoom.size();
                int rowsPerBlock = 500;    
                if(size>0)
                {
                    String listRoom = "";
                    int start=0;
                    int end=0;
                    int numberBlock = (int)Math.floor(size/(double)rowsPerBlock);
                    for (int i = 0; i < numberBlock; i++) 
                    {
                        start = i*rowsPerBlock;
                        end = start + rowsPerBlock;
                        listRoom = "";
                        for (int j = start; j < end; j++) 
                        {
                            RoomManagement room = this.listRoom.get(j);
                            listRoom+= room.roomID + "<col>" + room.roomName + "<col>" + room.countUser() + "<col>" + "<row>";
                        }
                        System.out.print("Gửi lần thứ: " + i);
                        user.send(actionType, ResultFlags.OK, listRoom);
                    }
                    
                    listRoom = "";
                    for (int i = end; i < size; i++) 
                    {
                        RoomManagement room = this.listRoom.get(i);
                        listRoom+= room.roomID + "<col>" + room.roomName + "<col>" + room.countUser() + "<col>" + "<row>";
                    }
                    user.send(actionType, ResultFlags.OK, listRoom);
                }else
                {
                    user.send(actionType, ResultFlags.OK, "");
                }
                notifyObservers(user.nickName + " vừa lấy danh sách phòng");
                break;
            }
            case ActionFlags.JOIN_ROOM:
            {
                String roomID = lines[1];   
                int size = listRoom.size();
                boolean success = false;
                for (int i = 0; i < size; i++) 
                {
                    RoomManagement room = listRoom.get(i);
                    if(room.roomID.equals(roomID))
                    {
                        room.addUser(user);
                        user.room = room;
                        user.send(actionType, ResultFlags.OK, roomID);
                        notifyObservers(user.nickName + " vừa tham gia phòng " + room.roomID);
                        user.room.updateNumberUser();
                        user.room.notifyJustJoinRoom(user);
                        success = true;
                    }
                }
                if(success==false)
                {
                    user.send(actionType, ResultFlags.ERROR, "Không tìm thấy phòng");
                    notifyObservers(user.nickName + " không thể tham gia phòng " + roomID);
                }
                
                break;
            }
            case ActionFlags.SEND_MESSAGE:
            {
                String contentMess = "";
                if(lines.length>=2)
                    contentMess = lines[1];   
                user.room.sendToAllUser(user.nickName, contentMess);
                notifyObservers(user.nickName + " vừa gửi tin");
                break;
            }
            case ActionFlags.LEAVE_ROOM:    
            {
                RoomManagement room = user.room;
                room.removeUser(user);
                if(room.countUser()>0)
                {
                    room.notifyJustLeaveRoom(user);
                    room.updateNumberUser();
                }
                else
                    listRoom.remove(room);
                user.room = null;
                notifyObservers(user.nickName + " vừa rời phòng");
                break;
            }
            case ActionFlags.LOGOUT:    
            {
                RoomManagement room = user.room;
                if(room!=null)
                {
                    room.removeUser(user);
                    if(room.countUser()>0)
                    {
                        room.notifyJustLeaveRoom(user);
                        room.updateNumberUser();
                    }
                    else
                        listRoom.remove(room);
                }
                listUserWaitLogout.add(user); 
                notifyObservers(user.nickName + " vừa đăng xuất");
                break;

            }
        }
    }
    
    boolean checkUser(String nickName, String password)
    {
        boolean flag = userDAO.checkLogin(nickName, password);
        if(flag == false)  
            return false;
        return true;
    }
    
    boolean checkRegister(String username, String password, String displayName){
        boolean checkReg = userDAO.checkRegister(username);
        if (checkReg == false){
            return false;
        }
        User user = new User(1, username, password, displayName);
        int flag = userDAO.insert(user);
        if(flag==0){
            return false;
        }
        return true;
    }

    RoomManagement generalRoom(String roomName)
    {
        RoomManagement room = new RoomManagement();
        room.roomName = roomName;
        room.numberUsers = 1;
        room.roomID = generalRoomID();
        return room;
    }
    
    int maxChar = 3;
    String generalRoomID()
    {
        int countRandom = 0;
        String roomID = "";
        do
        {
            if(countRandom>50) 
                maxChar++;
            
            roomID = randomString(maxChar);
            countRandom++;  
        }while(checkRoomID(roomID)==false);
        return roomID;
        
    }
    
    boolean checkRoomID(String roomID)
    {
        int size = listRoom.size();
        for (int i = 0; i < size; i++) 
        {
            RoomManagement room = listRoom.get(i);
            if(room.roomID.equals(roomID))
                return false;
        }
        return true;
    }
    
    String randomString(int length)
    {
        String data = "1234567890qwertyuiopasdfghjklzxcvbnm";
        int sizeData = data.length();
        String result = "";
        Random rd = new Random();
        for (int i = 0; i < length; i++) 
        {
            result += data.charAt(rd.nextInt(sizeData));
        }
        return result;
    }
    
    public int countUser()
    {
        return listUser.size();
    }
    public int countRoom()
    {
        return listRoom.size();
    }
}
