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

    // Listado de todas las rutinas insertadas en la bbdd sin ningun usuario especifico
    public static List<Routine> getAllRoutines() {
        List<Routine> routines = new ArrayList<>();

        // Consulta para obtener rutinas disponibles (sin asociar a un usuario específico)
        String query = "SELECT id, name FROM routines WHERE user_id = 1";
        // O ajusta la consulta si hay un valor especial en user_id para rutinas generales (por ejemplo, user_id = 0)

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id"); // ID de la rutina
                String name = resultSet.getString("name"); // Nombre de la rutina

                // Crear un objeto Routine sin asignar un userId
                Routine routine = new Routine(id, 5, name);
                routines.add(routine);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener las rutinas: " + e.getMessage());
        }

        return routines;
    }

    // Añadir rutinas importadas al usuario
    public static int addRoutineToUser (int userId, String routineName) {
        String query = "INSERT INTO routines (user_id, name) VALUES (?, ?)";
        int newRoutineId = -1;

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userId);
            statement.setString(2, routineName);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Rutina añadida al usuario correctamente.");

                // Obtener el ID de la nueva rutina
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newRoutineId = generatedKeys.getInt(1);
                }
            } else {
                System.out.println("No se pudo añadir la rutina al usuario.");
            }

        } catch (SQLException e) {
            System.err.println("Error al añadir rutina al usuario: " + e.getMessage());
        }

        return newRoutineId; // Devuelve el ID de la nueva rutina
    }

    public static int importRoutineForUser(int sourceRoutineId, int targetUserId) {
        String getRoutineNameQuery = "SELECT name FROM routines WHERE id = ?";
        String getDaysQuery = "SELECT id, day_of_week FROM routine_days WHERE routine_id = ?";
        String getExercisesQuery = "SELECT name, tempo, rest_time FROM exercises WHERE routine_day_id = ?";
        int newRoutineId = -1;

        try (Connection conn = Database.connect()) {
            // 1. Obtener el nombre de la rutina original
            String routineName;
            try (PreparedStatement stmt = conn.prepareStatement(getRoutineNameQuery)) {
                stmt.setInt(1, sourceRoutineId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    routineName = rs.getString("name");
                } else {
                    System.err.println("No se encontró la rutina con ID: " + sourceRoutineId);
                    return -1;
                }
            }

            // 2. Añadir la rutina al nuevo usuario
            newRoutineId = addRoutineToUser(targetUserId, routineName);
            if (newRoutineId == -1) {
                System.err.println("No se pudo crear la nueva rutina.");
                return -1;
            }

            // 3. Copiar los días asociados a la rutina
            try (PreparedStatement stmtDays = conn.prepareStatement(getDaysQuery);
                 PreparedStatement insertDays = conn.prepareStatement(
                         "INSERT INTO routine_days (routine_id, day_of_week) VALUES (?, ?)",
                         Statement.RETURN_GENERATED_KEYS
                 )) {
                stmtDays.setInt(1, sourceRoutineId);
                ResultSet rsDays = stmtDays.executeQuery();
                while (rsDays.next()) {
                    int sourceDayId = rsDays.getInt("id");
                    String dayOfWeek = rsDays.getString("day_of_week");

                    // Insertar el día para la nueva rutina
                    insertDays.setInt(1, newRoutineId);
                    insertDays.setString(2, dayOfWeek);
                    insertDays.executeUpdate();

                    // Obtener el nuevo ID del día insertado
                    ResultSet generatedKeys = insertDays.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newDayId = generatedKeys.getInt(1);

                        // 4. Copiar los ejercicios para este día
                        try (PreparedStatement stmtExercises = conn.prepareStatement(getExercisesQuery);
                             PreparedStatement insertExercises = conn.prepareStatement(
                                     "INSERT INTO exercises (routine_day_id, name, tempo, rest_time) VALUES (?, ?, ?, ?)"
                             )) {
                            stmtExercises.setInt(1, sourceDayId);
                            ResultSet rsExercises = stmtExercises.executeQuery();
                            while (rsExercises.next()) {
                                insertExercises.setInt(1, newDayId);
                                insertExercises.setString(2, rsExercises.getString("name"));
                                insertExercises.setString(3, rsExercises.getString("tempo"));
                                insertExercises.setInt(4, rsExercises.getInt("rest_time"));
                                insertExercises.executeUpdate();
                            }
                        }
                    }
                }
            }

            System.out.println("Rutina importada con éxito al usuario ID: " + targetUserId);
        } catch (SQLException e) {
            System.err.println("Error al importar rutina: " + e.getMessage());
            return -1;
        }

        return newRoutineId;
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
            //System.out.println("Consultando días para la rutina ID: " + routineId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                //System.out.println("Recuperado: " + rs.getString("day_of_week"));
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


    // Este es el método que obtiene los ejercicios e incluye sus sets.
    public List<Exercise> getImportedExercisesFromDatabase() {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT * FROM exercises";  // Suponiendo que tienes una tabla 'exercises'

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Crear el ejercicio
                Exercise exercise = new Exercise(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("tempo"),
                        rs.getInt("rest_time"),
                        rs.getInt("routine_id")
                );

                // Obtener los sets de este ejercicio
                List<ExerciseSet> sets = getExerciseSetsByExerciseId(exercise.getId());
                exercise.setSets(sets);  // Asignar los sets al ejercicio

                exercises.add(exercise);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los ejercicios: " + e.getMessage());
        }

        return exercises;
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
