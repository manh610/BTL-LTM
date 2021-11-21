/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import controller.UserController;
import entity.Request;
import entity.User;
import flag.ActionFlags;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author APC-LTN
 */
public class Server {

    private static final int port = 11000;
    private static List<UserController> userControllers = new ArrayList<>();
    private static List<UserController> userWaitLogout = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running");
        try {
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class Handler extends Thread {

        private Socket socket;
        private UserController userController;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                userController = new UserController(socket);
                userControllers.add(userController);
                System.out.println(socket.getLocalAddress() + " da ket noi" + userControllers.size());

                while (true) {
                    if (userController.objectInputStream != null) {
                        System.out.println("User size" + userControllers.size());
                        checkRequest(userController);
                    } else {
                        break;
                    }

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
//                userController.logOut(user, ActionFlags.LOGOUT);
                userControllers.remove(userController);
                userController.userDAO.closeConnection();
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private static void sendListAllUser() {
            for (int i = 0; i < userControllers.size(); i++) {
                UserController uc = userControllers.get(i);
                uc.getAllUser();
                System.out.println("Gui danh sach user cho user " + i);
            }
        }

        private static void checkRequest(UserController uc) throws ClassNotFoundException, IOException {
            Request request = (Request) uc.objectInputStream.readObject();
            processRequest(uc, request);
        }

        private static void processRequest(UserController userController, Request request) throws IOException {
            String actionType = request.getActionType();
            System.out.println(actionType + " " + request.toString());
            switch (actionType) {
                case ActionFlags.LOGIN: {
                    User user = (User) request.getEntity();
                    if (userController.checkLogin(user, actionType)) {
                        System.out.println("Thanh Cong");
                    }
                    break;
                }
                case ActionFlags.REGISTER: {
                    User objUser = (User) request.getEntity();
                    userController.checkRegister(objUser, actionType);
                    break;
                }
                case ActionFlags.LOGOUT: {
                    User user = (User) request.getEntity();
                    userController.logOut(user, actionType);
                    userController.objectInputStream = null;
                    userController.objectOutputStream = null;
                    if(userControllers.remove(userController))
                        sendListAllUser();
//                notifyObservers(user.getUsername() + " vừa đăng xuất");
                    break;
                }
                case ActionFlags.GET_ALL_USER: {
                    sendListAllUser();
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
            case ActionFlags.GET_LIST_ROOM: {
                User user = (User) request.getEntity();
                userController.getListRoom(user);
                System.out.println("Gui danh sach phong cho user");
                break;
            }
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
}
