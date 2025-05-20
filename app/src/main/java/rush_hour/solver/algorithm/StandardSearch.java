package rush_hour.solver.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import rush_hour.model.GameState;

public class StandardSearch implements SearchAlgorithm{
    private final Comparator<GameState> comparator;
    private int nodesExplored = 0;

    public StandardSearch(Comparator<GameState> comparator) {
        this.comparator = comparator;
    }

    @Override
    public List<GameState> solve(GameState initialState) {
        PriorityQueue<GameState> queue = new PriorityQueue<>(comparator);
        Set<String> visited = new HashSet<>();
        nodesExplored = 0;

        queue.add(initialState);

        while (!queue.isEmpty()) {
            nodesExplored++;
            GameState current = queue.poll();

            if (visited.contains(current.getBoard().toString())) {
                continue;
            }

            visited.add(current.getBoard().toString());

            if (current.isGoal()) {
                GameState lastState = current.lastMove();
                nodesExplored +=  current.getPrimaryPiece().getPositions().size();
                return reconstructPath(lastState);
            }

            for (GameState neighbor : current.generateSuccessors()) {
                if (!visited.contains(neighbor.getBoard().toString())) {
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public int getNodesExplored() {
        return nodesExplored;
    }

    private List<GameState> reconstructPath(GameState goalState) {
        List<GameState> path = new ArrayList<>();
        GameState current = goalState;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}
