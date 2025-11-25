package com.cooperativa.drones.dao;

import com.cooperativa.drones.model.Area;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDao {
    private final Connection c;
    public AreaDao(Connection c) { this.c = c; }
    public Area create(String nome, BigDecimal tamanho, String localizacao, String cultivo) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("insert into areas(nome,tamanho_ha,localizacao,cultivo) values (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setBigDecimal(2, tamanho);
            ps.setString(3, localizacao);
            ps.setString(4, cultivo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new Area(rs.getLong(1), nome, tamanho, localizacao, cultivo);
            }
        }
    }
    public List<Area> list() throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id,nome,tamanho_ha,localizacao,cultivo from areas order by id")) {
            var list = new ArrayList<Area>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Area(rs.getLong(1), rs.getString(2), rs.getBigDecimal(3), rs.getString(4), rs.getString(5)));
            }
            return list;
        }
    }
}