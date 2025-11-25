package com.cooperativa.drones.service;

import com.cooperativa.drones.dao.AreaDao;
import com.cooperativa.drones.dao.DroneDao;
import com.cooperativa.drones.dao.MissionDao;
import com.cooperativa.drones.dao.ReadingDao;
import com.cooperativa.drones.model.Drone;
import com.cooperativa.drones.model.Mission;
import com.cooperativa.drones.model.Reading;
import com.cooperativa.drones.model.Role;
import com.cooperativa.drones.model.User;
import com.cooperativa.drones.util.Console;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class MissionService {
    private final Connection c;
    private final User user;
    public MissionService(Connection c, User user) { this.c = c; this.user = user; }
    public void menuMissoes() throws Exception {
        var dao = new MissionDao(c);
        var sc = Console.scanner();
        System.out.println("1 listar 2 agendar 0 voltar");
        var op = sc.nextLine().trim();
        if ("1".equals(op)) {
            for (Mission m : dao.list()) System.out.println(m);
        } else if ("2".equals(op)) {
            if (user.role() == Role.OPERADOR || user.role() == Role.ADMIN) {
                var droneId = Long.parseLong(Console.readLine("drone_id: "));
                var areaId = Long.parseLong(Console.readLine("area_id: "));
                var inicio = LocalDateTime.parse(Console.readLine("inicio(YYYY-MM-DDThh:mm): "));
                var durMin = Integer.parseInt(Console.readLine("duracao_min: "));
                var fim = inicio.plusMinutes(durMin);
                var sensores = new HashSet<String>(Set.of(Console.readLine("sensores(csv): ").split(",")));
                if (dao.overlaps(droneId, inicio, fim)) {
                    System.out.println("conflito de agenda neste drone");
                    return;
                }
                var droneDao = new DroneDao(c);
                Drone d = droneDao.findById(droneId);
                if (d == null) { System.out.println("drone inexistente"); return; }
                if (d.bateria() < 30) { System.out.println("bateria insuficiente"); return; }
                if (!d.sensores().containsAll(sensores)) { System.out.println("sensores indisponiveis"); return; }
                System.out.println(dao.create(droneId, areaId, inicio, fim, sensores, "AGENDADA"));
            }
        }
    }
    public void menuLeituras() throws Exception {
        var dao = new ReadingDao(c);
        var sc = Console.scanner();
        System.out.println("1 adicionar 2 listar por missao 0 voltar");
        var op = sc.nextLine().trim();
        if ("1".equals(op)) {
            var missaoId = Long.parseLong(Console.readLine("missao_id: "));
            var tipo = Console.readLine("tipo: ");
            var valorStr = Console.readLine("valor(opcional): ");
            Double valor = valorStr.isBlank() ? null : Double.parseDouble(valorStr);
            var un = Console.readLine("unidade(opcional): ");
            var img = Console.readLine("imagem_path(opcional): ");
            var ts = LocalDateTime.parse(Console.readLine("capturado_em(YYYY-MM-DDThh:mm): "));
            System.out.println(dao.create(missaoId, tipo, valor, un, img, ts));
        } else if ("2".equals(op)) {
            var missaoId = Long.parseLong(Console.readLine("missao_id: "));
            for (Reading r : dao.listByMission(missaoId)) System.out.println(r);
        }
    }
}