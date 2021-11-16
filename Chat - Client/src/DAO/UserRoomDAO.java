/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Entity.Room;
import Entity.User;
import Entity.UserRoom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class UserRoomDAO extends IDAO<UserRoom> {
    
    UserDAO userDAO;

    public UserRoomDAO(Connection conn) {
        this.conn = conn;
        try {
            this.statement = this.conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public UserRoom[] selectAll() {
        List<UserRoom> userRooms = new ArrayList<>();
        UserRoom[] result;
        try{
            String sql = "Select * from userroom";
            rs = statement.executeQuery(sql);
            int i=0;
            while(rs.next()){
                int id = rs.getInt(1);
                int idUser = rs.getInt(2);
                User user = userDAO.selectById(idUser);
                UserRoom userroom = new UserRoom(id, user);
                i++;
            }
            result = new UserRoom[i];
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return userRooms.toArray(result);
    }

    @Override
    public UserRoom selectById(int id) {
        String sql = "Select * from Userroom where id='" + id+"'" ;
        
        try{
            UserRoom userroom = null;
            rs = statement.executeQuery(sql);
            int i=0;
            while(rs.next()){
                int idUser = rs.getInt(2);
                User user = userDAO.selectById(idUser);
                userroom = new UserRoom(id, user);
                i++;
            }
            return userroom;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public UserRoom[] selectByIdRoom(int idRoom) {
        String sql = "Select * from Userroom where roomid='" + idRoom+"'" ;
        List<UserRoom> userrooms = new ArrayList<>();
        UserRoom[] result;
        try{
            UserRoom userroom = null;
            rs = statement.executeQuery(sql);
            int i=0;
            while(rs.next()){
                int id = rs.getInt(1);
                int idUser = rs.getInt(3);
                User user = userDAO.selectById(idUser);
                userroom = new UserRoom(id, user);
                userrooms.add(userroom);
                i++;
            }
            result = new UserRoom[i];
            return result;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insert(UserRoom userroom) {
        return 0;
       
    }
    
    public int insertUserRoom(UserRoom userroom, Room room) {
        String sql = "INSERT INTO USER (USERID,"+
				"ROOMID)"+
				"VALUES (?,?)";
        try{
            this.preStatement = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.preStatement.setInt(1, userroom.getId());
            this.preStatement.setInt(2, room.getId());
            int check = this.preStatement.executeUpdate();
            return check;
        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
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
