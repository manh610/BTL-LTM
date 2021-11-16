/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Entity.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    
    public boolean checkLogin(String username, String password){
//        try{
//            String sql = "Select * from user where username =' " + username + "' and password = ' "+password + " ' ";
//            rs = statement.executeQuery(sql);
//            if(rs.getString("username").equals(username)==true && rs.getString("password").equals(password)==true){
//                return true;
//            }
//            
//        }catch(SQLException e){
//            e.printStackTrace();
//            return false;
//        }
//        return false;
        String sql = "Select * from user where username = ? and password = ? ";
        try{
            this.preStatement = this.conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
            this.preStatement.setString(1, username);
            this.preStatement.setString(2, password);
            ResultSet rs = this.preStatement.executeQuery();
            while(rs.next()){
                return true;
            }
            return false;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean checkRegister(String username){
//        try{
//            String sql = "Select * from user where username =' " + username + "' and password = ' "+password + " ' ";
//            rs = statement.executeQuery(sql);
//            if(rs.getString("username").equals(username)==true && rs.getString("password").equals(password)==true){
//                return true;
//            }
//            
//        }catch(SQLException e){
//            e.printStackTrace();
//            return false;
//        }
//        return false;
        String sql = "Select * from user where username = ?";
        try{
            this.preStatement = this.conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
            this.preStatement.setString(1, username);
            ResultSet rs = this.preStatement.executeQuery();
            while(rs.next()){
                return false;
            }
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User[] selectAll() {
        List<User> users = new ArrayList<>();
        User[] result;
        try{
            String sql = "Select * from user";
            rs = statement.executeQuery(sql);
            int i=0;
            while(rs.next()){
                User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                users.add(user);
                i++;
            }
            result = new User[i];
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return users.toArray(result);
    }

    @Override
    public User[] selectById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO USER (USERNAME,"+
				"PASSWORD,"+
				"DISPLAYNAME)"+
				"VALUES (?,?,?)";
        try{
            this.preStatement = this.conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
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

    @Override
    public int update(User user) {
        
        String sql = "UPDATE USER set "+
				"USERNAME = ?,"+
				"PASSWORD = ?,"+
				"DISPLAYNAME = ?"+
				"Where ID = ?";
        try{
            this.preStatement = this.conn.prepareStatement(sql);
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

    @Override
    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
