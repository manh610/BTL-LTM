/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Entity.Message;
import Entity.Room;
import Entity.User;
import Entity.Message;
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
    public Message[] selectAll() {
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
    
    public Message[] selectByIdRoom(int idRoom){
        List<Message> messages = new ArrayList<>();
        Message[] result;
        try{
            String sql = "Select * from message where idRoom=" + idRoom + "";
            rs = statement.executeQuery(sql);
            int i=0;
            while(rs.next()){
                int id = rs.getInt(1);
                int idUser = rs.getInt(2);
                String content = rs.getString(4);
                Date sendTime = rs.getDate(5);
                User user = userDAO.selectById(idUser);
                Message message = new Message(id, content, sendTime, user);
                messages.add(message);
                i++;
            }
            result = new Message[i];
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return messages.toArray(result);
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

}
