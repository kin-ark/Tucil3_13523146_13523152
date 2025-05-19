package rush_hour.gui;

import rush_hour.io.InputReader;
import rush_hour.model.GameBoard;
import rush_hour.model.GamePiece;
import rush_hour.model.GameState;
import rush_hour.solver.GameSolver;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GameSolverGUI {
    private JFrame frame;
    private JButton selectFileButton;
    private File selectedFile;
    private JRadioButton ucsButton, greedyButton, aStarButton;
    private JLabel loadingLabel;
    private JPanel boardPanel;
    private List<GameState> solutionPath;
    private long solveTime;
    private int rows, cols;
    private boolean done;

    public GameSolverGUI() {
        done = false;
        frame = new JFrame("Rush Hour Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Rush Hour Solver");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 20));

        selectFileButton = new JButton("Pick Puzzle File (.txt)");
        styleButton(selectFileButton);
        selectFileButton.addActionListener(e -> openFileChooser());

        ucsButton = new JRadioButton("UCS");
        greedyButton = new JRadioButton("Greedy Best First");
        aStarButton = new JRadioButton("A*");
        ButtonGroup group = new ButtonGroup();
        group.add(ucsButton);
        group.add(greedyButton);
        group.add(aStarButton);
        ucsButton.setSelected(true);

        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        gbc.gridy++;
        panel.add(ucsButton, gbc);
        gbc.gridy++;
        panel.add(greedyButton, gbc);
        gbc.gridy++;
        panel.add(aStarButton, gbc);
        gbc.gridy++;
        panel.add(selectFileButton, gbc);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectFileButton.setText("File Selected: " + selectedFile.getName());
            showLoadingScreen();
        }
    }

    private void showLoadingScreen() {
        frame.getContentPane().removeAll();

        JLabel statusLabel = new JLabel("Solving puzzle...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        frame.add(statusLabel, BorderLayout.NORTH);

        loadingLabel = new JLabel("⠋", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        frame.add(loadingLabel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();

        new Thread(this::runSolver).start();
        startLoadingAnimation();
    }

    private void startLoadingAnimation() {
        new Thread(() -> {
            String[] spinnerFrames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
            int i = 0;
            while (solutionPath == null) {
                loadingLabel.setText(spinnerFrames[i % spinnerFrames.length]);
                i++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();
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
            done = true;
        } catch (Exception e) {
            showErrorDialog("Exception: " + e.getMessage());
            return;
        } catch (Error e) {
            showErrorDialog("Error: " + e.getMessage());
        }
        if (done) {
            for (GameState state : solutionPath) {
                System.out.println(state.getMoveDescription());
            }
            SwingUtilities.invokeLater(this::showSolutionAnimation);
        }
    }

    private void showSolutionAnimation() {
        frame.getContentPane().removeAll();

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        JLabel timeLabel = new JLabel("Solve time: " + solveTime + " ms", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        topPanel.add(timeLabel);

        JLabel turnLabel = new JLabel("Nodes checked: " + GameSolver.turn, SwingConstants.CENTER);
        turnLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        topPanel.add(turnLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));
        frame.add(boardPanel, BorderLayout.CENTER);

        JButton restartButton = new JButton("Restart App");
        styleButton(restartButton);
        restartButton.addActionListener(e -> restartApp());
        frame.add(restartButton, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();

        new Thread(() -> {
            for (GameState state : solutionPath) {
                updateBoard(state);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void updateBoard(GameState state) {
        SwingUtilities.invokeLater(() -> {
            boardPanel.removeAll();
            char[][] board = state.getBoard().getGridCopy();
            for (char[] row : board) {
                for (char cell : row) {
                    JLabel label = new JLabel(String.valueOf(cell), SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setBackground(
                        cell == '.' 
                            ? Color.LIGHT_GRAY 
                            : (cell == 'P' 
                                ? new Color(255, 182, 193)
                                : new Color(173, 216, 230)
                            )
                    );
                    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    boardPanel.add(label);
                }
            }
            boardPanel.revalidate();
            boardPanel.repaint();
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(89, 119, 212));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(59, 89, 182));
            }
        });
    }

    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            restartApp();
        });
    }

    private void restartApp() {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            GameSolver.turn = 0;
            new GameSolverGUI();
        });
    }
}