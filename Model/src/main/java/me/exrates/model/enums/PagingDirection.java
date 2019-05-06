package me.exrates.model.enums;

public enum PagingDirection {
    FORWARD(1),
    BACKWARD(-1);

    private final int direction;

    PagingDirection(int status) {
        this.direction = status;
    }

    public int getDirection() {
        return direction;
    }
}
