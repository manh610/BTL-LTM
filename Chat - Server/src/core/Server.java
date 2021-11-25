/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import controller.UserWorker;
import entity.Message;
import entity.Request;
import entity.Room;
import entity.User;
import entity.UserRoom;
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
    private static final List<UserWorker> userWorkers = new ArrayList<>();

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
        private UserWorker userWorker;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                userWorker = new UserWorker(socket);
                userWorkers.add(userWorker);
                System.out.println(socket.getLocalAddress() + " da ket noi" + userWorkers.size());

                while (true) {
                    if (userWorker.objectInputStream != null) {
                        System.out.println("User size" + userWorkers.size());
                        checkRequest(userWorker);
                    } else {
                        break;
                    }

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                userWorker.logout();
                userWorkers.remove(userWorker);
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private static void checkRequest(UserWorker uc) throws ClassNotFoundException, IOException {
            Request request = (Request) uc.objectInputStream.readObject();
            processRequest(uc, request);
        }

        private static void processRequest(UserWorker userWorker, Request request) throws IOException {
            String actionType = request.getActionType();
//            System.out.println(userWorker.userc.getUsername() + " " + actionType + " ");
            switch (actionType) {
                case ActionFlags.LOGIN: {
                    User user = (User) request.getEntity();
                    userWorker.checkLogin(user.getUsername(), user.getPassword(), actionType);
                    userWorker.getListRoomByUser();
                    sendListUserHome();
                    break;
                }
                case ActionFlags.REGISTER: {
                    User objUser = (User) request.getEntity();
                    userWorker.checkRegister(objUser, actionType);
                    break;
                }
                case ActionFlags.LOGOUT: {
                    userWorker.logout();
                    userWorker.objectInputStream = null;
                    userWorker.objectOutputStream = null;
                    if (userWorkers.remove(userWorker)) {
                        sendListUserHome();
                        List<Room> listRoom = userWorker.getListRoomByUser();
                        if (listRoom != null) {
                            listRoom.forEach(room -> {
                                sendListUserRoom(room);
                            });
                        }
                    }
                    break;
                }

                case ActionFlags.GET_ALL_USER: {
                    userWorker.sendListUserHome();
                    break;
                }
                case ActionFlags.UPDATE_ROOM: {
                    Room room = (Room) request.getEntity();
                    Room roomUpdated = userWorker.updateRoom(room);
                    if (roomUpdated != null) {
                        updateListRoomByUser(roomUpdated);
                    }
                    break;
                }

                case ActionFlags.ADD_USER_TO_ROOM: {
                    UserRoom userRoom = (UserRoom) request.getEntity();
                    Room room = userWorker.addUserToRoom(userRoom);
                    if (room != null) {
                        updateListRoomByUser(room);
                    }
                    break;
                }

                case ActionFlags.CREATE_ROOM: {
                    Room room = (Room) request.getEntity();
                    userWorker.createRoomByUser(room, actionType);
                    break;
                }

                case ActionFlags.LEAVE_ROOM: {
                    UserRoom userRoom = (UserRoom) request.getEntity();
                    Room room = userWorker.LeaveRoom(userRoom, actionType);
                    if (room != null) {
                        sendListUserRoom(room);
                        updateListRoomByUser(room);
                    }
                    break;
                }
                case ActionFlags.CREATE_OR_JOIN_PRIVATE_ROOM: {
                    User user = (User) request.getEntity();
                    Room room = userWorker.checkExistsPrivateRoom(user);
                    if (room != null) {
                        // open view chat
                        userWorker.openRoomChat(room, ActionFlags.OPEN_ROOM_CHAT);
                        userWorker.getListRoomByUser();
                    }
                    break;
                }

                case ActionFlags.SEND_MESSAGE: {
                    Message message = (Message) request.getEntity();
                    message = userWorker.createMessage(message);
                    if (message != null) {
                        Room room = userWorker.getRoom(message.getRoomId());
                        if (room != null) {
                            sendMessageToAllUserInRoom(room, message);
                        }
                    }
                    break;
                }
            }
        }

        private static void sendListUserHome() {
            for (int i = 0; i < userWorkers.size(); i++) {
                UserWorker uc = userWorkers.get(i);
                uc.sendListUserHome();
                System.out.println("Gui danh sach user cho user " + i);
            }
        }

        private static void sendListUserRoom(Room room) {
            for (int i = 0; i < userWorkers.size(); i++) {
                UserWorker uc = userWorkers.get(i);
                uc.sendListUserRoom(room);
            }
        }

        private static void updateListRoomByUser(Room room) {
            for (UserRoom userRoom : room.getListUserRoom()) {
                for (int i = 0; i < userWorkers.size(); i++) {
                    UserWorker uc = userWorkers.get(i);
                    if (uc.userc.getId() == userRoom.getUser().getId()) {
                        uc.getListRoomByUser();
                    }
                }
            }
        }

        private static void sendMessageToAllUserInRoom(Room room, Message message) {

            for (UserRoom userRoom : room.getListUserRoom()) {
                for (int i = 0; i < userWorkers.size(); i++) {
                    UserWorker uc = userWorkers.get(i);
                    if (uc.userc.getId() == userRoom.getUser().getId()) {
                        uc.openRoomChat(room, ActionFlags.OPEN_ROOM_CHAT);
                        uc.sendMessage(message, ActionFlags.SEND_MESSAGE);
                        uc.sendListUserHome();
                        uc.getListRoomByUser();
                    }
                }
            }
        }
    }
}
