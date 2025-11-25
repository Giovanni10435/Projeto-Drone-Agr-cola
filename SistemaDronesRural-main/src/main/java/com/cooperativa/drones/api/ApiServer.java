package com.cooperativa.drones.api;

import com.cooperativa.drones.dao.*;
import com.cooperativa.drones.model.*;
import com.google.gson.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;

public class ApiServer {
    private final Connection c;
    private final Gson gson;
    public ApiServer(Connection c) {
        this.c = c;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString()))
                .create();
    }
    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/areas", wrap(this::areas));
        server.createContext("/drones", wrap(this::drones));
        server.createContext("/missions", wrap(this::missions));
        server.createContext("/missions/", wrap(this::missionSub));
        server.createContext("/login", wrap(this::login));
        server.setExecutor(null);
        server.start();
    }
    private HttpHandler wrap(Handler h) { return ex -> { try { h.handle(ex); } catch (Exception e) { respond(ex, 500, Map.of("error", e.getMessage())); } }; }
    private interface Handler { void handle(HttpExchange ex) throws Exception; }
    private void areas(HttpExchange ex) throws Exception {
        if ("GET".equals(ex.getRequestMethod())) {
            var dao = new AreaDao(c);
            respond(ex, 200, dao.list());
        } else respond(ex, 405, Map.of());
    }
    private void drones(HttpExchange ex) throws Exception {
        if ("GET".equals(ex.getRequestMethod())) {
            var dao = new DroneDao(c);
            respond(ex, 200, dao.list());
        } else respond(ex, 405, Map.of());
    }
    private void missions(HttpExchange ex) throws Exception {
        var dao = new MissionDao(c);
        if ("GET".equals(ex.getRequestMethod())) {
            respond(ex, 200, dao.list());
        } else if ("POST".equals(ex.getRequestMethod())) {
            var body = readJson(ex);
            var droneId = body.get("droneId").getAsLong();
            var areaId = body.get("areaId").getAsLong();
            var inicio = LocalDateTime.parse(body.get("inicio").getAsString());
            var dur = body.get("duracaoMin").getAsInt();
            var fim = inicio.plusMinutes(dur);
            var sensores = new HashSet<String>();
            body.get("sensores").getAsJsonArray().forEach(e -> sensores.add(e.getAsString()));
            if (dao.overlaps(droneId, inicio, fim)) { respond(ex, 409, Map.of("error","conflito")); return; }
            var ddao = new DroneDao(c);
            var d = ddao.findById(droneId);
            if (d == null) { respond(ex, 404, Map.of("error","drone")); return; }
            if (d.bateria() < 30) { respond(ex, 400, Map.of("error","bateria")); return; }
            if (!d.sensores().containsAll(sensores)) { respond(ex, 400, Map.of("error","sensores")); return; }
            respond(ex, 201, dao.create(droneId, areaId, inicio, fim, sensores, "AGENDADA"));
        } else respond(ex, 405, Map.of());
    }
    private void missionSub(HttpExchange ex) throws Exception {
        var path = ex.getRequestURI().getPath();
        var parts = path.split("/");
        if (parts.length >= 4 && "readings".equals(parts[3]) && "GET".equals(ex.getRequestMethod())) {
            var id = Long.parseLong(parts[2]);
            var rdao = new ReadingDao(c);
            respond(ex, 200, rdao.listByMission(id));
        } else respond(ex, 404, Map.of());
    }
    private void login(HttpExchange ex) throws Exception {
        if (!"POST".equals(ex.getRequestMethod())) { respond(ex, 405, Map.of()); return; }
        var body = readJson(ex);
        var username = body.get("username").getAsString();
        var password = body.get("password").getAsString();
        var udao = new UserDao(c);
        var u = udao.findByUsername(username);
        if (u == null) { respond(ex, 401, Map.of("ok",false)); return; }
        if (!org.mindrot.jbcrypt.BCrypt.checkpw(password, u.passwordHash())) { respond(ex, 401, Map.of("ok",false)); return; }
        respond(ex, 200, Map.of("ok",true,"role",u.role().name()));
    }
    private JsonObject readJson(HttpExchange ex) throws IOException {
        try (var r = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(r).getAsJsonObject();
        }
    }
    private void respond(HttpExchange ex, int status, Object body) throws IOException {
        var json = gson.toJson(body);
        var bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}