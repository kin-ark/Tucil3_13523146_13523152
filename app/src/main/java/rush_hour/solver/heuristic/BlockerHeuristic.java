package rush_hour.solver.heuristic;

import java.util.HashSet;
import java.util.Set;

import rush_hour.model.GameEnums;
import rush_hour.model.GamePiece;
import rush_hour.model.GameState;

public class BlockerHeuristic implements HeuristicFunction {
    @Override
    public int calculate(GameState state) {
        GamePiece primaryPiece = getPrimaryPiece(state);
        char[][] grid = state.getBoard().getGridCopy();
        GameEnums.GoalPlacement goalSide = state.getBoard().getGoalPlacement();

        Set<Character> blockers = new HashSet<>();

        for (int[] pos : primaryPiece.getPositions()) {
            int x = pos[0];
            int y = pos[1];

            switch (goalSide) {
                case RIGHT -> {
                    for (int col = x + 1; col < grid[0].length; col++) {
                        char cell = grid[y][col];
                        if (cell != '.' && cell != 'P') {
                            blockers.add(cell);
                        }
                    }
                }
                case LEFT -> {
                    for (int col = x - 1; col >= 0; col--) {
                        char cell = grid[y][col];
                        if (cell != '.' && cell != 'P') {
                            blockers.add(cell);
                        }
                    }
                }
                case BOTTOM -> {
                    for (int row = y + 1; row < grid.length; row++) {
                        char cell = grid[row][x];
                        if (cell != '.' && cell != 'P') {
                            blockers.add(cell);
                        }
                    }
                }
                case TOP -> {
                    for (int row = y - 1; row >= 0; row--) {
                        char cell = grid[row][x];
                        if (cell != '.' && cell != 'P') {
                            blockers.add(cell);
                        }
                    }
                }
            }
            break;
        }

        return blockers.size();
    }

    private GamePiece getPrimaryPiece(GameState state) {
        for (GamePiece piece : state.getPieces()) {
            if (piece.getId() == 'P') return piece;
        }
        throw new IllegalStateException("Primary piece (id='P') not found");
    }
}