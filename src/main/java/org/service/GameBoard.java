package org.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GameBoard {
    private final List<Tube> tubes;
    private int moveCount;
    private Move lastMove;
    private final List<Move> moveHistory;
    private int score;

    public static final int NUM_TUBES = 7;
    public static final int BALLS_PER_COLOR = 7;
    public static final int NUM_COLORS = 6;
    public static final String[] POSSIBLE_COLORS = { "Red", "Green", "Blue", "Yellow", "Purple", "Orange" };

    public static class ValidationResult {
        public boolean isValid;
        public String message;

        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
    }

    public GameBoard() {
        this.tubes = new ArrayList<>(NUM_TUBES);
        for (int i = 0; i < NUM_TUBES; i++) {
            tubes.add(new Tube(Tube.MAX_CAPACITY));
        }
        this.moveHistory = new ArrayList<>();
        initializeGame();
    }

    public void initializeGame() {
        for (Tube tube : tubes) {
            tube.clear();
        }
        this.moveCount = 0;
        this.lastMove = null;
        this.moveHistory.clear();
        this.score = 0;

        List<Ball> allBalls = new ArrayList<>();
        for (int i = 0; i < NUM_COLORS; i++) {
            for (int j = 0; j < BALLS_PER_COLOR; j++) {
                allBalls.add(new Ball(POSSIBLE_COLORS[i]));
            }
        }
        Collections.shuffle(allBalls, new Random());

        int ballIndex = 0;
        for (int i = 0; i < NUM_COLORS; i++) {
            Tube currentTube = tubes.get(i);
            for (int j = 0; j < BALLS_PER_COLOR; j++) {
                if (ballIndex < allBalls.size()) {
                    currentTube.pushBall(allBalls.get(ballIndex++));
                }
            }
        }
    }

    public synchronized String makeMove(int fromTubeIndex, int toTubeIndex) {
        if (fromTubeIndex < 0 || fromTubeIndex >= NUM_TUBES ||
                toTubeIndex < 0 || toTubeIndex >= NUM_TUBES ||
                fromTubeIndex == toTubeIndex) {
            return "Invalid tube selection.";
        }

        Tube fromTube = tubes.get(fromTubeIndex);
        Tube toTube = tubes.get(toTubeIndex);

        if (fromTube.isEmpty()) {
            return "Cannot move from an empty tube.";
        }
        if (toTube.isFull()) {
            return "Cannot move to a full tube.";
        }

        Ball ballToMove = fromTube.peekBall();

        if (lastMove != null && lastMove.isReversedBy(fromTubeIndex, toTubeIndex, ballToMove)) {
            return "Immediate reverse move is not allowed.";
        }


        ballToMove = fromTube.popBall();
        toTube.pushBall(ballToMove);

        this.lastMove = new Move(fromTubeIndex, toTubeIndex, ballToMove);
        this.moveHistory.add(this.lastMove);
        this.moveCount++;

        if (checkVictory()) {
            return "Victory! All tubes are sorted. Total moves: " + moveCount;
        }
        return "Move successful.";
    }

    public ValidationResult isMoveValid(int fromTubeIndex, int toTubeIndex) {
        if (fromTubeIndex < 0 || fromTubeIndex >= NUM_TUBES ||
                toTubeIndex < 0 || toTubeIndex >= NUM_TUBES) {
            return new ValidationResult(false, "Tube index out of bounds.");
        }
        if (fromTubeIndex == toTubeIndex) {
            return new ValidationResult(false, "Source and destination tubes cannot be the same.");
        }

        Tube fromTube = tubes.get(fromTubeIndex);
        Tube toTube = tubes.get(toTubeIndex);

        if (fromTube.isEmpty()) {
            return new ValidationResult(false, "Cannot move from an empty tube.");
        }
        if (toTube.isFull()) {
            return new ValidationResult(false, "Destination tube is full.");
        }

        Ball ballToValidate = fromTube.peekBall();
        if (lastMove != null && lastMove.isReversedBy(fromTubeIndex, toTubeIndex, ballToValidate)) {
            return new ValidationResult(false, "Immediate reverse move is not allowed.");
        }

        return new ValidationResult(true, "Move is valid.");
    }

    public boolean checkVictory() {
        for (Tube tube : tubes) {
            if (!tube.isHomogeneousOrEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void resetGame() {
        initializeGame();
    }

    public Map<String, Object> getGameStateData() {
        Map<String, Object> gameState = new HashMap<>();
        List<List<String>> tubeContents = new ArrayList<>();
        for (Tube tube : tubes) {
            tubeContents.add(tube.getBallColors());
        }
        gameState.put("tubes", tubeContents);

        gameState.put("tubeCapacities", this.tubes.stream().map(Tube::getCapacity).collect(Collectors.toList()));
        gameState.put("tubeSizes", this.tubes.stream().map(Tube::getSize).collect(Collectors.toList()));

        gameState.put("moveCount", this.moveCount);
        gameState.put("score", this.score);
        gameState.put("isVictory", checkVictory());

        if (this.lastMove != null) {
            Map<String, Object> lastMoveData = new HashMap<>();
            lastMoveData.put("from", this.lastMove.fromTubeIndex());
            lastMoveData.put("to", this.lastMove.toTubeIndex());
            lastMoveData.put("ballColor", this.lastMove.movedBall().getColor());
            gameState.put("lastMove", lastMoveData);
        } else {
            gameState.put("lastMove", null);
        }

        return gameState;
    }

    public List<Tube> getTubes() {
        return Collections.unmodifiableList(tubes);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getScore() {
        return score;
    }

}