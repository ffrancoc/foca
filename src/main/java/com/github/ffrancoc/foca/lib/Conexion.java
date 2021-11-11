package com.github.ffrancoc.foca.lib;

import com.github.ffrancoc.foca.model.ColumnInfo;
import com.github.ffrancoc.foca.model.FKInfo;
import com.github.ffrancoc.foca.model.TableInfo;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Conexion {
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/";

    public static Connection connect(String dbName, String user, String pass) {
        Connection conn = null;
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL+dbName, user, pass);
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println(metaData.getURL());
            System.out.println("Connected database successfully...");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error to connect to database, "+e.getMessage());
        }

        return conn;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error to close database, " + e.getMessage());
            }
        }
    }


    public static ArrayList<TableInfo> getTableNames(Connection conn) {
        ArrayList<TableInfo> tables = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                ArrayList<ColumnInfo> columns = Conexion.columnData(conn, rs.getString("TABLE_NAME"));
                tables.add(new TableInfo(rs.getString("TABLE_NAME"), countData(conn, rs.getString("TABLE_NAME")), columns));
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
                ArrayList<ColumnInfo> columns = Conexion.columnData(conn, rs.getString("TABLE_NAME"));
                views.add(new TableInfo(rs.getString("TABLE_NAME"), countData(conn, rs.getString("TABLE_NAME")), columns));
//                System.out.println(rs.getString("TABLE_NAME")+"("+countData(conn, rs.getString("TABLE_NAME"))+")");
                //System.out.println(rs.getString("TABLE_NAME"));
            }
        }catch (SQLException e) {
            System.err.println("Error to load view names from database, "+e.getMessage());
        }

        return views;
    }

    public static ArrayList<ColumnInfo> columnData(Connection conn, String table) {
        ArrayList<ColumnInfo> columns = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getColumns(null, null, table, null);
            while (rs.next()) {
                boolean pk = rs.getString("COLUMN_NAME").equals(pkColumn(conn, table));
                FKInfo fkInfo = fkColumn(conn, table, rs.getString("COLUMN_NAME"));
                ColumnInfo columnInfo = new ColumnInfo(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME")+"("+rs.getInt("COLUMN_SIZE")+")", pk, fkInfo);
                columns.add(columnInfo);
                //columnInfo.setName(rs.getString("COLUMN_NAME"));
                //columnInfo.setType(rs.getString("TYPE_NAME")+"("+rs.getInt("COLUMN_SIZE")+")");
                //String name = rs.getString("COLUMN_NAME");
                //String type = rs.getString("TYPE_NAME");
                //int size = rs.getInt("COLUMN_SIZE");

                //System.out.println("Column name: [" + name + "]; type: [" + type + "]; size: [" + size + "]");
            }
        }catch (SQLException e) {
            System.err.println("Error to load column info, "+e.getMessage());
        }

        return columns;
    }

    private static String pkColumn(Connection conn, String tableName) {
            String pkColumn = "";
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                pkColumn = rs.getString("COLUMN_NAME");
                //System.out.println("getPrimaryKeys(): columnName=" + columnName);

            }
        }catch (SQLException e) {
            System.err.println("Error to load pk column info, "+e.getMessage());
        }
        return pkColumn;
    }

    private static FKInfo fkColumn(Connection conn, String tableName, String colName) {
        //String pkColumn = "";
        FKInfo fkInfo = new FKInfo();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getImportedKeys(null, null, tableName);
            while (rs.next()) {
                String column_name = rs.getString("FKCOLUMN_NAME");
                String pk_table = rs.getString("PKTABLE_NAME");
                String pk_column = rs.getString("PKCOLUMN_NAME");
                String constraint_name = rs.getString("PKCOLUMN_NAME");

                if (column_name.equals(colName)){
                    fkInfo.setColumnName(column_name);
                    fkInfo.setTableParent(pk_table);
                    //System.out.println("  "+column_name+" reference to "+ pk_table+"("+constraint_name+")");
                    //System.out.println(tableName+" "+colName);
                    //System.out.println("");
                }
            }
        }catch (SQLException e) {
            System.err.println("Error to load fk column info, "+e.getMessage());
        }
        return fkInfo;
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
