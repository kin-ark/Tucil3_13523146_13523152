package rush_hour.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameState {
    private final GameBoard board;
    private final List<GamePiece> pieces;
    private final GameState parent;
    private final int cost;
    private final String moveDescription;

    public GameState(GameBoard board, List<GamePiece> pieces, GameState parent, String moveDescription) {
        this.board = board;
        this.pieces = Collections.unmodifiableList(new ArrayList<>(pieces));
        this.parent = parent;
        this.moveDescription = moveDescription;
        this.cost = parent == null ? 0 : parent.cost + 1;
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

    public List<GamePiece> getPieces() {
        return pieces;
    }

    public int getCost() {
        return cost;
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