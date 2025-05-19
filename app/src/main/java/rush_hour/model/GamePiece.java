package rush_hour.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GamePiece {
    private final char id;
    private final List<int[]> positions;
    private final GameEnums.Orientation orientation;

    public GamePiece(char id, List<int[]> positions) {
        if (positions == null || positions.size() < 2) {
            throw new IllegalArgumentException("Positions must contain at least two points");
        }
        this.id = id;
        this.positions = new ArrayList<>(positions);
        this.orientation = determineOrientation();
        validatePositions();
    }

    public GamePiece(GamePiece other) {
        this.id = other.id;
        this.orientation = other.orientation;
        this.positions = new ArrayList<>();
        for (int[] pos : other.positions) {
            this.positions.add(new int[] { pos[0], pos[1] });
        }
    }

    private GameEnums.Orientation determineOrientation() {
        int[] first = positions.get(0);
        int[] second = positions.get(1);
        return (first[1] == second[1]) ? GameEnums.Orientation.HORIZONTAL : GameEnums.Orientation.VERTICAL;
    }

    private void validatePositions() {
        if (orientation == GameEnums.Orientation.HORIZONTAL) {
            int row = positions.get(0)[1];
            for (int[] pos : positions) {
                if (pos[1] != row) {
                    throw new IllegalArgumentException("All positions must be in the same row for horizontal pieces");
                }
            }
        }
        else {
            int col = positions.get(0)[0];
            for (int[] pos : positions) {
                if (pos[0] != col) {
                    throw new IllegalArgumentException("All positions must be in the same row for horizontal pieces");
                }
            }
        }
    }

    // Getters
    public char getId() {
        return id;
    }

    public List<int[]> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    public GameEnums.Orientation getOrientation() {
        return orientation;
    }

    public boolean canMove(int distance, char[][] board) {
        for (int[] pos : positions) {
            int newX = pos[0];
            int newY = pos[1];

            if (orientation == GameEnums.Orientation.HORIZONTAL) {
                newX += distance;
                if (newX < 0 || newX >= board[0].length) return false; // cols
                if (!occupiesPosition(newX, pos[1]) && board[pos[1]][newX] != '.') return false; // [row][col]
            } else {
                newY += distance;
                if (newY < 0 || newY >= board.length) return false; // rows
                if (!occupiesPosition(pos[0], newY) && board[newY][pos[0]] != '.') return false;
            }
        }
        return true;
    }

    public void move(int distance) {
        for (int[] pos : positions) {
            if (orientation == GameEnums.Orientation.HORIZONTAL) {
                pos[0] += distance;
            } else {
                pos[1] += distance;
            }
        }
    }

    private boolean occupiesPosition(int x, int y) {
        for (int[] pos : positions) {
            if (pos[0] == x && pos[1] == y) return true;
        }
        return false;
    }
}