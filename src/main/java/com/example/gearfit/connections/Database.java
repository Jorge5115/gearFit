package com.example.gearfit.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:gearFit.db";

    static {
        try {
            // Carga el controlador SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("No se pudo encontrar el controlador JDBC: " + e.getMessage());
        }
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Conexión establecida.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                // Crear la tabla si no existe
                String sql = "CREATE TABLE IF NOT EXISTS registered_users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL, " +
                        "height DOUBLE NOT NULL, " +
                        "weight DOUBLE NOT NULL); ";
                conn.createStatement().execute(sql);
                System.out.println("Tabla creada.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
