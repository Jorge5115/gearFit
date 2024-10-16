package com.example.gearfit.connections;

import com.example.gearfit.models.User;

public class SessionManager {
    //ALMACENAR Y RECUPERAR INFORMACION DEL USUARIO AUTENTICADO
    private static User currentUser;

    // Método para establecer el usuario actualmente autenticado
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Método para obtener el usuario actualmente autenticado
    public static User getCurrentUser() {
        return currentUser;
    }

    // Método para cerrar sesión
    public static void logOut() {
        currentUser = null; // Limpiar la sesión
    }
}

