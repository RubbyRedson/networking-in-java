package se.kth.networking.java.third.data;

import se.kth.networking.java.third.model.*;

import javax.security.auth.login.LoginException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public class Database implements IRepository {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:8889/networking-in-java";
    private static final String USER = "root";
    private static final String PASS = "root";
    Connection connection;

    public Database(){}

    private String doHash(String src){
        MessageDigest md = null;
        String hashed = "SoucePan";
        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(src.getBytes());
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hashed = bigInt.toString(16);

            while (hashed.length() < 32){
                hashed = "0" + hashed;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hashed;
    }


    private Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private Statement getStatement(){
        Statement stmt = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stmt;
    }

    private PreparedStatement getPreparedStatement(String sql){
        connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
             preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }

    private void safeCloseConnection(){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUserByUsername(String username) {
        User user = null;
        try {
            PreparedStatement prepared = getPreparedStatement("select * from users where username = ?");
            prepared.setString(1, username);
            ResultSet rs = prepared.executeQuery();
            while(rs.next()){
                user = fillUser(rs.getInt(1), rs.getString(2), rs.getString(3));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return user;
    }

    private User fillUser(int _id, String _username, String _password){
        return new ConcreterUser(_id, _password, _username);
    }

    @Override
    public void saveItem(int userid, Item item) {
        try {
            PreparedStatement prepared = getPreparedStatement("insert into items (name, price, seller) VALUES (?, ?, ?)");
            prepared.setString(1, item.getName());
            prepared.setFloat(2, item.getPrice());
            prepared.setInt(3, userid);
            prepared.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }
    }

    @Override
    public void saveWish(int userid, Wish wish) {
        try {
            PreparedStatement prepared = getPreparedStatement("insert into wishes (name, price, wisher) VALUES (?, ?, ?)");
            prepared.setString(1, wish.getName());
            prepared.setFloat(2, wish.getPrice());
            prepared.setInt(3, userid);
            prepared.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }
    }

    @Override
    public void saveNotification(Notification notification) {
        try {
            PreparedStatement prepared = getPreparedStatement("insert into notifications (text, user) VALUES (?, ?)");
            prepared.setString(1, notification.getText());
            prepared.setFloat(2, notification.getUserId());
            prepared.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }
    }

    @Override
    public List<Notification> getUserNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();

        try {
            PreparedStatement prepared = getPreparedStatement("select * from notifications where user like ?");
            prepared.setInt(1, userId);
            ResultSet rs = prepared.executeQuery();
            while(rs.next()){
                //id, text, user
                //(int id, String text, int userId)

                notifications.add(new Notification(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return notifications;
    }

    @Override
    public List<Item> getAllItems() {
        Statement stmt = getStatement();
        List<Item> items = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery("select * from items");

            while(rs.next()){
                //(int id, String name, float price, Client seller, String currency)
                Item item = new Item(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getInt(5), rs.getString(4));
                items.add(item);
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return items;
    }

    @Override
    public Item getItemById(int id) {
        Item item = null;
        try {
            PreparedStatement prepared = getPreparedStatement("select * from items where id = ?");
            prepared.setInt(1, id);
            ResultSet rs = prepared.executeQuery();
            while(rs.next()){
                //int id, String name, float price, int seller, String currency
                item = new Item(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getInt(5), rs.getString(4));
                item.setBuyer(rs.getInt(6));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return item;
    }

    @Override
    public Item getItemByName(String name) {
        Item item = null;
        try {
            PreparedStatement prepared = getPreparedStatement("select * from items where name like ?");
            prepared.setString(1, name);
            ResultSet rs = prepared.executeQuery();
            while(rs.next()){
                //int id, String name, float price, int seller, String currency
                item = new Item(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getInt(5), rs.getString(4));
                item.setBuyer(rs.getInt(6));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        try {
            PreparedStatement prepared = getPreparedStatement("UPDATE items SET name=?, price=?, currency=?, seller=?, buyer=? where id=?");
            prepared.setString(1, item.getName());
            prepared.setFloat(2, item.getPrice());
            prepared.setString(3, item.getCurrency());
            prepared.setInt(4, item.getSeller());
            prepared.setInt(5, item.getBuyer());
            prepared.setInt(3, item.getId());
            prepared.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return item;
    }

    @Override
    public Wish getWishById(int id) {
        Wish wish = null;
        try {
            PreparedStatement prepared = getPreparedStatement("select * from wishes where id = ?");
            prepared.setInt(1, id);
            ResultSet rs = prepared.executeQuery();
            while(rs.next()){

                //int id, String name, float price, int wisher, String currency
                wish = new Wish(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getInt(4), rs.getString(5));

            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return wish;
    }

    @Override
    public void deleteWish(int userId, String goodName) {
        try {
            PreparedStatement prepared = getPreparedStatement("delete * from wishes where wisher = ? and name like ?");
            prepared.setInt(1, userId);
            prepared.setString(1, goodName);
            ResultSet rs = prepared.executeQuery();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }
    }

    @Override
    public List<Wish> getAllWishes() {
        Statement stmt = getStatement();
        List<Wish> wishes = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery("select * from wishes");

            while(rs.next()){

                //int id, String name, float price, int wisher, String currency
                //id,           name,       price,      currenct,       wisher

                wishes.add(new Wish(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getInt(5), rs.getString(4)));
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return wishes;
    }

    @Override
    public User getUserById(int id) {
        User user = null;
        try {
            PreparedStatement prepared = getPreparedStatement("select * from users where id = ?");
            prepared.setInt(1, id);
            ResultSet rs = prepared.executeQuery();
            while(rs.next()){
                user = fillUser(rs.getInt(1), rs.getString(2), rs.getString(3));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            safeCloseConnection();
        }

        return user;
    }

    @Override
    public User login(String username, String password) throws LoginException {
        User user = getUserByUsername(username);

        if(user != null){
            if(user.getPassword().equals(doHash(password))){
                return user;
            }else{
                user = null;
            }
        }

        return user;
    }

    @Override
    public User register(String username, String password) {

        User u = getUserByUsername(username);
        User newUser = null;
        if(u == null){
            PreparedStatement prepared = getPreparedStatement("insert into users (username, password) VALUES (?, ?)");
            try {
                prepared.setString(1, username);
                prepared.setString(2, doHash(password));
                prepared.execute();

                newUser = getUserByUsername(username);

            } catch (SQLException e) {

                e.printStackTrace();
            }finally {
                safeCloseConnection();
            }
        }

        return newUser;
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
