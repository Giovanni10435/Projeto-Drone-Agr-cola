package com.cooperativa.drones.dao;

import com.cooperativa.drones.model.Mission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MissionDao {
    private final Connection c;
    public MissionDao(Connection c) { this.c = c; }
    private String join(Set<String> s) { return String.join(",", s); }
    private Set<String> split(String s) { return new HashSet<>(Arrays.asList(s.split(","))); }
    public Mission create(Long droneId, Long areaId, LocalDateTime inicio, LocalDateTime fim, Set<String> sensores, String status) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("insert into missoes(drone_id,area_id,inicio,fim,sensores,status) values (?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, droneId);
            ps.setLong(2, areaId);
            ps.setObject(3, inicio);
            ps.setObject(4, fim);
            ps.setString(5, join(sensores));
            ps.setString(6, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new Mission(rs.getLong(1), droneId, areaId, inicio, fim, sensores, status);
            }
        }
    }
    public List<Mission> list() throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id,drone_id,area_id,inicio,fim,sensores,status from missoes order by inicio desc")) {
            var list = new ArrayList<Mission>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Mission(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getObject(4, LocalDateTime.class), rs.getObject(5, LocalDateTime.class), split(rs.getString(6)), rs.getString(7)));
            }
            return list;
        }
    }
    public boolean overlaps(Long droneId, LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select count(*) from missoes where drone_id=? and not (fim<=? or inicio>=?)")) {
            ps.setLong(1, droneId);
            ps.setObject(2, inicio);
            ps.setObject(3, fim);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}