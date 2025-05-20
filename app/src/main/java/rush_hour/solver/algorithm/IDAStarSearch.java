package rush_hour.solver.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rush_hour.model.GameState;
import rush_hour.solver.heuristic.HeuristicFunction;

public class IDAStarSearch implements SearchAlgorithm {
    private final HeuristicFunction heuristic;
    private int nodesExplored = 0;
    private Map<String, Integer> visited; // String board, Integer cost

    public IDAStarSearch(HeuristicFunction heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public List<GameState> solve(GameState initialState) {
        nodesExplored = 0;
        int threshold = heuristic.calculate(initialState);
        
        while (true) {
            visited = new HashMap<>();
            List<GameState> initialPath = new ArrayList<>();
            initialPath.add(initialState);
            Result result = search(initialState, threshold, initialPath);
            
            if (result.found) {
                return result.path;
            }
            
            if (result.nextThreshold == Integer.MAX_VALUE) {
                return Collections.emptyList();
            }
            
            threshold = result.nextThreshold;
        }
    }

    private Result search(GameState state, int threshold, List<GameState> path) {
        nodesExplored++;
        
        int f = state.getCost() + heuristic.calculate(state);
        
        if (f > threshold) {
            return new Result(false, null, f);
        }
        
        String boardKey = state.getBoard().toString();
        Integer previousCost = visited.get(boardKey);
        if (previousCost != null && previousCost <= state.getCost()) {
            return new Result(false, null, threshold + 1);
        }
        visited.put(boardKey, state.getCost());
        
        if (state.isGoal()) {
            GameState lastState = state.lastMove();
            path.add(lastState);
            nodesExplored += state.getPrimaryPiece().getPositions().size();
            return new Result(true, path, threshold);
        }
        
        // Recursive DFS
        int nextThreshold = Integer.MAX_VALUE;
        List<GameState> successors = state.generateSuccessors();
        
        for (GameState successor : successors) {
            path.add(successor);
            Result result = search(successor, threshold, path);
            
            if (result.found) {
                return result;
            }
            
            path.remove(path.size() - 1);
            nextThreshold = Math.min(nextThreshold, result.nextThreshold);
        }
        
        return new Result(false, null, nextThreshold);
    }

    @Override
    public int getNodesExplored() {
        return nodesExplored;
    }
    
    private static class Result {
        final boolean found;
        final List<GameState> path;
        final int nextThreshold;
        
        Result(boolean found, List<GameState> path, int nextThreshold) {
            this.found = found;
            this.path = path;
            this.nextThreshold = nextThreshold;
        }
    }
}