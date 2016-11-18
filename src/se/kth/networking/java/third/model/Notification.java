package se.kth.networking.java.third.model;

import java.io.Serializable;

/**
 * Created by victoraxelsson on 2016-11-18.
 */
public class Notification implements Serializable {

    int id;
    String text;
    int userId;

    public Notification(String text, int userId){
        this(-1, text, userId);
    }

    public Notification(int id, String text, int userId) {
        this.id = id;
        this.text = text;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getUserId() {
        return userId;
    }
}
