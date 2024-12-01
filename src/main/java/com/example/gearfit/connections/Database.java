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
                // Habilitar claves foráneas
                enableForeignKeys(conn);

                // Llamamos a los métodos para crear las tablas
                createUsersTable(conn);
                createRoutinesTable(conn);
                createRoutineDaysTable(conn);
                createExercisesTable(conn);
                createExerciseSetsTable(conn);

                // Crear las nuevas tablas de alimentos
                createUserFoodsTable(conn);
                createDailyFoodsTable(conn);

                // Insertar datos iniciales (como el usuario admin)
                insertInitialData(conn);

                System.out.println("Base de datos inicializada correctamente.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error al conectar con la BBDD", e);
        }
    }

    private static void enableForeignKeys(Connection conn) throws SQLException {
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
    }


    private static void createUsersTable(Connection conn) throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS registered_users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "height INT NOT NULL, " +
                "weight DOUBLE NOT NULL, " +
                "calories INT NOT NULL);";
        conn.createStatement().execute(createUsersTable);
    }

    private static void createRoutinesTable(Connection conn) throws SQLException {
        String createRoutinesTable = "CREATE TABLE IF NOT EXISTS routines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES registered_users(id));";
        conn.createStatement().execute(createRoutinesTable);
    }

    private static void createRoutineDaysTable(Connection conn) throws SQLException {
        String createRoutineDaysTable = "CREATE TABLE IF NOT EXISTS routine_days (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "routine_id INTEGER NOT NULL," +
                "day_of_week TEXT NOT NULL," +
                "FOREIGN KEY (routine_id) REFERENCES routines(id));";
        conn.createStatement().execute(createRoutineDaysTable);
    }

    private static void createExercisesTable(Connection conn) throws SQLException {
        String createExercisesTable = "CREATE TABLE IF NOT EXISTS exercises (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "routine_day_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "tempo TEXT," +               // Ejemplo: '3-1-1-0'
                "rest_time INTEGER," +        // Tiempo de descanso en segundos
                "FOREIGN KEY (routine_day_id) REFERENCES routine_days(id));";
        conn.createStatement().execute(createExercisesTable);
    }

    private static void createExerciseSetsTable(Connection conn) throws SQLException {
        String createExerciseSetsTable = "CREATE TABLE IF NOT EXISTS exercise_sets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "exercise_id INTEGER NOT NULL," +
                "set_number INTEGER NOT NULL," +     // Número de la serie (1, 2, 3, etc.)
                "repetitions INTEGER NOT NULL," +    // Número de repeticiones para esta serie
                "weight DOUBLE," +                   // Peso en kg para esta serie
                "FOREIGN KEY (exercise_id) REFERENCES exercises(id));";
        conn.createStatement().execute(createExerciseSetsTable);
    }

    private static void createUserFoodsTable(Connection conn) throws SQLException {
        String createUserFoodsTable = "CREATE TABLE IF NOT EXISTS user_foods (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "fats_per_100g REAL NOT NULL," +
                "carbs_per_100g REAL NOT NULL," +
                "proteins_per_100g REAL NOT NULL," +
                "kcal_per_100g REAL NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES registered_users(id));";
        conn.createStatement().execute(createUserFoodsTable);
    }

    private static void createDailyFoodsTable(Connection conn) throws SQLException {
        String createDailyFoodsTable = "CREATE TABLE IF NOT EXISTS daily_foods (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "food_id INTEGER NOT NULL," +
                "date DATE NOT NULL," +
                "meal_type TEXT NOT NULL," +
                "grams REAL NOT NULL," +
                "FOREIGN KEY (food_id) REFERENCES user_foods(id));";
        conn.createStatement().execute(createDailyFoodsTable);
    }

    private static void insertInitialData(Connection conn) throws SQLException {
        // Verificar si el usuario "admin" ya existe
        String checkAdminQuery = "SELECT COUNT(*) FROM registered_users WHERE username = 'admin'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkAdminQuery)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Si el usuario no existe, lo insertamos
                String insertAdminUser = "INSERT INTO registered_users (username, email, password, height, weight, calories) VALUES (?, ?, ?, ?, ?, ?)";
                String hashedPassword = BCrypt.hashpw("12345678", BCrypt.gensalt()); // Hashea la contraseña
                int adminUserId; // Para capturar el ID del usuario admin

                try (PreparedStatement pstmt = conn.prepareStatement(insertAdminUser, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, "admin@gmail.com");
                    pstmt.setString(3, hashedPassword);
                    pstmt.setInt(4, 180);
                    pstmt.setDouble(5, 75.0);
                    pstmt.setInt(6, 2000);
                    pstmt.executeUpdate();

                    // Obtener el ID generado del usuario admin
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        adminUserId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del usuario admin.");
                    }
                }

                // Ahora insertamos las rutinas genéricas para el usuario "admin"
                // Crear la primera rutina
                String insertRoutines = "INSERT INTO routines (user_id, name) VALUES (?, ?)";
                String insertRoutineDays = "INSERT INTO routine_days (routine_id, day_of_week) VALUES (?, ?)";
                String insertExercises = "INSERT INTO exercises (routine_day_id, name, tempo, rest_time) VALUES (?, ?, ?, ?)";

                // Rutina 1: Full Body
                int routineId1;
                try (PreparedStatement pstmt = conn.prepareStatement(insertRoutines, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, adminUserId); // ID del usuario admin
                    pstmt.setString(2, "Rutina Full Body");
                    pstmt.executeUpdate();

                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineId1 = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la rutina.");
                    }
                }

                int[] routineDayIds1 = new int[2];
                try (PreparedStatement pstmtDays = conn.prepareStatement(insertRoutineDays, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtDays.setInt(1, routineId1);
                    pstmtDays.setString(2, "Lunes");
                    pstmtDays.executeUpdate();
                    ResultSet generatedKeys = pstmtDays.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineDayIds1[0] = generatedKeys.getInt(1);
                    }

                    pstmtDays.setInt(1, routineId1);
                    pstmtDays.setString(2, "Jueves");
                    pstmtDays.executeUpdate();
                    generatedKeys = pstmtDays.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineDayIds1[1] = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement pstmtExercises = conn.prepareStatement(insertExercises)) {
                    // Ejercicios para el lunes
                    pstmtExercises.setInt(1, routineDayIds1[0]);
                    pstmtExercises.setString(2, "Sentadillas");
                    pstmtExercises.setString(3, "3-1-1-0");
                    pstmtExercises.setInt(4, 60);
                    pstmtExercises.executeUpdate();

                    pstmtExercises.setInt(1, routineDayIds1[0]);
                    pstmtExercises.setString(2, "Press de Banca");
                    pstmtExercises.setString(3, "2-0-2-0");
                    pstmtExercises.setInt(4, 90);
                    pstmtExercises.executeUpdate();

                    // Ejercicios para el jueves
                    pstmtExercises.setInt(1, routineDayIds1[1]);
                    pstmtExercises.setString(2, "Peso Muerto");
                    pstmtExercises.setString(3, "3-0-3-0");
                    pstmtExercises.setInt(4, 120);
                    pstmtExercises.executeUpdate();

                    pstmtExercises.setInt(1, routineDayIds1[1]);
                    pstmtExercises.setString(2, "Dominadas");
                    pstmtExercises.setString(3, "2-0-1-1");
                    pstmtExercises.setInt(4, 60);
                    pstmtExercises.executeUpdate();
                }

                // Rutina 2: Hipertrofia
                int routineId2;
                try (PreparedStatement pstmt = conn.prepareStatement(insertRoutines, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, adminUserId);
                    pstmt.setString(2, "Rutina Hipertrofia");
                    pstmt.executeUpdate();

                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineId2 = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la rutina.");
                    }
                }

                int[] routineDayIds2 = new int[2];
                try (PreparedStatement pstmtDays = conn.prepareStatement(insertRoutineDays, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtDays.setInt(1, routineId2);
                    pstmtDays.setString(2, "Martes");
                    pstmtDays.executeUpdate();
                    ResultSet generatedKeys = pstmtDays.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineDayIds2[0] = generatedKeys.getInt(1);
                    }

                    pstmtDays.setInt(1, routineId2);
                    pstmtDays.setString(2, "Viernes");
                    pstmtDays.executeUpdate();
                    generatedKeys = pstmtDays.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineDayIds2[1] = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement pstmtExercises = conn.prepareStatement(insertExercises)) {
                    // Ejercicios para el martes
                    pstmtExercises.setInt(1, routineDayIds2[0]);
                    pstmtExercises.setString(2, "Press Militar");
                    pstmtExercises.setString(3, "2-1-2-0");
                    pstmtExercises.setInt(4, 90);
                    pstmtExercises.executeUpdate();

                    pstmtExercises.setInt(1, routineDayIds2[0]);
                    pstmtExercises.setString(2, "Curl de Bíceps");
                    pstmtExercises.setString(3, "1-1-1-0");
                    pstmtExercises.setInt(4, 45);
                    pstmtExercises.executeUpdate();

                    // Ejercicios para el viernes
                    pstmtExercises.setInt(1, routineDayIds2[1]);
                    pstmtExercises.setString(2, "Zancadas");
                    pstmtExercises.setString(3, "3-2-1-0");
                    pstmtExercises.setInt(4, 60);
                    pstmtExercises.executeUpdate();

                    pstmtExercises.setInt(1, routineDayIds2[1]);
                    pstmtExercises.setString(2, "Remo con Barra");
                    pstmtExercises.setString(3, "3-1-1-1");
                    pstmtExercises.setInt(4, 75);
                    pstmtExercises.executeUpdate();
                }

                // Rutina 3: Fuerza
                int routineId3;
                try (PreparedStatement pstmt = conn.prepareStatement(insertRoutines, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, adminUserId);
                    pstmt.setString(2, "Rutina Fuerza");
                    pstmt.executeUpdate();

                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineId3 = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la rutina.");
                    }
                }

                int[] routineDayIds3 = new int[2];
                try (PreparedStatement pstmtDays = conn.prepareStatement(insertRoutineDays, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtDays.setInt(1, routineId3);
                    pstmtDays.setString(2, "Miércoles");
                    pstmtDays.executeUpdate();
                    ResultSet generatedKeys = pstmtDays.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineDayIds3[0] = generatedKeys.getInt(1);
                    }

                    pstmtDays.setInt(1, routineId3);
                    pstmtDays.setString(2, "Sábado");
                    pstmtDays.executeUpdate();
                    generatedKeys = pstmtDays.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        routineDayIds3[1] = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement pstmtExercises = conn.prepareStatement(insertExercises)) {
                    // Ejercicios para el miércoles
                    pstmtExercises.setInt(1, routineDayIds3[0]);
                    pstmtExercises.setString(2, "Peso Muerto Sumo");
                    pstmtExercises.setString(3, "2-0-2-0");
                    pstmtExercises.setInt(4, 120);
                    pstmtExercises.executeUpdate();

                    pstmtExercises.setInt(1, routineDayIds3[0]);
                    pstmtExercises.setString(2, "Remo con Mancuerna");
                    pstmtExercises.setString(3, "3-1-2-0");
                    pstmtExercises.setInt(4, 75);
                    pstmtExercises.executeUpdate();

                    // Ejercicios para el sábado
                    pstmtExercises.setInt(1, routineDayIds3[1]);
                    pstmtExercises.setString(2, "Prensa");
                    pstmtExercises.setString(3, "2-1-1-0");
                    pstmtExercises.setInt(4, 90);
                    pstmtExercises.executeUpdate();

                    pstmtExercises.setInt(1, routineDayIds3[1]);
                    pstmtExercises.setString(2, "Fondos en Paralelas");
                    pstmtExercises.setString(3, "3-0-2-0");
                    pstmtExercises.setInt(4, 60);
                    pstmtExercises.executeUpdate();
                }
                System.out.println("3 rutinas creadas exitosamente.");
            }
        }
    }

    private static int getExerciseId(Connection conn, String exerciseName, int routineDayId) throws SQLException {
        String query = "SELECT id FROM exercises WHERE name = ? AND routine_day_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, exerciseName);
            pstmt.setInt(2, routineDayId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No se encontró el ejercicio con el nombre: " + exerciseName);
                }
            }
        }
    }
}