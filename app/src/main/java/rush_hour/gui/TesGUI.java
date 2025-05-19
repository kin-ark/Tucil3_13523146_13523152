package rush_hour.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import rush_hour.io.InputReader;
import rush_hour.model.GameBoard;
import rush_hour.model.GamePiece;
import rush_hour.model.GameState;
import rush_hour.solver.GameSolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class TesGUI extends Application {
    private Stage primaryStage;
    private File selectedFile;
    private RadioButton ucsButton, greedyButton, aStarButton;
    private GridPane boardDisplay;
    private List<GameState> solutionPath;
    private long solveTime;
    private int rows, cols;
    private Label loadingLabel;
    private Timeline loadingAnimation;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Rush Hour Solver");
        primaryStage.setWidth(600);
        primaryStage.setHeight(600);
        
        showMainMenu();
        
        primaryStage.show();
    }

    private void showMainMenu() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Rush Hour Solver");
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        
        // Algorithm selection
        ToggleGroup algorithmGroup = new ToggleGroup();
        ucsButton = new RadioButton("UCS");
        greedyButton = new RadioButton("Greedy Best First");
        aStarButton = new RadioButton("A*");
        
        ucsButton.setToggleGroup(algorithmGroup);
        greedyButton.setToggleGroup(algorithmGroup);
        aStarButton.setToggleGroup(algorithmGroup);
        ucsButton.setSelected(true);
        
        VBox algorithmBox = new VBox(10, ucsButton, greedyButton, aStarButton);
        algorithmBox.setAlignment(Pos.CENTER_LEFT);
        
        // File selection button
        Button selectFileButton = new Button("Pick Puzzle File (.txt)");
        styleButton(selectFileButton);
        selectFileButton.setOnAction(e -> openFileChooser());
        
        root.getChildren().addAll(titleLabel, algorithmBox, selectFileButton);
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        selectedFile = fileChooser.showOpenDialog(primaryStage);
        
        if (selectedFile != null) {
            showLoadingScreen();
        }
    }

    private void showLoadingScreen() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30));
        
        Label statusLabel = new Label("Solving puzzle...");
        statusLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        statusLabel.setAlignment(Pos.CENTER);
        
        loadingLabel = new Label("⠋");
        loadingLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 40));
        
        StackPane centerPane = new StackPane(loadingLabel);
        centerPane.setAlignment(Pos.CENTER);
        
        root.setTop(statusLabel);
        root.setCenter(centerPane);
        BorderPane.setAlignment(statusLabel, Pos.CENTER);
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        
        startLoadingAnimation();
        CompletableFuture.runAsync(this::runSolver);
    }
    
    private void startLoadingAnimation() {
        String[] spinnerFrames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        AtomicInteger frameIndex = new AtomicInteger(0);
        
        loadingAnimation = new Timeline(
            new KeyFrame(Duration.millis(100), e -> {
                loadingLabel.setText(spinnerFrames[frameIndex.getAndIncrement() % spinnerFrames.length]);
            })
        );
        loadingAnimation.setCycleCount(Timeline.INDEFINITE);
        loadingAnimation.play();
    }
    
    private void runSolver() {
        try {
            InputReader reader = new InputReader(selectedFile.getAbsolutePath());
            rows = reader.A;
            cols = reader.B;
            
            GameState.setComparisonStrategy(GameState.Strategy.UCS);
            if (greedyButton.isSelected()) GameState.setComparisonStrategy(GameState.Strategy.GREEDY);
            else if (aStarButton.isSelected()) GameState.setComparisonStrategy(GameState.Strategy.ASTAR);

            List<GamePiece> pieces = new ArrayList<>();
            for (int i = 0; i < reader.ids.length; i++) {
                pieces.add(new GamePiece(reader.ids[i], reader.puzzlePieces.get(i)));
            }

            GameBoard initialBoard = new GameBoard(reader.A, reader.B, reader.goalPlacement, reader.goalIndex);
            initialBoard.placePieces(pieces);

            GameState initialState = new GameState(initialBoard, pieces, null, "Start");

            long startTime = System.currentTimeMillis();
            solutionPath = GameSolver.solve(initialState, GameState.comparisonStrategy);
            long endTime = System.currentTimeMillis();
            solveTime = endTime - startTime;
            
            Platform.runLater(this::showSolutionAnimation);
        } catch (Exception e) {
            Platform.runLater(() -> showErrorDialog("Exception: " + e.getMessage()));
        } catch (Error e) {
            Platform.runLater(() -> showErrorDialog("Error: " + e.getMessage()));
        }
    }
    
    private void showSolutionAnimation() {
        if (loadingAnimation != null) {
            loadingAnimation.stop();
        }
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Top panel with statistics
        GridPane statsPane = new GridPane();
        statsPane.setHgap(10);
        statsPane.setVgap(10);
        statsPane.setPadding(new Insets(0, 0, 20, 0));
        
        Label timeLabel = new Label("Solve time: " + solveTime + " ms");
        timeLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        
        Label nodesLabel = new Label("Nodes checked: " + GameSolver.turn);
        nodesLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        
        statsPane.add(timeLabel, 0, 0);
        statsPane.add(nodesLabel, 1, 0);
        
        // Game board
        boardDisplay = new GridPane();
        boardDisplay.setAlignment(Pos.CENTER);
        
        // Restart button
        Button restartButton = new Button("Restart App");
        styleButton(restartButton);
        restartButton.setOnAction(e -> restartApp());
        
        StackPane bottomPane = new StackPane(restartButton);
        bottomPane.setPadding(new Insets(20, 0, 0, 0));
        
        root.setTop(statsPane);
        root.setCenter(boardDisplay);
        root.setBottom(bottomPane);
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        
        // Start the animation
        animateSolution();
    }
    
    private void animateSolution() {
        AtomicInteger stepIndex = new AtomicInteger(0);
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(500), e -> {
                int index = stepIndex.getAndIncrement();
                if (index < solutionPath.size()) {
                    updateBoard(solutionPath.get(index));
                }
            })
        );
        timeline.setCycleCount(solutionPath.size());
        timeline.play();
    }
    
    private void updateBoard(GameState state) {
        boardDisplay.getChildren().clear();
        
        char[][] board = state.getBoard().getGridCopy();
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                char cell = board[row][col];
                
                Label cellLabel = new Label(String.valueOf(cell));
                cellLabel.setAlignment(Pos.CENTER);
                cellLabel.setPrefSize(50, 50);
                cellLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
                
                // Cell styling
                StackPane cellPane = new StackPane(cellLabel);
                cellPane.setStyle("-fx-border-color: black; -fx-border-width: 1;");
                
                if (cell == '.') {
                    cellPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                } else if (cell == 'P') {
                    cellPane.setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    cellPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                
                boardDisplay.add(cellPane, col, row);
            }
        }
    }
    
    private void showErrorDialog(String message) {
        if (loadingAnimation != null) {
            loadingAnimation.stop();
        }
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        
        restartApp();
    }
    
    private void restartApp() {
        GameSolver.turn = 0;
        showMainMenu();
    }
    
    private void styleButton(Button button) {
        button.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        button.setStyle(
            "-fx-background-color: #3b59b6; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 20 10 20;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: #5977d4; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20 10 20;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: #3b59b6; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20 10 20;"
            )
        );
    }
    
    public static void launchApp(String[] args) {
        launch(args);
    }
}