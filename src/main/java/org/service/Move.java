package org.service;

import java.util.Objects;

public record Move(int fromTubeIndex, int toTubeIndex, Ball movedBall) {

    public boolean isReversedBy(int currentFromIndex, int currentToIndex, Ball currentBall) {
        return this.fromTubeIndex == currentToIndex &&
                this.toTubeIndex == currentFromIndex &&
                Objects.equals(this.movedBall, currentBall);
    }
}