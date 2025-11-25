package com.cooperativa.drones.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:file:./data/dronesdb_demo;AUTO_SERVER=TRUE");
    }
    public static void initSchema(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.executeUpdate("create table if not exists areas (id identity primary key, nome varchar(100) not null, tamanho_ha decimal(10,2) not null, localizacao varchar(200) not null, cultivo varchar(100) not null)");
            st.executeUpdate("create table if not exists drones (id identity primary key, codigo varchar(50) not null unique, sensores varchar(200) not null, status varchar(20) not null, bateria int not null)");
            st.executeUpdate("create table if not exists usuarios (id identity primary key, username varchar(50) not null unique, password_hash varchar(100) not null, role varchar(20) not null)");
            st.executeUpdate("create table if not exists missoes (id identity primary key, drone_id bigint not null, area_id bigint not null, inicio timestamp not null, fim timestamp not null, sensores varchar(200) not null, status varchar(20) not null, constraint fk_m_drone foreign key (drone_id) references drones(id), constraint fk_m_area foreign key (area_id) references areas(id))");
            st.executeUpdate("create table if not exists leituras (id identity primary key, missao_id bigint not null, tipo varchar(30) not null, valor decimal(10,2), unidade varchar(20), imagem_path varchar(200), capturado_em timestamp not null, constraint fk_l_m foreign key (missao_id) references missoes(id))");
        }
    }
}