package entity;

import java.io.Serializable;

public class UserRoom implements Serializable {
    private int id;
    private User user;

    public UserRoom() {
    }

    public UserRoom(int id, User user) {
        this.id = id;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
