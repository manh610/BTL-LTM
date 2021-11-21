/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import entity.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class UserDAO extends IDAO<User> {

    public UserDAO(Connection conn) {
        this.conn = conn;
        try {
            this.statement = this.conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public User checkLogin(String username, String password){
        String sql = "Select * from user where Username = ? and Password = ? ";
        try{
            this.preStatement = this.conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
            this.preStatement.setString(1, username);
            this.preStatement.setString(2, password);
            ResultSet rs = this.preStatement.executeQuery();
            User user = new User();
            if(rs.next()){
                user.setId(rs.getInt("ID"));
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setDisplayName(rs.getString("DisplayName"));
            }
            return user;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    
    public int checkExistsUser(String username){
        String sql = "Select * from user where username = ?";
        try{
            this.preStatement = this.conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
            this.preStatement.setString(1, username);
            ResultSet rs = this.preStatement.executeQuery();
            if(rs.next()){
                return 0;
            }
            return 1;
        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<User> selectAll() {
        List<User> users = new ArrayList<>();
        try{
            String sql = "Select * from user";
            rs = statement.executeQuery(sql);
            while(rs.next()){
                User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                users.add(user);
            }
            return users;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User selectById(int id) {
        String sql = "Select * from user where id = ?";
        try{
            preStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStatement.setInt(1, id);
            rs = preStatement.executeQuery(sql);
            User user = new User();
            if(rs.next()){
                user.setId(rs.getInt("ID"));
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setDisplayName(rs.getString("DisplayName"));
            }
            return user;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO USER (USERNAME,"+
				"PASSWORD,"+
				"DISPLAYNAME)"+
				"VALUES (?,?,?)";
        try{
            this.preStatement = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.preStatement.setString(1, user.getUsername());
            this.preStatement.setString(2, user.getPassword());
            this.preStatement.setString(3, user.getDisplayName());
            int check = this.preStatement.executeUpdate();
            return check;
        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
    }

    public int update(User user) {
        
        String sql = "UPDATE USER set "+
				"USERNAME = ?,"+
				"PASSWORD = ?,"+
				"DISPLAYNAME = ?," +
                                "ACTIVE = ? " +
				"Where ID = ?";
        try{
            this.preStatement = this.conn.prepareStatement(sql);
            this.preStatement.setString(1, user.getUsername());
            this.preStatement.setString(2, user.getPassword());
            this.preStatement.setString(3, user.getDisplayName());
            this.preStatement.setString(4, user.getActive());
            this.preStatement.setInt(5, user.getId());
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
