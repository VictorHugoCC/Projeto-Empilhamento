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

                String moveResultMessage = traduzirMensagem(
                        gameBoard.makeMove(moveRequest.fromTube, moveRequest.toTube));
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
                            new GameBoard.ValidationResult(false, "Missing 'fromTube' or 'toTube' query parameters."));
                }

                int fromTube = Integer.parseInt(fromTubeStr);
                int toTube = Integer.parseInt(toTubeStr);

                GameBoard.ValidationResult validationResult = gameBoard.isMoveValid(fromTube, toTube);
                return gson.toJson(validationResult);

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new GameBoard.ValidationResult(false,
                        "Invalid 'fromTube' or 'toTube' query parameters. Must be numbers."));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new GameBoard.ValidationResult(false, "Error validating move: " + e.getMessage()));
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

        get("/status", (req, res) -> gson.toJson(Map.of("status", "Color Ball Classification Game Server is running")));

        System.out.println("Color Ball Classification Game Server running on http://localhost:4567");
    }

    static class MoveRequest {
        Integer fromTube;
        Integer toTube;

        public boolean isValid() {
            return fromTube != null && toTube != null;
        }
    }

    private static String traduzirMensagem(String msg) {
        if (msg == null)
            return "";
        if (msg.equals("Invalid tube selection."))
            return "Movimento inválido: seleção de tubo incorreta.";
        if (msg.equals("Cannot move from an empty tube."))
            return "Não é possível mover de um tubo vazio.";
        if (msg.equals("Cannot move to a full tube."))
            return "Não é possível mover para um tubo cheio.";
        if (msg.equals("Immediate reverse move is not allowed."))
            return "Movimento reverso imediato não é permitido.";
        if (msg.equals("Move successful."))
            return "Movimento realizado com sucesso!";
        if (msg.startsWith("Victory!"))
            return "Parabéns! Você venceu! " + msg.replace("Victory!", "");
        return msg;
    }
}