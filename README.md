# Projeto-Drone-Agr-cola
# Sistema de Controle de Drones Rural

## Visão Geral
- Projeto Java 17 com H2, console interativo e API HTTP simples.
- Tema: monitoramento de plantações com drones, missões, leituras e relatórios.
- Segurança: autenticação com `BCrypt`, controle de acesso por `role`, prepared statements, validações.

## Requisitos
- JDK 17
- Apache Maven 3.9+
- Postman (opcional, para testar a API)

## Instalação (Windows)
- Baixe e instale JDK 17 (Temurin/Adoptium): `https://adoptium.net/temurin/releases/?version=17`
- Instale Maven: `https://maven.apache.org/download.cgi`

## Configuração de Ambiente
- Variáveis de sistema:
  - `JAVA_HOME` → `C:\Program Files\Java\jdk-17` (sem `\bin`)
  - `Path` → inclua `%JAVA_HOME%\bin` e o `bin` do Maven
- Verifique:
  - `java -version` (deve mostrar 17)
  - `mvn -v` (deve apontar para JDK 17)

## Build
- No diretório do projeto: `mvn -q -DskipTests package`
- Saída: artefatos em `target/`

## Execução
- API HTTP (padrão): `mvn -q -DskipTests exec:java`
  - Inicia em `http://localhost:8080/`
- Console interativo: `mvn -q -DskipTests exec:java -Dexec.mainClass=com.cooperativa.drones.app.Main`

## Banco de Dados
- H2 arquivo: `data/dronesdb_demo` (gitignored)
- Esquema criado automaticamente:
  - `areas`, `drones`, `usuarios`, `missoes`, `leituras` (`src/main/java/com/cooperativa/drones/db/Database.java:12-19`)
- Semeadura inicial: 1 área e 1 drone (`src/main/java/com/cooperativa/drones/service/SetupService.java:29-34`)
- Usuários iniciais: `admin/admin` e `operador/operador` (`src/main/java/com/cooperativa/drones/service/SetupService.java:19-28`)

## API HTTP (Postman)
- Base: `http://localhost:8080`
- Login
  - `POST /login`
  - Body (JSON): `{"username":"admin","password":"admin"}`
- Listar áreas
  - `GET /areas`
- Listar drones
  - `GET /drones`
- Listar missões
  - `GET /missions`
- Leituras por missão
  - `GET /missions/{id}/readings`
- Agendar missão (`src/main/java/com/cooperativa/drones/api/ApiServer.java:40-73`)
  - `POST /missions`
  - Body (JSON):
```
{
  "droneId": 1,
  "areaId": 1,
  "inicio": "2025-11-23T10:00",
  "duracaoMin": 30,
  "sensores": ["temp", "umidade"]
}
```
  - Regras:
    - Sem sobreposição de agenda por drone (`missoes`), bateria ≥ 30%, sensores solicitados disponíveis.

## Console (Fluxo rápido)
- Login: `admin/admin`
- Menus ADMIN: `areas`, `drones`, `usuarios`, `missoes`, `leituras`, `relatorios` (`src/main/java/com/cooperativa/drones/app/Main.java:28-44`)
- Agendar missão: `missoes` → `2 agendar` (`src/main/java/com/cooperativa/drones/service/MissionService.java:23-49`)
- Leituras: `leituras` → `1 adicionar` (`src/main/java/com/cooperativa/drones/service/MissionService.java:51-69`)

## Segurança
- Autenticação com senha `BCrypt` (`src/main/java/com/cooperativa/drones/service/AuthService.java:10-16`)
- Controle de acesso por `Role` (`ADMIN`, `OPERADOR`) em menus e serviços.
- Prepared statements nos DAOs (CRUD seguro) (`src/main/java/com/cooperativa/drones/dao/*`)
- Validações em modelos (entradas obrigatórias e faixas válidas) (`src/main/java/com/cooperativa/drones/model/*`)

## Problemas comuns
- "JAVA_HOME not defined correctly": `JAVA_HOME` deve apontar para a raiz do JDK (sem `\bin`); inclua `%JAVA_HOME%\bin` no `Path`.
- Portas ocupadas: troque a porta no `ApiMain` ao iniciar o servidor.
- H2 bloqueado: pare processos usando o banco; os arquivos ficam em `data/`.

## Licença
- Uso acadêmico e demonstração.
