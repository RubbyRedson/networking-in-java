package se.kth.networking.java.third.data;

import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.User;
import se.kth.networking.java.third.model.Wish;

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
public class Database implements IRepository{

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

    private User getUserByUsername(String username) {
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
        return new User() {

            private int id = _id;
            private String username = _username;
            private String password = _password;

            public void setId(int id) {
                this.id = id;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public void setPassword(String password) {
                this.password = password;
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
        };
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
            PreparedStatement prepared = getPreparedStatement("insert into items (name, price, wisher) VALUES (?, ?, ?)");
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

            String hashed = doHash(password);


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
