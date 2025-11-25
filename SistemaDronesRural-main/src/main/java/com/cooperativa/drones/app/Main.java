package com.cooperativa.drones.app;

import com.cooperativa.drones.db.Database;
import com.cooperativa.drones.model.Role;
import com.cooperativa.drones.model.User;
import com.cooperativa.drones.service.AuthService;
import com.cooperativa.drones.service.MissionService;
import com.cooperativa.drones.service.ReportService;
import com.cooperativa.drones.service.SetupService;
import com.cooperativa.drones.util.Console;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection c = Database.connect()) {
            Database.initSchema(c);
            SetupService.ensureInitialAdmin(c);
            SetupService.ensureSampleData(c);
            var scanner = Console.scanner();
            User user = null;
            while (user == null) {
                System.out.print("usuario: ");
                var u = scanner.nextLine().trim();
                System.out.print("senha: ");
                var p = Console.readPassword();
                user = AuthService.login(c, u, p);
                if (user == null) System.out.println("credenciais invalidas");
            }
            var missionService = new MissionService(c, user);
            var reportService = new ReportService(c, user);
            boolean running = true;
            while (running) {
                if (user.role() == Role.ADMIN) {
                    System.out.println("1 areas 2 drones 3 usuarios 4 missoes 5 leituras 6 relatorios 0 sair");
                    var op = scanner.nextLine().trim();
                    switch (op) {
                        case "1" -> SetupService.menuAreas(c);
                        case "2" -> SetupService.menuDrones(c);
                        case "3" -> SetupService.menuUsuarios(c);
                        case "4" -> missionService.menuMissoes();
                        case "5" -> missionService.menuLeituras();
                        case "6" -> reportService.menuRelatorios();
                        case "0" -> running = false;
                        default -> System.out.println("opcao invalida");
                    }
                } else {
                    System.out.println("1 missoes 2 leituras 3 relatorios 0 sair");
                    var op = scanner.nextLine().trim();
                    switch (op) {
                        case "1" -> missionService.menuMissoes();
                        case "2" -> missionService.menuLeituras();
                        case "3" -> reportService.menuRelatorios();
                        case "0" -> running = false;
                        default -> System.out.println("opcao invalida");
                    }
                }
            }
        }
    }
}