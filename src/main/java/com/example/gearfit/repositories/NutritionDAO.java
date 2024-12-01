package com.example.gearfit.repositories;

import com.example.gearfit.connections.Database;
import com.example.gearfit.models.DailyFood;
import com.example.gearfit.models.Food;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class NutritionDAO {

    // Agregar un nuevo alimento a la base de datos
    public static boolean addFood(Food food) {
        String sql = "INSERT INTO user_foods (user_id, name, fats_per_100g, carbs_per_100g, proteins_per_100g, kcal_per_100g) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, food.getUserId());
            pstmt.setString(2, food.getName());
            pstmt.setDouble(3, food.getFats());
            pstmt.setDouble(4, food.getCarbs());
            pstmt.setDouble(5, food.getProtein());
            pstmt.setDouble(6, food.getCalories());

            int affectedRows = pstmt.executeUpdate();

            // Si el alimento se agregó correctamente, asignar el ID generado
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        food.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al agregar el alimento: " + e.getMessage());
        }

        return false;
    }

    // Obtener todos los alimentos de un usuario específico
    public static List<Food> getFoodsByUserId(int userId) {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM user_foods WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Food food = new Food(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getDouble("fats_per_100g"),
                        rs.getDouble("carbs_per_100g"),
                        rs.getDouble("proteins_per_100g"),
                        rs.getDouble("kcal_per_100g")
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los alimentos: " + e.getMessage());
        }

        return foods;
    }

    // Actualizar un alimento existente
    public static boolean updateFood(Food food) {
        String sql = "UPDATE user_foods SET name = ?, fats_per_100g = ?, carbs_per_100g = ?, proteins_per_100g = ?, kcal_per_100g = ? " +
                "WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, food.getName());
            pstmt.setDouble(2, food.getFats());
            pstmt.setDouble(3, food.getCarbs());
            pstmt.setDouble(4, food.getProtein());
            pstmt.setDouble(5, food.getCalories());
            pstmt.setInt(6, food.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar el alimento: " + e.getMessage());
        }

        return false;
    }

    // Eliminar un alimento por su ID
    public static boolean deleteFood(int foodId) {
        String sql = "DELETE FROM user_foods WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, foodId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar el alimento: " + e.getMessage());
        }

        return false;
    }

    // Obtener un alimento por su ID
    public static Food getUserFoodById(int foodId) {
        Food userFood = null;
        String sql = "SELECT name, fats_per_100g, carbs_per_100g, proteins_per_100g, kcal_per_100g FROM user_foods WHERE id = ?"; // Cambia 'user_foods' al nombre correcto de tu tabla

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double fats = rs.getDouble("fats_per_100g"); // Asegúrate de que este nombre sea correcto
                double carbs = rs.getDouble("carbs_per_100g");
                double proteins = rs.getDouble("proteins_per_100g");
                double calories = rs.getDouble("kcal_per_100g");

                userFood = new Food(foodId, name, fats, carbs, proteins, calories); // Asegúrate de tener un constructor adecuado
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el alimento del usuario: " + e.getMessage());
        }

        return userFood;
    }


    public static boolean addDailyFood(DailyFood dailyFood) {
        String sql = "INSERT INTO daily_foods (food_id, date, meal_type, grams) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dailyFood.getFoodId());
            pstmt.setDate(2, Date.valueOf(dailyFood.getDate()));
            pstmt.setString(3, dailyFood.getMealType());
            pstmt.setDouble(4, dailyFood.getGrams());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar el registro de comida diaria: " + e.getMessage());
        }

        return false;
    }

    public static List<DailyFood> getDailyFoodsByUserIdAndDate(int userId, LocalDate date) {
        List<DailyFood> dailyFoods = new ArrayList<>();
        String sql = "SELECT df.* FROM daily_foods df " +
                "JOIN user_foods uf ON df.food_id = uf.id " +
                "WHERE uf.user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                long millis = rs.getLong("date"); // Obtener la fecha en milisegundos
                LocalDate foodDate = convertMillisToLocalDate(millis); // Convertir a LocalDate

                // Solo agregar alimentos que coincidan con la fecha actual
                if (foodDate.isEqual(date)) {
                    DailyFood dailyFood = new DailyFood(
                            rs.getInt("id"),
                            rs.getInt("food_id"),
                            foodDate,
                            rs.getString("meal_type"),
                            rs.getDouble("grams")
                    );
                    dailyFoods.add(dailyFood);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los alimentos diarios: " + e.getMessage());
        }

        return dailyFoods;
    }

    public static LocalDate convertMillisToLocalDate(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
