/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;


/**
 *
 * @author ADMIN
 */
public abstract class IDAO<T> {
    public Statement statement;
    public PreparedStatement preStatement;
    public Connection conn;
    public ResultSet rs;
    public abstract List<T> selectAll();
    public abstract T selectById(int id);
    public abstract int insert(T object);
    public abstract void closeConnection();
    
}