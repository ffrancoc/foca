package com.github.ffrancoc.foca.lib;

import com.github.ffrancoc.foca.model.TableInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Conexion {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://localhost:3306/";

    public static Connection connect(String dbName, String user, String pass) {
        Connection conn = null;
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL+dbName, user, pass);
            System.out.println("Connected database successfully...");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error to connect to database, "+e.getMessage());
        }

        return conn;
    }


    public static ArrayList<TableInfo> getTableNames(Connection conn) {
        ArrayList<TableInfo> tables = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(new TableInfo(rs.getString("TABLE_NAME"), countData(conn, rs.getString("TABLE_NAME"))));
                //tables.add(rs.getString("TABLE_NAME"));
                //System.out.println(rs.getString("TABLE_NAME")+"("+countData(conn, rs.getString("TABLE_NAME"))+")");
                //System.out.println(rs.getString("TABLE_NAME"));
            }
        }catch (SQLException e) {
            System.err.println("Error to load table names from database, "+e.getMessage());
        }

        return tables;
    }


    public static ArrayList<TableInfo> getViewNames(Connection conn) {
        ArrayList<TableInfo> views = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"VIEW"});
            while (rs.next()) {
                views.add(new TableInfo(rs.getString("TABLE_NAME"), countData(conn, rs.getString("TABLE_NAME"))));
//                System.out.println(rs.getString("TABLE_NAME")+"("+countData(conn, rs.getString("TABLE_NAME"))+")");
                //System.out.println(rs.getString("TABLE_NAME"));
            }
        }catch (SQLException e) {
            System.err.println("Error to load view names from database, "+e.getMessage());
        }

        return views;
    }

    public static int countData(Connection conn,  String table) {
        int count = 0;
        try {
            String query = "SELECT COUNT(*) FROM `" + table+"`;";
            PreparedStatement  statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error to load count data from table, "+e.getMessage());
        }
        return count;
    }
}
