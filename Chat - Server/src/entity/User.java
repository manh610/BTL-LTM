package entity;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String username;
    private String password;
    private String displayName;
    public User() {
    }

    public User(String username, String password, String displayName, int logged) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }
    
    public User(int id, String username, String password, String displayName, int logged) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

 
}
