package rush_hour.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import rush_hour.io.InputReader;
import rush_hour.model.GameBoard;
import rush_hour.model.GamePiece;
import rush_hour.model.GameState;
import rush_hour.solver.GameSolver;

public class GameSolverGUI extends Application {
    // Color palette
    private static final Color BACKGROUND_COLOR = Color.web("#2E3440");
    private static final Color PRIMARY_COLOR = Color.web("#5E81AC");
    private static final Color PRIMARY_LIGHT = Color.web("#88C0D0");
    private static final Color TEXT_COLOR = Color.web("#ECEFF4");
    private static final Color EMPTY_CELL = Color.web("#3B4252");
    private static final Color PLAYER_CELL = Color.web("#A3BE8C");
    private static final Color OTHER_CELL = Color.web("#BF616A");

    private Stage primaryStage;
    private File selectedFile;
    private ComboBox<String> algorithmComboBox;
    private GridPane boardDisplay;
    private List<GameState> solutionPath;
    private long solveTime;
    private int rows, cols;
    private Label loadingLabel;
    private Timeline loadingAnimation;
    private Timeline solutionAnimation;
    private final AtomicInteger currentStepIndex = new AtomicInteger(0);
    private Slider speedSlider;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Rush Hour Solver");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        showMainMenu();

