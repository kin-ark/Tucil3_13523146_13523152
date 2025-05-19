package com.kinan.rushhour.model;

import java.util.*;

public class GameState implements Comparable<GameState> {
    private final GameBoard board;
    private final List<GamePiece> pieces;
    private final GameState parent;
    private final int cost;       // g(n)
    private final int heuristic;  // h(n)
    private final String moveDescription;

    public enum Strategy {
        UCS, GREEDY, ASTAR
    }

    public static Strategy comparisonStrategy = Strategy.ASTAR;

    public static void setComparisonStrategy(Strategy strategy) {
        comparisonStrategy = strategy;
    }

    public GameState(GameBoard board, List<GamePiece> pieces, GameState parent, String moveDescription) {
        this.board = board;
        this.pieces = pieces;
        this.parent = parent;
        this.moveDescription = moveDescription;
        this.cost = parent == null ? 0 : parent.cost + 1;
        this.heuristic = calculateHeuristic();
    }

    private int calculateHeuristic() {
        GamePiece primary = getPrimaryPiece();
        char[][] grid = board.getGridCopy();
        GameEnums.GoalPlacement goalSide = board.getGoalPlacement();

        Set<Character> blockers = new HashSet<>();
        int distanceToGoal = 0;

        for (int[] pos : primary.getPositions()) {
            int x = pos[0];
            int y = pos[1];

            switch (goalSide) {
                case RIGHT -> {
                    distanceToGoal = grid[0].length - 1 - x;
                    for (int col = x + 1; col < grid[0].length; col++) {
                        char cell = grid[y][col];
                        if (cell != '.' && cell != 'P') {
                            blockers.add(cell);
                        }
                    }
                }
                case LEFT -> {
                    distanceToGoal = x;
                    for (int col = x - 1; col >= 0; col--) {
                        char cell = grid[y][col];
                        if (cell != '.' && cell != 'P') {
                            blockers.add(cell);
                        }
                    }
                }
                case BOTTOM -> {
                    distanceToGoal = grid.length - 1 - y;
                    for (int row = y + 1; row < grid.length; row++) {
                        char cell = grid[row][x];
                        if (cell != '.' && cell != 'P') {
                            blockers.add(cell);
                        }
                    }
                }
                case TOP -> {
                    distanceToGoal = y;
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

        return distanceToGoal + blockers.size();
    }

    private GamePiece getPrimaryPiece() {
        for (GamePiece piece : pieces) {
            if (piece.getId() == 'P') return piece;
        }
        throw new IllegalStateException("Red car (id='P') not found");
    }

    public boolean isGoal() {
        return board.isGoalReached(getPrimaryPiece());
    }

    public List<GameState> generateSuccessors() {
        List<GameState> successors = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            GamePiece piece = pieces.get(i);
            for (int dir : new int[]{-1, 1}) {
                GamePiece movedPiece = new GamePiece(piece);
                try {
                    if (movedPiece.canMove(dir, board.getGridCopy())) {
                        movedPiece.move(dir);
                        List<GamePiece> newPieces = new ArrayList<>(pieces);
                        newPieces.set(i, movedPiece);

                        GameBoard newBoard = new GameBoard(
                                board.getGridCopy().length,
                                board.getGridCopy()[0].length,
                                board.getGoalPlacement(),
                                board.getGoalIndex()
                        );
                        newBoard.placePieces(newPieces);

                        String direction;
                        if (piece.getOrientation() == GameEnums.Orientation.HORIZONTAL) {
                            direction = ( dir == 1 ? "Right" : "Left");
                        } else {
                            direction = ( dir == 1 ? "Down" : "Up");
                        }

                        successors.add(new GameState(newBoard, newPieces, this,
                                "Move " + piece.getId() + " " + direction));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return successors;
    }

    public boolean isPrimaryPieceMoved() {
        return moveDescription != null && moveDescription.contains("P");
    }

    @Override
    public int compareTo(GameState other) {
        int result = switch (comparisonStrategy) {
            case UCS -> Integer.compare(this.cost, other.cost);
            case GREEDY -> Integer.compare(this.heuristic, other.heuristic);
            case ASTAR -> Integer.compare(this.cost + this.heuristic, other.cost + other.heuristic);
        };

        if (result == 0) {
            if (this.isPrimaryPieceMoved() && !other.isPrimaryPieceMoved()) return -1;
            if (!this.isPrimaryPieceMoved() && other.isPrimaryPieceMoved()) return 1;
        }

        return result;
    }

    public List<GamePiece> getPieces() {
        return pieces;
    }

    public int getCost() {
        return cost;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public String getMoveDescription() {
        return moveDescription;
    }

    public GameState getParent() {
        return parent;
    }

    public GameBoard getBoard() {
        return board;
    }
}