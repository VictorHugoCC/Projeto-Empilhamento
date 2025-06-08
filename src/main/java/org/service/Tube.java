package org.service;

import java.util.Stack;
import java.util.List;
import java.util.stream.Collectors;

public class Tube {
    private final Stack<Ball> balls;
    private final int capacity;
    public static final int MAX_CAPACITY = 7;

    public Tube(int capacity) {
        this.capacity = capacity;
        this.balls = new Stack<>();
    }

    public Tube() {
        this(MAX_CAPACITY);
    }

    public boolean pushBall(Ball ball) {
        if (isFull()) {
            return false;
        }
        balls.push(ball);
        return true;
    }

    public Ball popBall() {
        if (isEmpty()) {
            return null;
        }
        return balls.pop();
    }

    public Ball peekBall() {
        if (isEmpty()) {
            return null;
        }
        return balls.peek();
    }

    public boolean isEmpty() {
        return balls.isEmpty();
    }

    public boolean isFull() {
        return balls.size() >= capacity;
    }

    public int getSize() {
        return balls.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isHomogeneousOrEmpty() {
        if (isEmpty()) {
            return true;
        }
        if (balls.size() == 1) {
            return true;
        }
        String firstColor = balls.peek().getColor();
        for (Ball ball : balls) {
            if (!ball.getColor().equals(firstColor)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getBallColors() {
        return balls.stream().map(Ball::getColor).collect(Collectors.toList());
    }

    public void addBalls(List<Ball> initialBalls) {
        for (Ball ball : initialBalls) {
            if (!isFull()) {
                this.balls.push(ball);
            } else {
                break;
            }
        }
    }

    public void clear() {
        balls.clear();
    }
}