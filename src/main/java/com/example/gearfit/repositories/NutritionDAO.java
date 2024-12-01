package com.example.gearfit.repositories;

import com.example.gearfit.connections.Database;
import com.example.gearfit.models.Food;

import java.sql.*;
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
    public static Food getFoodById(int foodId) {
        String sql = "SELECT * FROM user_foods WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, foodId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Food(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getDouble("fats_per_100g"),
                        rs.getDouble("carbs_per_100g"),
                        rs.getDouble("proteins_per_100g"),
                        rs.getDouble("kcal_per_100g")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el alimento: " + e.getMessage());
        }

        return null;
    }
}
