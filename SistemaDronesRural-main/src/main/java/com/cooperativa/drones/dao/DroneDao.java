package com.cooperativa.drones.dao;

import com.cooperativa.drones.model.Drone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DroneDao {
    private final Connection c;
    public DroneDao(Connection c) { this.c = c; }
    private String join(Set<String> s) { return String.join(",", s); }
    private Set<String> split(String s) { return new HashSet<>(Arrays.asList(s.split(","))); }
    public Drone create(String codigo, Set<String> sensores, String status, int bateria) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("insert into drones(codigo,sensores,status,bateria) values (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, codigo);
            ps.setString(2, join(sensores));
            ps.setString(3, status);
            ps.setInt(4, bateria);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new Drone(rs.getLong(1), codigo, sensores, status, bateria);
            }
        }
    }
    public List<Drone> list() throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id,codigo,sensores,status,bateria from drones order by id")) {
            var list = new ArrayList<Drone>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Drone(rs.getLong(1), rs.getString(2), split(rs.getString(3)), rs.getString(4), rs.getInt(5)));
            }
            return list;
        }
    }
    public Drone findById(long id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id,codigo,sensores,status,bateria from drones where id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Drone(rs.getLong(1), rs.getString(2), split(rs.getString(3)), rs.getString(4), rs.getInt(5));
                return null;
            }
        }
    }
}