package rush_hour.solver.comparator;

import java.util.Comparator;

import rush_hour.model.GameState;

public class UCSComparator implements Comparator<GameState>{
    @Override
    public int compare(GameState a, GameState b) {
        return Integer.compare(a.getCost(), b.getCost());
    }
}