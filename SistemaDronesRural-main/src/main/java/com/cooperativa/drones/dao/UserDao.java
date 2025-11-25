package com.cooperativa.drones.dao;

import com.cooperativa.drones.model.Role;
import com.cooperativa.drones.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final Connection c;
    public UserDao(Connection c) { this.c = c; }
    public User create(String username, String passwordHash, Role role) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("insert into usuarios(username,password_hash,role) values (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new User(rs.getLong(1), username, passwordHash, role);
            }
        }
    }
    public User findByUsername(String username) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id,username,password_hash,role from usuarios where username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new User(rs.getLong(1), rs.getString(2), rs.getString(3), Role.valueOf(rs.getString(4)));
                return null;
            }
        }
    }
    public List<User> list() throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id,username,password_hash,role from usuarios order by id")) {
            var list = new ArrayList<User>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new User(rs.getLong(1), rs.getString(2), rs.getString(3), Role.valueOf(rs.getString(4))));
            }
            return list;
        }
    }
}