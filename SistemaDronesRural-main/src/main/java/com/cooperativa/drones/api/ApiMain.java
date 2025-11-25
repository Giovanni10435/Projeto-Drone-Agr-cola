package com.cooperativa.drones.api;

import com.cooperativa.drones.db.Database;
import com.cooperativa.drones.service.SetupService;

import java.sql.Connection;

public class ApiMain {
    public static void main(String[] args) throws Exception {
        try (Connection c = Database.connect()) {
            Database.initSchema(c);
            SetupService.ensureInitialAdmin(c);
            SetupService.ensureSampleData(c);
            var server = new ApiServer(c);
            server.start(8080);
            System.out.println("http://localhost:8080/");
            Thread.currentThread().join();
        }
    }
}