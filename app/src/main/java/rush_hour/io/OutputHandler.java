package rush_hour.io;

import rush_hour.model.GameState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputHandler {
    public static void saveSolutionPath(List<GameState> path, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (GameState state : path) {
                writer.write(state.toString());
                writer.write("\n------------------------------\n");
            }
            System.out.println("Solution path successfully written to: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to save solution path: " + e.getMessage());
        }
    }
}