/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import controller.UserController;
import flag.ActionFlags;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Observer;
import java.util.Random;

/**
 *
 * @author APC-LTN
 */
public class Server extends Observable {

    int port = 11000;
    private BufferedReader bufferReader;
    private DataOutputStream dataOutputStream;
    private UserController userController;
    private Observer obs;
    ServerSocket serverSocket;
    Thread threadAccept, threadProcess;

    public Server(Observer obs) {
        this.addObserver(obs);
        this.obs = obs;
    }

    public Server(ServerSocket serverSocket, Observer obs) {
        this.addObserver(obs);
        this.obs = obs;
        this.serverSocket = serverSocket;
    }

    public void dispose() throws IOException {
        if (threadAccept != null) {
            threadAccept.stop();
//            threadProcess.stop();
            serverSocket.close();
        }
    }

    public boolean startServer() throws SQLException {
        try {
            serverSocket = new ServerSocket(port);
            startThreadAccept();
//            startThreadProcess();
            notifyObservers("Khởi động server thành công");

            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            notifyObservers("Không thể khởi động server");
            return false;
        }
    }

    private void startThreadAccept() {
        threadAccept = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        userController = new UserController(bufferReader, dataOutputStream, obs);
                        checkRequest();
                    }
                } catch (IOException ex) {
                    notifyObservers("Lỗi kết nối");
                } catch (SQLException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        threadAccept.start();
    }

    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
//    private void startThreadProcess() {
//        threadProcess = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        checkRequest();
//                        Thread.sleep(0);
//                    }
//                } catch (IOException ex) {
//                    notifyObservers("Lỗi kết nối");
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//        threadProcess.start();
//    }

    void checkRequest() throws IOException {
        if (bufferReader == null) {
            return;
        }

        if (bufferReader.ready()) {
            String request = bufferReader.readLine();
            if (request != null) {
                processRequest(request);
            }
        }
    }

    void processRequest(String request) {
        String[] lines = request.split(";");
        String actionType = lines[0];
        switch (actionType) {
            case ActionFlags.LOGIN: {
                String username = lines[1];
                String password = lines[2];
                userController.checkLogin(username, password, actionType);
                break;
            }
            case ActionFlags.REGISTER: {
                String username = lines[1];
                String password = lines[2];
                String displayName = lines[3];
                userController.checkRegister(username, password, displayName, actionType);
                break;
            }
//            case ActionFlags.CREATE_ROOM: {
//                String roomName = lines[1];
//                RoomController room = generalRoom(roomName);
//                listRoom.add(room);
//                user.room = room;
//                if (user.send(actionType, ResultFlags.OK, room.roomID)) {
//                    room.addUser(user);
//                }
//                notifyObservers(user.nickName + " vừa tạo phòng " + roomName);
//                break;
//            }
//            case ActionFlags.GET_LIST_ROOM: {
//                int size = listRoom.size();
//                int rowsPerBlock = 500;
//                if (size > 0) {
//                    String listRoom = "";
//                    int start = 0;
//                    int end = 0;
//                    int numberBlock = (int) Math.floor(size / (double) rowsPerBlock);
//                    for (int i = 0; i < numberBlock; i++) {
//                        start = i * rowsPerBlock;
//                        end = start + rowsPerBlock;
//                        listRoom = "";
//                        for (int j = start; j < end; j++) {
//                            RoomController room = this.listRoom.get(j);
//                            listRoom += room.roomID + "<col>" + room.roomName + "<col>" + room.countUser() + "<col>" + "<row>";
//                        }
//                        System.out.print("Gửi lần thứ: " + i);
//                        user.send(actionType, ResultFlags.OK, listRoom);
//                    }
//
//                    listRoom = "";
//                    for (int i = end; i < size; i++) {
//                        RoomController room = this.listRoom.get(i);
//                        listRoom += room.roomID + "<col>" + room.roomName + "<col>" + room.countUser() + "<col>" + "<row>";
//                    }
//                    user.send(actionType, ResultFlags.OK, listRoom);
//                } else {
//                    user.send(actionType, ResultFlags.OK, "");
//                }
//                notifyObservers(user.nickName + " vừa lấy danh sách phòng");
//                break;
//            }
//            case ActionFlags.JOIN_ROOM: {
//                String roomID = lines[1];
//                int size = listRoom.size();
//                boolean success = false;
//                for (int i = 0; i < size; i++) {
//                    RoomController room = listRoom.get(i);
//                    if (room.roomID.equals(roomID)) {
//                        room.addUser(user);
//                        user.room = room;
//                        user.send(actionType, ResultFlags.OK, roomID);
//                        notifyObservers(user.nickName + " vừa tham gia phòng " + room.roomID);
//                        user.room.updateNumberUser();
//                        user.room.notifyJustJoinRoom(user);
//                        success = true;
//                    }
//                }
//                if (success == false) {
//                    user.send(actionType, ResultFlags.ERROR, "Không tìm thấy phòng");
//                    notifyObservers(user.nickName + " không thể tham gia phòng " + roomID);
//                }
//
//                break;
//            }
//            case ActionFlags.SEND_MESSAGE: {
//                String contentMess = "";
//                if (lines.length >= 2) {
//                    contentMess = lines[1];
//                }
//                user.room.sendToAllUser(user.nickName, contentMess);
//                notifyObservers(user.nickName + " vừa gửi tin");
//                break;
//            }
//            case ActionFlags.LEAVE_ROOM: {
//                RoomController room = user.room;
//                room.removeUser(user);
//                if (room.countUser() > 0) {
//                    room.notifyJustLeaveRoom(user);
//                    room.updateNumberUser();
//                } else {
//                    listRoom.remove(room);
//                }
//                user.room = null;
//                notifyObservers(user.nickName + " vừa rời phòng");
//                break;
//            }
//            case ActionFlags.LOGOUT: {
//                RoomController room = user.room;
//                if (room != null) {
//                    room.removeUser(user);
//                    if (room.countUser() > 0) {
//                        room.notifyJustLeaveRoom(user);
//                        room.updateNumberUser();
//                    } else {
//                        listRoom.remove(room);
//                    }
//                }
//                listUserWaitLogout.add(user);
//                notifyObservers(user.nickName + " vừa đăng xuất");
//                break;
//
//            }
        }
    }

//
//    RoomController generalRoom(String roomName) {
//        RoomController room = new RoomController();
//        room.roomName = roomName;
//        room.numberUsers = 1;
//        room.roomID = generalRoomID();
//        return room;
//    }
//    int maxChar = 3;
//
//    String generalRoomID() {
//        int countRandom = 0;
//        String roomID = "";
//        do {
//            if (countRandom > 50) {
//                maxChar++;
//            }
//
//            roomID = randomString(maxChar);
//            countRandom++;
//        } while (checkRoomID(roomID) == false);
//        return roomID;
//
//    }
//    boolean checkRoomID(String roomID) {
//        int size = listRoom.size();
//        for (int i = 0; i < size; i++) {
//            RoomController room = listRoom.get(i);
//            if (room.roomID.equals(roomID)) {
//                return false;
//            }
//        }
//        return true;
//    }
    String randomString(int length) {
        String data = "1234567890qwertyuiopasdfghjklzxcvbnm";
        int sizeData = data.length();
        String result = "";
        Random rd = new Random();
        for (int i = 0; i < length; i++) {
            result += data.charAt(rd.nextInt(sizeData));
        }
        return result;
    }

}
