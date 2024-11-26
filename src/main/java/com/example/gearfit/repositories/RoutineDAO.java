package com.example.gearfit.repositories;

import com.example.gearfit.connections.Database;
import com.example.gearfit.models.Exercise;
import com.example.gearfit.models.ExerciseSet;
import com.example.gearfit.models.Routine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutineDAO {

    // Crear una nueva rutina en la base de datos
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

    // Obtener todas las rutinas de un usuario específico
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

    // Obtener los días de una rutina específica
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

    // Eliminar una rutina por su ID
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

    // Guardar los días de una rutina
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

    // Obtener todos los ejercicios de un dia de la rutina
    public static List<Exercise> getExercisesByRoutineDay(int routineId, String day) {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT e.* FROM exercises e JOIN routine_days rd ON e.routine_day_id = rd.id WHERE rd.routine_id = ? AND rd.day_of_week = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routineId);
            pstmt.setString(2, day);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Exercise exercise = new Exercise(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("tempo"),
                        rs.getInt("rest_time"),
                        rs.getInt("routine_day_id")
                );
                exercises.add(exercise);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ejercicios: " + e.getMessage());
        }
        return exercises;
    }

    // Agregar un nuevo ejercicio a la base de datos
    public static boolean addExercise(Exercise newExercise, String routineDay) {
        String sql = "INSERT INTO exercises (name, tempo, rest_time, routine_day_id) VALUES (?, ?, ?, ?)";

        // Primero, necesitamos obtener el ID del día de la rutina basado en el routineDay
        int routineDayId = RoutineDAO.getRoutineDayId(newExercise.getRoutineId(), routineDay);
        if (routineDayId == -1) {
            System.out.println("Error: No se encontró el ID del día de la rutina para el día: " + routineDay);
            return false;
        }

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newExercise.getName());
            pstmt.setString(2, newExercise.getTempo());
            pstmt.setInt(3, newExercise.getRestTime());
            pstmt.setInt(4, routineDayId); // Usar el ID del día de la rutina

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar el ejercicio: " + e.getMessage());
            return false;
        }
    }

    // Metodo auxiliar para obtener el ID del día de la rutina
    private static int getRoutineDayId(int routineId, String day) {
        String sql = "SELECT id FROM routine_days WHERE routine_id = ? AND day_of_week = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routineId);
            pstmt.setString(2, day);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el ID del día de la rutina: " + e.getMessage());
        }
        return -1; // Retornar -1 si no se encuentra el ID
    }

    // Agregar una serie de un ejercicio
    public static boolean addExerciseSet(ExerciseSet exerciseSet) {
        String sql = "INSERT INTO exercise_sets (exercise_id, set_number, repetitions, weight) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, exerciseSet.getExerciseId());
            pstmt.setInt(2, exerciseSet.getSetNumber());
            pstmt.setInt(3, exerciseSet.getRepetitions());
            pstmt.setDouble(4, exerciseSet.getWeight());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al agregar la serie: " + e.getMessage());
            return false;
        }
    }

    // Actualizar una serie de ejercicio
    public static boolean updateExerciseSet(ExerciseSet exerciseSet) {
        String sql = "UPDATE exercise_sets SET repetitions = ?, weight = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, exerciseSet.getRepetitions());
            pstmt.setDouble(2, exerciseSet.getWeight());
            pstmt.setInt(3, exerciseSet.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar la serie de ejercicio: " + e.getMessage());
            return false;
        }
    }

    // Obtener las series de un ejercicio
    public static List<ExerciseSet> getExerciseSetsByExerciseId(int exerciseId) {
        List<ExerciseSet> sets = new ArrayList<>();
        String sql = "SELECT * FROM exercise_sets WHERE exercise_id = ? ORDER BY set_number";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, exerciseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ExerciseSet set = new ExerciseSet(
                        rs.getInt("id"),
                        rs.getInt("exercise_id"),
                        rs.getInt("set_number"),
                        rs.getInt("repetitions"),
                        rs.getDouble("weight")
                );
                sets.add(set);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las series de ejercicios: " + e.getMessage());
        }
        return sets;
    }

    public static List<Exercise> getExercisesWithSetsByRoutineDay(int routineId, String routineDay) {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT e.id AS exercise_id, e.name AS exercise_name, e.tempo, e.rest_time, " +
                "es.id AS set_id, es.set_number, es.repetitions, es.weight " +
                "FROM exercises e " +
                "LEFT JOIN exercise_sets es ON e.id = es.exercise_id " +
                "WHERE e.routine_day_id = (SELECT id FROM routine_days WHERE routine_id = ? AND day_of_week = ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routineId);
            pstmt.setString(2, routineDay);
            ResultSet rs = pstmt.executeQuery();

            // Mapeo para evitar duplicados de ejercicios
            Map<Integer, Exercise> exerciseMap = new HashMap<>();

            while (rs.next()) {
                int exerciseId = rs.getInt("exercise_id");
                Exercise exercise = exerciseMap.getOrDefault(exerciseId, new Exercise(
                        exerciseId,
                        rs.getString("exercise_name"),
                        rs.getString("tempo"),
                        rs.getInt("rest_time"),
                        routineId
                ));

                // Crear la serie si existen datos en el resultado
                int setId = rs.getInt("set_id");
                if (setId > 0) {
                    ExerciseSet set = new ExerciseSet(
                            setId,
                            exerciseId,
                            rs.getInt("set_number"),
                            rs.getInt("repetitions"),
                            rs.getDouble("weight")
                    );
                    exercise.addSet(set); // Agregar la serie al ejercicio
                }

                // Agregar el ejercicio al mapa (solo la primera vez)
                exerciseMap.putIfAbsent(exerciseId, exercise);
            }

            exercises.addAll(exerciseMap.values()); // Convertir el mapa en lista
        } catch (SQLException e) {
            System.out.println("Error al obtener ejercicios con series: " + e.getMessage());
        }
        return exercises;
    }


    public static boolean deleteExercise(int exerciseId) {
        // Primero, eliminamos las series asociadas al ejercicio
        if (!deleteExerciseSetsByExerciseId(exerciseId)) {
            System.out.println("Error al eliminar las series asociadas al ejercicio con ID: " + exerciseId);
            return false;
        }

        String sql = "DELETE FROM exercises WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, exerciseId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Devuelve true si se eliminó el ejercicio
        } catch (SQLException e) {
            System.out.println("Error al eliminar el ejercicio: " + e.getMessage());
        }
        return false;
    }

    // Eliminar las series asociadas a un ejercicio
    private static boolean deleteExerciseSetsByExerciseId(int exerciseId) {
        String sql = "DELETE FROM exercise_sets WHERE exercise_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, exerciseId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows >= 0; // Devuelve true si se eliminaron las series (0 si no había series)
        } catch (SQLException e) {
            System.out.println("Error al eliminar las series del ejercicio: " + e.getMessage());
        }
        return false;
    }
}
