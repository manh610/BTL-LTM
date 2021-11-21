/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author DUC
 */

import entity.Message;
import entity.Room;
import entity.User;
import entity.Message;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class MessageDAO extends IDAO<Message> {
    
    UserDAO userDAO;
    public MessageDAO(Connection conn) {
        this.conn = conn;
        try {
            this.statement = this.conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public List<Message> selectAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

    public int insert(User user, Room room, Message message) {
        String sql = "INSERT INTO MESSAGE (USERID,"+
				"ROOMID,"+
				"CONTENT,"+
				"SENDTIME)"+
				"VALUES (?,?, ?, ?)";
        try{
            this.preStatement = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.preStatement.setInt(1, user.getId());
            this.preStatement.setInt(2, room.getId());
            this.preStatement.setString(3, message.getContent());
//            this.preStatement.setDate(4, java.sql.Date.valueOf(message.getSendTime()));
            int check = this.preStatement.executeUpdate();
            return check;
        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
       
    }
    
    @Override
    public Message selectById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Message> selectByRoomId(Room room){
        List<Message> messages = new ArrayList<>();
        String sql = "Select * from message where RoomId = ?";
        try{
            this.preStatement = this.conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
            this.preStatement.setInt(1, room.getId());
            rs = preStatement.executeQuery(sql);
            Message message = new Message();
            while(rs.next()){
                message.setId(rs.getInt("ID"));
                message.setUser(userDAO.selectById(rs.getInt("UserID")));
                message.setContent("Content");
                message.setSendTime(rs.getDate("SendTime"));
                messages.add(message);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return messages;
    }

    
    @Override
    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int insert(Message object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int update(Message object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
