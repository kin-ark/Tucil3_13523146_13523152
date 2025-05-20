package rush_hour.solver.heuristic;

import rush_hour.model.GameState;

public interface HeuristicFunction {
    int calculate(GameState state);
}
