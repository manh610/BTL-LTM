package entity;

import java.io.Serializable;

public class Room implements Serializable {
    private int id;
    private String description;
    private UserRoom userRoom;
    private Message message;

    public Room() {
    }

    public Room(int id, String description, UserRoom userRoom, Message message) {
        this.id = id;
        this.description = description;
        this.userRoom = userRoom;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserRoom getUserRoom() {
        return userRoom;
    }

    public void setUserRoom(UserRoom userRoom) {
        this.userRoom = userRoom;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
