package com.cooperativa.drones.service;

import com.cooperativa.drones.dao.AreaDao;
import com.cooperativa.drones.dao.DroneDao;
import com.cooperativa.drones.dao.UserDao;
import com.cooperativa.drones.model.Area;
import com.cooperativa.drones.model.Drone;
import com.cooperativa.drones.model.Role;
import com.cooperativa.drones.model.User;
import com.cooperativa.drones.util.Console;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

public class SetupService {
    public static void ensureInitialAdmin(Connection c) throws Exception {
        var dao = new UserDao(c);
        if (dao.list().isEmpty()) {
            var adminHash = BCrypt.hashpw("admin", BCrypt.gensalt());
            dao.create("admin", adminHash, Role.ADMIN);
            var opHash = BCrypt.hashpw("operador", BCrypt.gensalt());
            dao.create("operador", opHash, Role.OPERADOR);
        }
    }
    public static void ensureSampleData(Connection c) throws Exception {
        var areaDao = new AreaDao(c);
        var droneDao = new DroneDao(c);
        if (areaDao.list().isEmpty()) areaDao.create("Area A", new BigDecimal("10.0"), "Fazenda Norte", "Soja");
        if (droneDao.list().isEmpty()) droneDao.create("DR-001", new HashSet<>(Set.of("temp","umidade","imagem")), "OK", 80);
    }
    public static void menuAreas(Connection c) throws Exception {
        var dao = new AreaDao(c);
        var sc = Console.scanner();
        System.out.println("1 listar 2 criar 0 voltar");
        var op = sc.nextLine().trim();
        if ("1".equals(op)) {
            for (Area a : dao.list()) System.out.println(a);
        } else if ("2".equals(op)) {
            var nome = Console.readLine("nome: ");
            var tam = new BigDecimal(Console.readLine("tamanho_ha: "));
            var loc = Console.readLine("localizacao: ");
            var cul = Console.readLine("cultivo: ");
            System.out.println(dao.create(nome, tam, loc, cul));
        }
    }
    public static void menuDrones(Connection c) throws Exception {
        var dao = new DroneDao(c);
        var sc = Console.scanner();
        System.out.println("1 listar 2 criar 0 voltar");
        var op = sc.nextLine().trim();
        if ("1".equals(op)) {
            for (Drone d : dao.list()) System.out.println(d);
        } else if ("2".equals(op)) {
            var codigo = Console.readLine("codigo: ");
            var sensores = new HashSet<String>(Set.of(Console.readLine("sensores(csv): ").split(",")));
            var status = Console.readLine("status: ");
            var bat = Integer.parseInt(Console.readLine("bateria(0-100): "));
            System.out.println(dao.create(codigo, sensores, status, bat));
        }
    }
    public static void menuUsuarios(Connection c) throws Exception {
        var dao = new UserDao(c);
        var sc = Console.scanner();
        System.out.println("1 listar 2 criar 0 voltar");
        var op = sc.nextLine().trim();
        if ("1".equals(op)) {
            for (User u : dao.list()) System.out.println(u.username()+" "+u.role());
        } else if ("2".equals(op)) {
            var username = Console.readLine("usuario: ");
            var pass = Console.readPassword();
            var role = Role.valueOf(Console.readLine("role(ADMIN/OPERADOR): ").toUpperCase());
            var hash = BCrypt.hashpw(pass, BCrypt.gensalt());
            System.out.println(dao.create(username, hash, role));
        }
    }
}