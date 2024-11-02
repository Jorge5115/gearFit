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
        String sql = "SELECT * FROM registered_users";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getDouble("height"),
                        rs.getDouble("weight")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    // Eliminar un usuario por ID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM registered_users WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Devuelve true si se eliminó al menos una fila

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Devuelve false en caso de error
        }
    }

    // Actualizar un usuario
    public void updateUser(User user, String hashedPassword) {
        String sql = "UPDATE registered_users SET username = ?, height = ?, weight = ?, password = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setDouble(2, user.getHeight());
            pstmt.setDouble(3, user.getWeight());

            // Verifica si `hashedPassword` es nulo o vacío
            if (hashedPassword != null && !hashedPassword.isEmpty()) {
                pstmt.setString(4, hashedPassword);  // Actualizar con el nuevo hash
            } else {
                pstmt.setString(4, getPasswordFromDB(user.getId())); // Obtener el hash actual de la BD si no hay cambio de contraseña
            }

            pstmt.setInt(5, user.getId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Filas actualizadas: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPasswordFromDB(int userId) {
        String sql = "SELECT password FROM registered_users WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    // Buscar un usuario por ID
    public User findUserById(int id) {
        String sql = "SELECT * FROM registered_users WHERE id = ?";
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

    public boolean verifyPassword(String email, String password) {
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

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM registered_users WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.getInt(1) > 0; // Si el conteo es mayor a 0, el nombre de usuario existe
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false; // Error en la consulta o el usuario no existe
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM registered_users WHERE email = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            return rs.getInt(1) > 0; // Si el conteo es mayor a 0, el correo electrónico existe
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false; // Error en la consulta o el correo no existe
    }

    public User getUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM registered_users WHERE email = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                // Compara la contraseña hasheada
                if (BCrypt.checkpw(password, dbPassword)) {
                    // Si la contraseña es correcta, crea y devuelve un nuevo objeto User
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setHeight(rs.getDouble("height"));
                    user.setWeight(rs.getDouble("weight"));
                    return user;
                } else {
                    System.out.println("Contraseña incorrecta.");
                }
            } else {
                System.out.println("No se encontró el usuario con el email: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra el usuario o la contraseña es incorrecta
    }
}
