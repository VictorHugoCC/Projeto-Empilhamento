package org.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static final Gson gson = new Gson();
    private static final GameBoard gameBoard = new GameBoard();

    static class VictoryResponse {
        boolean isVictory;
        String message;
        int score;
        int moves;

        public VictoryResponse(boolean isVictory, String message, int score, int moves) {
            this.isVictory = isVictory;
            this.message = message;
            this.score = score;
            this.moves = moves;
        }
    }

    public static void main(String[] args) {
        port(4567);

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
        });

        post("/game/new", (req, res) -> {
            gameBoard.resetGame();
            Map<String, Object> gameState = gameBoard.getGameStateData();
            if (!gameState.containsKey("message") || gameState.get("message") == null
                    || ((String) gameState.get("message")).isEmpty()) {
                gameState.put("message", "Novo jogo iniciado com sucesso.");
            }
            return gson.toJson(gameState);
        });

        get("/game/state", (req, res) -> {
            Map<String, Object> gameState = gameBoard.getGameStateData();
            if (!gameState.containsKey("message") || gameState.get("message") == null
                    || ((String) gameState.get("message")).isEmpty()) {
                gameState.put("message", "Estado atual do jogo carregado.");
            }
            return gson.toJson(gameState);
        });

        post("/game/move", (req, res) -> {
            try {
                String payload = req.body();
                MoveRequest moveRequest = gson.fromJson(payload, MoveRequest.class);

                if (moveRequest == null || !moveRequest.isValid()) {
                    res.status(400);
                    return gson.toJson(Map.of("error",
                            "Requisição de movimento inválida. Informe: fromTube e toTube (inteiros)."));
                }

                String moveResultMessage = gameBoard.makeMove(moveRequest.fromTube, moveRequest.toTube);
                Map<String, Object> gameState = gameBoard.getGameStateData();
                gameState.put("message", moveResultMessage);

                if (moveResultMessage.toLowerCase().startsWith("movimento inválido") ||
                        moveResultMessage.toLowerCase().startsWith("não é possível") ||
                        moveResultMessage.contains("não permitido")) {
                    res.status(400);
                }
                return gson.toJson(gameState);

            } catch (JsonSyntaxException e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Formato JSON inválido para requisição de movimento."));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(Map.of("error", "Erro interno do servidor: " + e.getMessage()));
            }
        });

        get("/game/validate", (req, res) -> {
            try {
                String fromTubeStr = req.queryParams("fromTube");
                String toTubeStr = req.queryParams("toTube");

                if (fromTubeStr == null || toTubeStr == null) {
                    res.status(400);
                    return gson.toJson(
                            new GameBoard.ValidationResult(false, "Parâmetros 'fromTube' ou 'toTube' ausentes."));
                }

                int fromTube = Integer.parseInt(fromTubeStr);
                int toTube = Integer.parseInt(toTubeStr);

                GameBoard.ValidationResult validationResult = gameBoard.isMoveValid(fromTube, toTube);
                return gson.toJson(validationResult);

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new GameBoard.ValidationResult(false,
                        "Parâmetros 'fromTube' ou 'toTube' inválidos. Devem ser números."));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new GameBoard.ValidationResult(false, "Erro ao validar movimento: " + e.getMessage()));
            }
        });

        get("/game/victory", (req, res) -> {
            try {
                boolean isVictory = gameBoard.checkVictory();
                String message = isVictory ? "Parabéns! Você venceu!" : "O jogo ainda está em andamento.";
                int score = gameBoard.getScore();
                int moves = gameBoard.getMoveCount();

                return gson.toJson(new VictoryResponse(isVictory, message, score, moves));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("isVictory", false);
                errorResponse.put("message", "Erro ao verificar status de vitória: " + e.getMessage());
                errorResponse.put("score", 0);
                errorResponse.put("moves", 0);
                return gson.toJson(errorResponse);
            }
        });

        get("/status", (req, res) -> gson.toJson(Map.of("status", "Servidor do jogo de classificação de bolas está rodando")));

        System.out.println("Servidor do jogo de classificação de bolas rodando em http://localhost:4567");
    }

    static class MoveRequest {
        Integer fromTube;
        Integer toTube;

        public boolean isValid() {
            return fromTube != null && toTube != null;
        }
    }
}