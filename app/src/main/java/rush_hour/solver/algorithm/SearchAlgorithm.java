package rush_hour.solver.algorithm;

import rush_hour.model.GameState;
import java.util.List;

public interface SearchAlgorithm {
    List<GameState> solve(GameState initialState);
    int getNodesExplored();
}