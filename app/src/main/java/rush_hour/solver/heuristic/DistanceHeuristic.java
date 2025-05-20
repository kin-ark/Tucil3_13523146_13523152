package rush_hour.solver.heuristic;

import rush_hour.model.GameEnums;
import rush_hour.model.GamePiece;
import rush_hour.model.GameState;

public class DistanceHeuristic implements HeuristicFunction {
    @Override
    public int calculate(GameState state) {
        GamePiece primaryPiece = getPrimaryPiece(state);
        char[][] grid = state.getBoard().getGridCopy();
        GameEnums.GoalPlacement goalSide = state.getBoard().getGoalPlacement();

        int distanceToGoal = 0;

        for (int[] pos : primaryPiece.getPositions()) {
            int x = pos[0];
            int y = pos[1];
            
            switch (goalSide) {
                case RIGHT -> distanceToGoal = grid[0].length - 1 - x;
                case LEFT -> distanceToGoal = x;
                case BOTTOM -> distanceToGoal = grid.length - 1 - y;
                case TOP -> distanceToGoal = y;
            }
            break;
        }

        return distanceToGoal;
    }

    private GamePiece getPrimaryPiece(GameState state) {
        for (GamePiece piece : state.getPieces()) {
            if (piece.getId() == 'P') return piece;
        }
        throw new IllegalStateException("Primary piece (id='P') not found");
    }
}