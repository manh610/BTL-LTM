/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Entity.Message;
import Entity.Room;
import Entity.UserRoom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
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
    public Room[] selectAll() {
        Room[] rooms = null;
        UserRoom[] userrooms =null;
        Message[] messages = null;
        Room[] result;
        try {
            String sql = "Select * from room";
            rs = statement.executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                int id=rs.getInt(1);
                String description = rs.getString(2);
                userrooms = userromDAO.selectByIdRoom(id);
                messages = messageDAO.selectByIdRoom(id);
                Room room = new Room(id, description, userrooms, messages);
                i++;
            }
            result = new Room[i];
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    

    @Override
    public int insert(Room room) {
        String sql = "INSERT INTO ROOM (DESCRIPTION)"
                + "VALUES (?)";
        try {
            this.preStatement = this.conn.prepareStatement(sql);
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
