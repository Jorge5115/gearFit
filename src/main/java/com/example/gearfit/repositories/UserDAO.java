package com.example.gearfit.repositories;

import com.example.gearfit.connections.Database;
import com.example.gearfit.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Agregar un usuario
    public void addUser(User user, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO registered_users(username, email, password, height, weight) VALUES(?, ?, ?,?,?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword); // Hasheamos la contraseña
            pstmt.setDouble(4, user.getHeight());
            pstmt.setDouble(5, user.getWeight());
            pstmt.executeUpdate();
            System.out.println("Usuario agregado con éxito.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Consultar todos los usuarios
    public List<User> queryUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getDouble("height"),
                        rs.getString("weight")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    // Eliminar un usuario por ID
    public void deleteUser(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario eliminado con éxito.");
            } else {
                System.out.println("No se encontró el usuario con ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Actualizar un usuario
    public void updateUser(User usuario, String password) {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ? WHERE id = ?";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, hashedPassword); // Hasheamos la nueva contraseña
            pstmt.setInt(4, usuario.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario actualizado con éxito.");
            } else {
                System.out.println("No se encontró el usuario con ID: " + usuario.getId());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
    // Buscar un usuario por ID
    public User findUserById(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        User usuario = null;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                usuario = new User(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email")
                );
            } else {
                System.out.println("No se encontró el usuario con ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return usuario;
    }*/

    public boolean verificarContrasena(String email, String password) {
        String sql = "SELECT password FROM registered_users WHERE email = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, hashedPassword); // Verificamos si la contraseña ingresada coincide con la hasheada
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false; // El usuario no existe o la contraseña no coincide
    }
}
