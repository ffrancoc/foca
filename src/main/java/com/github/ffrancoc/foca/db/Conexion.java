package com.github.ffrancoc.foca.db;

import com.github.ffrancoc.foca.model.*;

import java.sql.*;
import java.util.ArrayList;

public class Conexion {
    // nombre del driver
    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";

    public static ConnectionObject connect(String url, String user, String pass) {
        Connection conn = null;
        ConnectionObject connObj = new ConnectionObject(conn, "");

        try {
            // Registro del driver
            Class.forName(JDBC_DRIVER);

            // Creacion de la conexion
            conn = DriverManager.getConnection(url, user, pass);
            connObj.setConn(conn);
            connObj.setMessage("Connection succesfully");
        } catch (ClassNotFoundException | SQLException e) {
            connObj.setMessage("Error to connect to database, "+e.getMessage());
        }

        return connObj;
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

    // Funcion que devuelve las columnas y filas de una consulta
    public static QueryData executeQuery(Connection conn, String sqlQuery) {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<ArrayList<String>> rows = new ArrayList<>();

        QueryData queryData = new QueryData(columns, rows);

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sqlQuery);
            ResultSetMetaData rsMD = rs.getMetaData();

            for( int x = 1; x < rsMD.getColumnCount(); x++) {
                columns.add(rsMD.getColumnName(x));
            }

            while (rs.next()) {
                ArrayList<String> row = new ArrayList<>();
                for (int x = 1; x < rsMD.getColumnCount(); x++) {
                    row.add(rs.getString(x));
                }
                rows.add(row);
            }

        }catch (SQLException e) {
            System.err.println("Error to load execute query from table, "+e.getMessage());
        }
        return queryData;
    }


    // Funciones de Metadata
    // Funcion que devuelve el nombre de todas las tablas en la base de datos
    public static ArrayList<TableInfo> getTableNames(Connection conn) {
        ArrayList<TableInfo> tables = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(new TableInfo(rs.getString("TABLE_NAME"), countData(conn, rs.getString("TABLE_NAME"))));
            }
        }catch (SQLException e) {
            System.err.println("Error to load table names from database, "+e.getMessage());
        }
        return tables;
    }

    // Funcion que devuelve el nombre de todas las vistas en la base de datos
    public static ArrayList<TableInfo> getViewNames(Connection conn) {
        ArrayList<TableInfo> views = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"VIEW"});
            while (rs.next()) {
                views.add(new TableInfo(rs.getString("TABLE_NAME"), countData(conn, rs.getString("TABLE_NAME"))));
            }
        }catch (SQLException e) {
            System.err.println("Error to load view names from database, "+e.getMessage());
        }

        return views;
    }

    // Funcion que devuelve informacion de columnas de una tabla
    public static ArrayList<ColumnInfo> columnData(Connection conn, String tableName) {
        ArrayList<ColumnInfo> columns = new ArrayList<>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getColumns(null, null, tableName, null);
            while (rs.next()) {
                // Verificar si la columna es llave primaria
                boolean pk = rs.getString("COLUMN_NAME").equals(pkColumn(conn, tableName));
                // Obtener todas las columnas que sean llaves foraneas
                FKInfo fkInfo = fkColumn(conn, tableName, rs.getString("COLUMN_NAME"));
                // Guardando la informacion de las columnas de la tabla
                ColumnInfo columnInfo = new ColumnInfo(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME")+"("+rs.getInt("COLUMN_SIZE")+")", pk, fkInfo);
                columns.add(columnInfo);
            }
        }catch (SQLException e) {
            System.err.println("Error to load column info, "+e.getMessage());
        }

        return columns;
    }

    // Funcion que devuelve el nombre de la columna que contiene la llave llave primaria de la tabla
    public static String pkColumn(Connection conn, String tableName) {
            String pkColumn = "";
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                pkColumn = rs.getString("COLUMN_NAME");
            }
        }catch (SQLException e) {
            System.err.println("Error to load pk column info, "+e.getMessage());
        }
        return pkColumn;
    }

    // Funcion que devuelve informacion de las llaves foraneas de la tabla
    public static FKInfo fkColumn(Connection conn, String tableName, String colName) {
        FKInfo fkInfo = new FKInfo();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getImportedKeys(null, null, tableName);
            while (rs.next()) {
                String column_name = rs.getString("FKCOLUMN_NAME");
                String pk_table = rs.getString("PKTABLE_NAME");
                String pk_column = rs.getString("PKCOLUMN_NAME");
                //String constraint_name = rs.getString("PKCOLUMN_NAME");

                if (column_name.equals(colName)){
                    // Guardar el nombre de la tabla de referencia y columna de la misma
                    fkInfo.setColumnName(pk_column);
                    fkInfo.setTableParent(pk_table);
                }
            }
        }catch (SQLException e) {
            System.err.println("Error to load fk column info, "+e.getMessage());
        }
        return fkInfo;
    }


    // Funcion que devuelve el numero de filas de una tabla
    public static int countData(Connection conn,  String table) {
        int count = 0;
        try {
            String query = "SELECT COUNT(*) FROM `" + table+"`;";
            Statement  statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            count = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error to load count data from table, "+e.getMessage());
        }
        return count;
    }
}
