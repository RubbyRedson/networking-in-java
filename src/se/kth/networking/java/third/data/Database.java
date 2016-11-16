package se.kth.networking.java.third.data;

import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.User;
import se.kth.networking.java.third.model.Wish;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public class Database implements IRepository{

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:8889/networking-in-java";
    private static final String USER = "root";
    private static final String PASS = "root";

    public Database(){}


    private Connection getConnection(){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return con;
    }

    private Statement getStatement(){
        Statement stmt = null;
        try {
            Connection con = getConnection();
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stmt;
    }

    private PreparedStatement getPreparedStatement(String sql){
        Connection con = getConnection();
        PreparedStatement preparedStatement = null;
        try {
             preparedStatement = con.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    public void saveItem(int userid, Item item) {
        
    }

    @Override
    public void saveWish(int userid, Wish wish) {

    }

    @Override
    public List<Item> getAllItems() {
        return null;
    }

    @Override
    public User getUserById(int id) {
        return null;
    }

    @Override
    public User login(String username, String password) throws LoginException {
        return null;
    }

    @Override
    public User register(String username, String password) {
        return null;
    }

    /*
    @Override
    public void saveItem(Item item) {
        Statement stmt = getStatement();
        try {
            PreparedStatement prepared = getPreparedStatement("insert into items (name, price, seller) VALUES (?, ?, ?)");
            prepared.setString(1, item.getName());
            prepared.setFloat(2, item.getPrice());
            //prepared.setInt(3, item.getSeller().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */

}
