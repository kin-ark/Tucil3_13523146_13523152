package rush_hour.solver.comparator;

import java.util.Comparator;

import rush_hour.model.GameState;
import rush_hour.solver.heuristic.HeuristicFunction;

public class AStarComparator implements Comparator<GameState> {
    private final HeuristicFunction heuristic;

    public AStarComparator(HeuristicFunction heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public int compare(GameState a, GameState b) {
        return Integer.compare(
            a.getCost() + heuristic.calculate(a),
            b.getCost() + heuristic.calculate(b)
        );
    }
}
