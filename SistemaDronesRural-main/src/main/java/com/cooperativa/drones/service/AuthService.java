package com.cooperativa.drones.service;

import com.cooperativa.drones.dao.UserDao;
import com.cooperativa.drones.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;

public class AuthService {
    public static User login(Connection c, String username, String password) throws Exception {
        var dao = new UserDao(c);
        var u = dao.findByUsername(username);
        if (u == null) return null;
        if (!BCrypt.checkpw(password, u.passwordHash())) return null;
        return u;
    }
}