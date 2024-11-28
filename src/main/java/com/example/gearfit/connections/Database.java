package com.example.gearfit.connections;

import com.example.gearfit.exceptions.DatabaseConnectionException;
import com.example.gearfit.exceptions.DatabaseInitializationException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

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
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error al conectar con la base de datos", e);
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                try {
                    // Crear la tabla de usuarios registrados
                    String createUsersTable = "CREATE TABLE IF NOT EXISTS registered_users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT NOT NULL," +
                            "email TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL, " +
                            "height INT NOT NULL, " +
                            "weight DOUBLE NOT NULL, " +
                            "calories INT NOT NULL);";
                    conn.createStatement().execute(createUsersTable);

                    // Crear la tabla de las rutinas de cada usuario
                    String createRoutinesTable = "CREATE TABLE IF NOT EXISTS routines (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "user_id INTEGER NOT NULL," +
                            "name TEXT NOT NULL," +
                            "FOREIGN KEY (user_id) REFERENCES registered_users(id));";
                    conn.createStatement().execute(createRoutinesTable);

                    // Crear la tabla de los dias de la semana de cada rutina
                    String createRoutineDaysTable = "CREATE TABLE IF NOT EXISTS routine_days (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "routine_id INTEGER NOT NULL," +
                            "day_of_week TEXT NOT NULL," +     // Ejemplo: 'Lunes', 'Martes', etc.
                            "FOREIGN KEY (routine_id) REFERENCES Routines(id));";
                    conn.createStatement().execute(createRoutineDaysTable);

                    // Crear la tabla de los ejercicios de cada dia
                    String createExercisesTable = "CREATE TABLE IF NOT EXISTS exercises (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "routine_day_id INTEGER NOT NULL," +
                            "name TEXT NOT NULL," +
                            "tempo TEXT," +               // Ejemplo: '3-1-1-0'
                            "rest_time INTEGER," +        // Tiempo de descanso en segundos
                            "FOREIGN KEY (routine_day_id) REFERENCES RoutineDays(id));";
                    conn.createStatement().execute(createExercisesTable);

                    // Crear la tabla de las series de cada ejercicio
                    String createExerciseSetsTable = "CREATE TABLE IF NOT EXISTS exercise_sets (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "exercise_id INTEGER NOT NULL," +
                            "set_number INTEGER NOT NULL," +     // Número de la serie (1, 2, 3, etc.)
                            "repetitions INTEGER NOT NULL," +    // Número de repeticiones para esta serie
                            "weight DOUBLE," +                   // Peso en kg para esta serie
                            "FOREIGN KEY (exercise_id) REFERENCES Exercises(id));";
                    conn.createStatement().execute(createExerciseSetsTable);

                    // Verificar si el usuario "admin" ya existe
                    String checkAdminQuery = "SELECT COUNT(*) FROM registered_users WHERE username = 'admin'";
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(checkAdminQuery)) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            // Si el usuario no existe, lo insertamos
                            String insertAdminUser = "INSERT INTO registered_users (username, email, password, height, weight, calories) VALUES (?, ?, ?, ?, ?, ?)";
                            String hashedPassword = BCrypt.hashpw("12345678", BCrypt.gensalt()); // Hashea la contraseña

                            try (PreparedStatement pstmt = conn.prepareStatement(insertAdminUser)) {
                                pstmt.setString(1, "admin");
                                pstmt.setString(2, "admin@gmail.com");
                                pstmt.setString(3, hashedPassword);
                                pstmt.setInt(4, 180);
                                pstmt.setDouble(5, 75.0);
                                pstmt.setInt(6, 2000);
                                pstmt.executeUpdate();
                            }

                            // Ahora insertamos las rutinas genéricas para el usuario "admin"
                            String insertRoutines = "INSERT INTO routines (user_id, name) VALUES " +
                                    "(1, 'Rutina Full Body'), " +
                                    "(1, 'Rutina Fuerza'), " +
                                    "(1, 'Rutina Cardio');";
                            conn.createStatement().execute(insertRoutines);

                            // Insertar los días de la rutina
                            String insertRoutineDays = "INSERT INTO routine_days (routine_id, day_of_week) VALUES " +
                                    "(1, 'Lunes'), " +
                                    "(1, 'Miércoles'), " +
                                    "(1, 'Viernes'), " +
                                    "(2, 'Martes'), " +
                                    "(2, 'Jueves'), " +
                                    "(3, 'Sábado');";
                            conn.createStatement().execute(insertRoutineDays);

                            // Insertar ejercicios para cada día de rutina
                            String insertExercises = "INSERT INTO exercises (routine_day_id, name, tempo, rest_time) VALUES " +
                                    "(1, 'Sentadillas', '3-1-1-0', 60), " +
                                    "(1, 'Press de Banca', '2-0-2-0', 90), " +
                                    "(2, 'Dominadas', '3-1-1-0', 60), " +
                                    "(2, 'Flexiones', '2-0-2-0', 90), " +
                                    "(3, 'Correr', '1-0-1-0', 120), " +
                                    "(3, 'Abdominales', '2-0-2-0', 60);";
                            conn.createStatement().execute(insertExercises);

                            // Insertar series de los ejercicios
                            String insertExerciseSets = "INSERT INTO exercise_sets (exercise_id, set_number, repetitions, weight) VALUES " +
                                    "(1, 1, 12, 50), " +
                                    "(1, 2, 10, 55), " +
                                    "(2, 1, 10, 40), " +
                                    "(2, 2, 8, 45), " +
                                    "(3, 1, 8, 0), " +
                                    "(3, 2, 6, 0), " +
                                    "(4, 1, 15, 0), " +
                                    "(4, 2, 12, 0), " +
                                    "(5, 1, 30, 0), " +
                                    "(6, 1, 3, 20);"; // Ejemplo de series para ejercicios
                            conn.createStatement().execute(insertExerciseSets);
                        }
                    }

                    System.out.println("Datos iniciales insertados correctamente.");

                    // System.out.println("Tablas creadas o ya existen.");
                } catch (SQLException e) {
                    throw new DatabaseInitializationException("Error al crear las tablas de la base de datos", e);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error al conectar con la BBDD", e);
        }
    }
}
