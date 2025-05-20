package rush_hour.solver.comparator;

import java.util.Comparator;

import rush_hour.model.GameState;
import rush_hour.solver.heuristic.HeuristicFunction;

public class GreedyComparator implements Comparator<GameState>{
    private final HeuristicFunction heuristic;

    public GreedyComparator(HeuristicFunction heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public int compare(GameState a, GameState b) {
        return Integer.compare(
            heuristic.calculate(a),
            heuristic.calculate(b)
        );
    }
}
