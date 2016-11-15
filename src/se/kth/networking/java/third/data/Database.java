package se.kth.networking.java.third.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public class Database {

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
}
