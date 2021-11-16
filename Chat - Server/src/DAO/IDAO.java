/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 *
 * @author ADMIN
 */
public abstract class IDAO<T> {
    Statement statement;
    PreparedStatement preStatement;
    Connection conn;
    ResultSet rs;
    
    public abstract T[] selectAll();
    public abstract T[] selectById(int id);
    public abstract int insert(T object);
    public abstract int update(T object);
    public abstract void closeConnection();
    
}
