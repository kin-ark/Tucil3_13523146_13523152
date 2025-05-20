package rush_hour.solver.heuristic;

import rush_hour.model.GameState;

public class CombinedHeuristic implements HeuristicFunction {
    private final DistanceHeuristic distanceHeuristic = new DistanceHeuristic();
    private final BlockerHeuristic blockerCountHeuristic = new BlockerHeuristic();

    @Override
    public int calculate(GameState state) {
        return distanceHeuristic.calculate(state) + blockerCountHeuristic.calculate(state);
    }
}