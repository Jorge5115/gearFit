package com.example.gearfit.repositories;

import com.example.gearfit.connections.Database;
import com.example.gearfit.models.Routine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoutineDAO {

    // Método para crear una nueva rutina en la base de datos
    public static boolean createRoutine(Routine routine) {
        String sql = "INSERT INTO routines (user_id, name) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, routine.getUserId());
            pstmt.setString(2, routine.getName());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        routine.setId(generatedKeys.getInt(1)); // Asigna el ID generado a la rutina
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error al crear la rutina: " + e.getMessage());
        }
        return false;
    }

    // Método para obtener todas las rutinas de un usuario específico
    public static List<Routine> getRoutinesByUserId(int userId) {
        List<Routine> routines = new ArrayList<>();
        String sql = "SELECT * FROM routines WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Routine routine = new Routine(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name")
                );
                routines.add(routine);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener rutinas: " + e.getMessage());
        }
        return routines;
    }

    // Método para obtener los días de una rutina específica
    public static List<String> getDaysByRoutineId(int routineId) {
        List<String> days = new ArrayList<>();
        String sql = "SELECT day_of_week FROM routine_days WHERE routine_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routineId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                days.add(rs.getString("day_of_week"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los días de la rutina: " + e.getMessage());
        }
        return days;
    }

    // Método para actualizar el nombre de una rutina
    public static boolean updateRoutine(Routine routine) {
        String sql = "UPDATE routines SET name = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, routine.getName());
            pstmt.setInt(2, routine.getId());
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar la rutina: " + e.getMessage());
        }
        return false;
    }

    // Método para eliminar una rutina por su ID
    public static boolean deleteRoutine(int routineId) {
        String sql = "DELETE FROM routines WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routineId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar la rutina: " + e.getMessage());
        }
        return false;
    }

    // Método para guardar los días de una rutina
    public static boolean saveRoutineDays(int routineId, List<String> days) {
        String sql = "INSERT INTO routine_days (routine_id, day_of_week) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String day : days) {
                pstmt.setInt(1, routineId);
                pstmt.setString(2, day);
                pstmt.addBatch(); // Añadir a un batch para ejecutar múltiples inserciones
            }

            pstmt.executeBatch(); // Ejecutar todas las inserciones
            return true;
        } catch (SQLException e) {
            System.out.println("Error al guardar los días de la rutina: " + e.getMessage());
        }
        return false;
    }

}
