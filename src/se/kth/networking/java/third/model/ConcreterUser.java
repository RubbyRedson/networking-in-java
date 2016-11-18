package se.kth.networking.java.third.model;

/**
 * Created by Nick on 11/18/2016.
 */
public class ConcreterUser implements User {
    private int id;
    private String password;
    private String username;

    public ConcreterUser(int id, String password, String username) {
        this.id = id;
        this.password = password;
        this.username = username;
    }

    public ConcreterUser(String password, String username) {
        this.password = password;
        this.username = username;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
