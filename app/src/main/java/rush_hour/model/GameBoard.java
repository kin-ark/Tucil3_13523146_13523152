package rush_hour.model;

import java.util.Arrays;
import java.util.List;

public class GameBoard {
    private final int rows, cols;
    private final char[][] grid;
    private final GameEnums.GoalPlacement goalPlacement;
    private final int goalIndex;

    public GameBoard(int rows, int cols, GameEnums.GoalPlacement goalPlacement, int goalIndex) {
        this.rows = rows;
        this.cols = cols;
        this.goalPlacement = goalPlacement;
        this.goalIndex = goalIndex;
        this.grid = new char[rows][cols];
        for (char[] row : grid) {
            Arrays.fill(row, '.');
        }
    }

    public void placePieces(List<GamePiece> pieces) {
        for (GamePiece piece : pieces) {
            for (int[] pos : piece.getPositions()) {
                int col = pos[0];
                int row = pos[1];
                if (!isInBounds(col, row)) {
                    throw new IllegalArgumentException("Piece out of bounds");
                }
                if (grid[row][col] != '.') {
                    throw new IllegalArgumentException("Overlapping pieces detected");
                }
                grid[row][col] = piece.getId();
            }
        }
    }

    public boolean isGoalReached(GamePiece primary) {
        for (int[] pos : primary.getPositions()) {
            int col = pos[0];
            int row = pos[1];
            switch (goalPlacement) {
                case LEFT -> {
                    if (col == 0 && row == goalIndex) return true;
                }
                case RIGHT -> {
                    if (col == cols - 1 && row == goalIndex) return true;
                }
                case TOP -> {
                    if (col == goalIndex && row == 0) return true;
                }
                case BOTTOM -> {
                    if (col == goalIndex && row == rows - 1) return true;
                }
            }
        }
        return false;
    }

    public boolean isInBounds(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    public char[][] getGridCopy() {
        char[][] copy = new char[rows][cols];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(grid[row], 0, copy[row], 0, cols);
        }
        return copy;
    }

    public int getGoalIndex() {
        return goalIndex;
    }

    public GameEnums.GoalPlacement getGoalPlacement() {
        return goalPlacement;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                sb.append(grid[row][col]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}