        primaryStage.show();
    }

    private void showMainMenu() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");

        Label titleLabel = new Label("Rush Hour Solver");
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 32));
        titleLabel.setTextFill(PRIMARY_COLOR);

        // Ini kalo mau ada deskripsi aja sih
        Label descLabel = new Label("Rush Hour Solver with Pathfinding Algorithm");
        descLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        descLabel.setTextFill(TEXT_COLOR);

        // Milih algoritma ganti dropdown lah ya
        HBox algorithmBox = new HBox(15);
        algorithmBox.setAlignment(Pos.CENTER);

        Label algoLabel = new Label("Algorithm:");
        algoLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        algoLabel.setTextFill(TEXT_COLOR);

        algorithmComboBox = new ComboBox<>();
        algorithmComboBox.getItems().addAll("UCS", "Greedy Best First", "A*");
        algorithmComboBox.setValue("UCS");
        algorithmComboBox.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 14px;");
        algorithmComboBox.setPrefWidth(200);

        algorithmBox.getChildren().addAll(algoLabel, algorithmComboBox);

        // File selection button
        Button selectFileButton = new Button("Select Puzzle File");
        styleButton(selectFileButton);
        selectFileButton.setOnAction(e -> openFileChooser());

        root.getChildren().addAll(titleLabel, descLabel, algorithmBox, selectFileButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Puzzle File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            showLoadingScreen();
        }
    }

    private void showLoadingScreen() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");

        Label statusLabel = new Label("Solving puzzle...");
        statusLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        statusLabel.setTextFill(TEXT_COLOR);
        statusLabel.setAlignment(Pos.CENTER);

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        Label fileNameLabel = new Label("File: " + selectedFile.getName());
        fileNameLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        fileNameLabel.setTextFill(TEXT_COLOR);

        String selectedAlgorithm = algorithmComboBox.getValue();
        Label algoLabel = new Label("Algorithm: " + selectedAlgorithm);
        algoLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        algoLabel.setTextFill(TEXT_COLOR);

        topBox.getChildren().addAll(statusLabel, fileNameLabel, algoLabel);

        loadingLabel = new Label("⠋");
        loadingLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 60));
        loadingLabel.setTextFill(PRIMARY_COLOR);

        StackPane centerPane = new StackPane(loadingLabel);
        centerPane.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        styleButton(cancelButton);
        cancelButton.setOnAction(e -> showMainMenu());

        StackPane bottomPane = new StackPane(cancelButton);
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setPadding(new Insets(20, 0, 0, 0));

        root.setTop(topBox);
        root.setCenter(centerPane);
        root.setBottom(bottomPane);
        BorderPane.setAlignment(topBox, Pos.CENTER);

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

            String selectedAlgorithm = algorithmComboBox.getValue();
            switch (selectedAlgorithm) {
                case "Greedy Best First" -> GameState.setComparisonStrategy(GameState.Strategy.GREEDY);
                case "A*" -> GameState.setComparisonStrategy(GameState.Strategy.ASTAR);
                default -> GameState.setComparisonStrategy(GameState.Strategy.UCS);
            }

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

            Platform.runLater(this::showSolutionScreen);
        } catch (Exception e) {
            Platform.runLater(() -> showErrorDialog("Exception: " + e.getMessage()));
        } catch (Error e) {
            Platform.runLater(() -> showErrorDialog("Error: " + e.getMessage()));
        }
    }

    private void showSolutionScreen() {
        if (loadingAnimation != null) {
            loadingAnimation.stop();
        }

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");

        VBox statsBox = new VBox(12);
        statsBox.setAlignment(Pos.CENTER);

        Label resultLabel = new Label("Solution Found!");
        resultLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        resultLabel.setTextFill(PRIMARY_COLOR);

        HBox statsPane = new HBox(30);
        statsPane.setAlignment(Pos.CENTER);

        Label timeLabel = new Label("Time: " + solveTime + " ms");
        timeLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        timeLabel.setTextFill(TEXT_COLOR);

        Label nodesLabel = new Label("Nodes: " + GameSolver.turn);
        nodesLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        nodesLabel.setTextFill(TEXT_COLOR);

        Label stepsLabel = new Label("Steps: " + (solutionPath.size() - 1));
        stepsLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        stepsLabel.setTextFill(TEXT_COLOR);

        statsPane.getChildren().addAll(timeLabel, nodesLabel, stepsLabel);
        statsBox.getChildren().addAll(resultLabel, statsPane);

        StackPane boardContainer = new StackPane();
        boardContainer.setPadding(new Insets(20));
        boardContainer.setStyle(
            "-fx-background-color: " + toHexString(PRIMARY_COLOR) + ";" +
            "-fx-border-color: " + toHexString(PRIMARY_LIGHT) + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 5;"
        );

        boardDisplay = new GridPane();
        boardDisplay.setAlignment(Pos.CENTER);
        boardDisplay.setHgap(2);
        boardDisplay.setVgap(2);

        currentStepIndex.set(0);
        updateBoard(solutionPath.get(0));
        boardContainer.getChildren().add(boardDisplay);

        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(20, 0, 0, 0));

        Label stepIndicator = new Label("Step: 0/" + (solutionPath.size() - 1));
        stepIndicator.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        stepIndicator.setTextFill(TEXT_COLOR);

        Label moveDescriptionLabel = new Label("Initial position");
        moveDescriptionLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        moveDescriptionLabel.setTextFill(TEXT_COLOR);
        moveDescriptionLabel.setPrefWidth(200);
        moveDescriptionLabel.setAlignment(Pos.CENTER);

        Button playPauseButton = new Button("▶ Play");
        styleButton(playPauseButton);
        playPauseButton.setUserData(false);
        playPauseButton.setOnAction(e -> {
            boolean isPlaying = (boolean) playPauseButton.getUserData();
            if (isPlaying) {
                stopAnimation();
                playPauseButton.setText("▶ Play");
                playPauseButton.setUserData(false);
            } else {
                playAnimation(stepIndicator, moveDescriptionLabel, playPauseButton);
                playPauseButton.setText("⏸ Pause");
                playPauseButton.setUserData(true);
            }
        });

        Button prevButton = new Button("◀");
        styleButton(prevButton);
        prevButton.setOnAction(e -> {
            stopAnimation();
            int index = currentStepIndex.decrementAndGet();
            if (index < 0) {
                currentStepIndex.set(0);
                index = 0;
            }
            updateBoard(solutionPath.get(index));
            updateStepInfo(stepIndicator, moveDescriptionLabel, index);
            playPauseButton.setText("▶ Play");
            playPauseButton.setUserData(false);
            playPauseButton.setDisable(false);
        });

        Button nextButton = new Button("▶");
        styleButton(nextButton);
        nextButton.setOnAction(e -> {
            stopAnimation();
            int index = currentStepIndex.incrementAndGet();
            if (index >= solutionPath.size()) {
                currentStepIndex.set(solutionPath.size() - 1);
                index = solutionPath.size() - 1;
            }
            updateBoard(solutionPath.get(index));
            updateStepInfo(stepIndicator, moveDescriptionLabel, index);
            playPauseButton.setText("▶ Play");
            playPauseButton.setUserData(false);
            playPauseButton.setDisable(false);
        });

        Label speedLabel = new Label("Speed:");
        speedLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        speedLabel.setTextFill(TEXT_COLOR);
        
        speedSlider = new Slider(0.5, 3, 1);
        speedSlider.setPrefWidth(100);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(0.5);

        Button resetButton = new Button("↺ Reset");
        styleButton(resetButton);
        resetButton.setOnAction(e -> {
            stopAnimation();
            currentStepIndex.set(0);
            updateBoard(solutionPath.get(0));
            updateStepInfo(stepIndicator, moveDescriptionLabel, 0);
            playPauseButton.setText("▶ Play");
            playPauseButton.setUserData(false);
            playPauseButton.setDisable(false);
        });

        Button backButton = new Button("← Back");
        styleButton(backButton);
        backButton.setOnAction(e -> {
            stopAnimation();
            showMainMenu();
            GameSolver.turn = 0;
        });

        HBox navigationBox = new HBox(10, prevButton, playPauseButton, nextButton);
        navigationBox.setAlignment(Pos.CENTER);

        controlsBox.getChildren().addAll(
            new VBox(5, speedLabel, speedSlider),
            new VBox(5, stepIndicator, moveDescriptionLabel),
            navigationBox, 
            resetButton, 
            backButton
        );

        root.setTop(statsBox);
        root.setCenter(boardContainer);
        root.setBottom(controlsBox);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    private void playAnimation(Label stepIndicator, Label moveDescLabel, Button playPauseButton) {
        stopAnimation();

        solutionAnimation = new Timeline();
        double speed = speedSlider.getValue();

        KeyFrame keyFrame = new KeyFrame(Duration.millis(500 / speed), e -> {
            int index = currentStepIndex.getAndIncrement();
            if (index < solutionPath.size()) {
                updateBoard(solutionPath.get(index));
                updateStepInfo(stepIndicator, moveDescLabel, index);
            } else {
                stopAnimation();
                playPauseButton.setText("▶ Play");
                playPauseButton.setUserData(false);
                playPauseButton.setDisable(true);
            }
        });

        solutionAnimation.getKeyFrames().add(keyFrame);
        solutionAnimation.setCycleCount(Timeline.INDEFINITE);
        solutionAnimation.play();
    }

    private void stopAnimation() {
        if (solutionAnimation != null) {
            solutionAnimation.stop();
        }
    }

    private void updateStepInfo(Label stepIndicator, Label moveDescriptionLabel, int index) {
        stepIndicator.setText("Step: " + index + "/" + (solutionPath.size() - 1));

        if (index == 0) {
            moveDescriptionLabel.setText("Initial position");
        } else {
            GameState state = solutionPath.get(index);
            String moveDescription = state.getMoveDescription();
            moveDescriptionLabel.setText(moveDescription);
        }
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
                cellLabel.setTextFill(TEXT_COLOR);

                StackPane cellPane = new StackPane(cellLabel);
                cellPane.setStyle(
                    "-fx-border-color: " + toHexString(PRIMARY_LIGHT) + ";" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 3;"
                );

                switch (cell) {
                    case '.' -> cellPane.setBackground(new Background(new BackgroundFill(EMPTY_CELL, new CornerRadii(3), Insets.EMPTY)));
                    case 'P' -> cellPane.setBackground(new Background(new BackgroundFill(PLAYER_CELL, new CornerRadii(3), Insets.EMPTY)));
                    default -> cellPane.setBackground(new Background(new BackgroundFill(OTHER_CELL, new CornerRadii(3), Insets.EMPTY)));
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
        alert.setHeaderText("Something is Wrong!");
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");
        dialogPane.lookup(".label.content").setStyle("-fx-font-size: 14px; -fx-font-family: 'Poppins';");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: " + toHexString(PRIMARY_COLOR) + ";");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: " + toHexString(TEXT_COLOR) + ";" + " -fx-font-size: 16px; -fx-font-family: 'Poppins';");

        alert.showAndWait();
        showMainMenu();
    }

    private void styleButton(Button button) {
        button.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        button.setPadding(new Insets(10, 20, 10, 20));

        String baseStyle = 
            "-fx-background-color: " + toHexString(PRIMARY_COLOR) + ";" +
            "-fx-text-fill: " + toHexString(TEXT_COLOR) + ";" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;";
        
        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: " + toHexString(PRIMARY_LIGHT) + ";" +
                "-fx-text-fill: " + toHexString(TEXT_COLOR) + ";" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );

        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    public static void launchApp(String[] args) {
        launch(args);
    }
}