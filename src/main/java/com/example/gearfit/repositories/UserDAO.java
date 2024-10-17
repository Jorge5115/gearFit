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
    public void deleteUser(int id) {
        String sql = "DELETE FROM registered_users WHERE id = ?";

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
        StringBuilder sql = new StringBuilder("UPDATE registered_users SET username = ?, height = ?, weight = ?");

        // Agrega la actualización de la contraseña solo si se proporciona una nueva
        if (password != null && !password.isEmpty()) {
            sql.append(", password = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setDouble(2, usuario.getHeight());
            pstmt.setDouble(3, usuario.getWeight());

            // Si se proporciona una nueva contraseña, la hasheamos y la agregamos
            if (password != null && !password.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                pstmt.setString(4, hashedPassword);
                pstmt.setInt(5, usuario.getId());
            } else {
                // Si no hay nueva contraseña, solo actualizamos los campos de nombre y email
                pstmt.setInt(4, usuario.getId());
            }

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario actualizado con éxito.");
            } else {
                System.out.println("No se encontró el usuario con ID: " + usuario.getId());
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el usuario: " + e.getMessage());
        }
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
                    // Configura otros atributos si es necesario
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra el usuario o la contraseña es incorrecta
    }
}
