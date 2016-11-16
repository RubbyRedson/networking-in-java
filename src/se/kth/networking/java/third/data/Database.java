package se.kth.networking.java.third.data;

import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.User;

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



    public Database(){
        /*
        Connection con = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM items");

            List<String> names = new ArrayList<>();
            while (rs.next()){
                String name = rs.getString(2);
                names.add(name);
            }

            System.out.println(names);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        */
    }

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

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
