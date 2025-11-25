package com.cooperativa.drones.dao;

import com.cooperativa.drones.model.Reading;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReadingDao {
    private final Connection c;
    public ReadingDao(Connection c) { this.c = c; }
    public Reading create(Long missaoId, String tipo, Double valor, String unidade, String imagemPath, LocalDateTime capturadoEm) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("insert into leituras(missao_id,tipo,valor,unidade,imagem_path,capturado_em) values (?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, missaoId);
            ps.setString(2, tipo);
            if (valor == null) ps.setNull(3, java.sql.Types.DECIMAL); else ps.setDouble(3, valor);
            ps.setString(4, unidade);
            ps.setString(5, imagemPath);
            ps.setObject(6, capturadoEm);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new Reading(rs.getLong(1), missaoId, tipo, valor, unidade, imagemPath, capturadoEm);
            }
        }
    }
    public List<Reading> listByMission(Long missaoId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id,missao_id,tipo,valor,unidade,imagem_path,capturado_em from leituras where missao_id=? order by capturado_em desc")) {
            ps.setLong(1, missaoId);
            var list = new ArrayList<Reading>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Reading(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getObject(4) == null ? null : rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getObject(7, LocalDateTime.class)));
            }
            return list;
        }
    }
}