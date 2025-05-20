package rush_hour.solver;

import java.util.List;

import rush_hour.model.GameState;
import rush_hour.solver.algorithm.SearchAlgorithm;
import rush_hour.solver.algorithm.StandardSearch;
import rush_hour.solver.comparator.AStarComparator;
import rush_hour.solver.comparator.GreedyComparator;
import rush_hour.solver.comparator.UCSComparator;
import rush_hour.solver.heuristic.HeuristicFunction;
import rush_hour.solver.heuristic.StandardHeuristic;

public class GameSolver {
    public static SearchAlgorithm createSolver(String algorithmName) {
        HeuristicFunction heuristic = new StandardHeuristic();
        
        return switch (algorithmName) {
            case "UCS" -> new StandardSearch(new UCSComparator());
            case "Greedy Best First" -> new StandardSearch(new GreedyComparator(heuristic));
            case "A*" -> new StandardSearch(new AStarComparator(heuristic));
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithmName);
        };
    }

    public static SolverResult solve(GameState initialState, String algorithmName) {
        SearchAlgorithm algorithm = createSolver(algorithmName);
        List<GameState> path = algorithm.solve(initialState);
        return new SolverResult(path, algorithm.getNodesExplored());
    }

    public static class SolverResult {
        private final List<GameState> path;
        private final int nodesExplored;

        public SolverResult(List<GameState> path, int nodesExplored) {
            this.path = path;
            this.nodesExplored = nodesExplored;
        }

        public List<GameState> getPath() {
            return path;
        }

        public int getNodesExplored() {
            return nodesExplored;
        }
    }
}