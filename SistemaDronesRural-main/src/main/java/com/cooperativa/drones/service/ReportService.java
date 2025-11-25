package com.cooperativa.drones.service;

import com.cooperativa.drones.dao.MissionDao;
import com.cooperativa.drones.dao.ReadingDao;
import com.cooperativa.drones.model.Mission;
import com.cooperativa.drones.model.Reading;
import com.cooperativa.drones.model.User;
import com.cooperativa.drones.util.Console;

import java.sql.Connection;

public class ReportService {
    private final Connection c;
    private final User user;
    public ReportService(Connection c, User user) { this.c = c; this.user = user; }
    public void menuRelatorios() throws Exception {
        var sc = Console.scanner();
        System.out.println("1 ultimas missoes 2 leituras por missao 0 voltar");
        var op = sc.nextLine().trim();
        if ("1".equals(op)) {
            var dao = new MissionDao(c);
            for (Mission m : dao.list()) System.out.println(m);
        } else if ("2".equals(op)) {
            var missaoId = Long.parseLong(Console.readLine("missao_id: "));
            var rdao = new ReadingDao(c);
            for (Reading r : rdao.listByMission(missaoId)) System.out.println(r);
        }
    }
}