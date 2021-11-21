/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Message;
import entity.Room;
import entity.User;
import entity.UserRoom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DUC
 */
public class RoomDAO extends IDAO<Room> {

    UserRoomDAO userromDAO;
    MessageDAO messageDAO;

    public RoomDAO(Connection conn) {
        this.conn = conn;
        try {
            this.statement = this.conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Room> selectAll() {
//        Room[] rooms = null;
//        UserRoom[] userrooms =null;
//        Message[] messages = null;
//        Room[] result;
//        try {
//            String sql = "Select * from room";
//            rs = statement.executeQuery(sql);
//            int i = 0;
//            while (rs.next()) {
//                int id=rs.getInt(1);
//                String description = rs.getString(2);
//                userrooms = userromDAO.selectByIdRoom(id);
//                messages = messageDAO.selectByIdRoom(id);
//                Room room = new Room(id, description, userrooms, messages);
//                i++;
//            }
//            result = new Room[i];
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return result;
    return  null;
    }

    public List<Room> getListRoomByUserId(User user){
        List<Room> listRoom = new ArrayList<Room>();
        String sql =    "SELECT room.ID, room.Description FROM room, userroom, user\n" +
                            "where user.ID = userroom.UserID \n" +
                            "and userroom.RoomID = room.ID\n" +
                            "and user.ID = ?";
        
        try{
            preStatement = this.conn.prepareStatement(sql);
            preStatement.setInt(1, user.getId());
            rs = preStatement.executeQuery(sql);
            Room room = new Room();
            while(rs.next()){
                room.setId(rs.getInt("ID"));
                room.setDescription(rs.getString("Description"));
                room.setListMessage(messageDAO.selectByRoomId(room));
                room.setListUserRoom(userromDAO.selectListUserRoomByRoomId(room.getId()));
                listRoom.add(room);
            }
            return listRoom;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insert(Room room) {
        String sql = "INSERT INTO ROOM (DESCRIPTION)"
                + "VALUES (?)";
        try {
            this.preStatement = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.preStatement.setString(1, room.getDescription());
            int check = this.preStatement.executeUpdate();
            return check;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public Room selectById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

}