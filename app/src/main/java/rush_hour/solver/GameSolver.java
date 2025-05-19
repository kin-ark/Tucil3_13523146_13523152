package rush_hour.solver;

import rush_hour.model.*;
import java.util.*;

public class GameSolver {
    public static int turn = 0;

    public static List<GameState> solve(GameState initialState, GameState.Strategy strategy) {
        GameState.setComparisonStrategy(strategy);

        PriorityQueue<GameState> prioQ = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        prioQ.add(initialState);

        while (!prioQ.isEmpty()) {
            turn++;
            GameState current = prioQ.poll();

            if (visited.contains(current.getBoard().toString())) {
                continue;
            }

            visited.add(current.getBoard().toString());

            if (current.isGoal()) {
                // System.err.println(current.getBoard());
                return reconstructPath(current);
            }

            for (GameState neighbor : current.generateSuccessors()) {
                if (!visited.contains(neighbor.getBoard().toString())) {
                    prioQ.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    private static List<GameState> reconstructPath(GameState goalState) {
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