package com.kinan.rushhour.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

enum GoalPlacement {
    TOP,
    LEFT,
    RIGHT,
    BOTTOM
}

public class InputReader {
    public int A, B, N;
    public char[][] board;
    public char primaryPiece = 'P';
    public char goalPiece = 'K';
    public List<List<int[]>> puzzlePieces;
    public char[] ids;

    private List<String> rawBoardLines;
    public GoalPlacement goalPlacement;
    public int goalIndex;
    private boolean isGoalFound;
    private int startA, endA, startB, endB;

    public InputReader(String fileName) {
        puzzlePieces = new ArrayList<>();
        rawBoardLines = new ArrayList<>();
        readInputFile(fileName);
    }

    private void readInputFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String[] firstLine = br.readLine().split(" ");
            if (firstLine.length != 2) {
                throw new Error("Invalid first line: Expected format 'A B'");
            }

            // Read A and B (Rows and Columns)
            A = parsePositiveInt(firstLine[0], "Invalid row count (A)");
            B = parsePositiveInt(firstLine[1], "Invalid column count (B)");

            // Read N (Number of Pieces)
            String pLine = br.readLine();
            if (pLine == null) throw new Error("Missing piece count (N)");
            N = parsePositiveInt(pLine.trim(), "Invalid piece count (N)");
            
            // Scan for goal position while get the raw board lines
            for (int i = 0; i <= A; i++) {
                String line = br.readLine();
                if (line == null) {
                    if (!isGoalFound && i != A) {
                        throw new Error("Unexpected end of file when reading board.");
                    }
                    break;
                } 

                if (line.length() < B || line.length() > B + 1) {
                    throw new Error("Invalid line length at row " + i + ": must be " + B + " or " + (B + 1) + " characters.");
                }

                rawBoardLines.add(line);
                
                for (int j = 0; j < line.length(); j++) {
                    char c = line.charAt(j);
                    if (c == goalPiece) {
                        if (isGoalFound) {
                            throw new Error("Multiple goal positions ('K') found.");
                        }

                        if (i == 0 && j < B && rawBoardLines.size() == 7) { // TOP border
                            goalPlacement = GoalPlacement.TOP;
                            goalIndex = j;
                            isGoalFound = true;
                        } 
                        else if (j == 0 && i < A) { // LEFT border
                            goalPlacement = GoalPlacement.LEFT;
                            goalIndex = i;
                            isGoalFound = true;
                        }
                        else if (j == B && i < A) { // RIGHT border
                            goalPlacement = GoalPlacement.RIGHT;
                            goalIndex = i;
                            isGoalFound = true;
                        }
                        else if (i == A && j < B) { // BOTTOM border
                            goalPlacement = GoalPlacement.BOTTOM;
                            goalIndex = j;
                            isGoalFound = true;
                        }
                        else {
                            throw new Error("Goal piece ('K') must be on the border.");
                        }
                    }
                }
            }

            if (!isGoalFound) {
                throw new Error("No goal position ('K') found.");
            }

            switch (goalPlacement) {
                case LEFT:
                    startA = 0;
                    endA = A;
                    startB = 1;
                    endB = B + 1;
                    break;
                case TOP:
                    startA = 1;
                    endA = A + 1;
                    startB = 0;
                    endB = B;
                    break;
                case RIGHT:
                case BOTTOM:
                default:
                    startA = 0;
                    endA = A;
                    startB = 0;
                    endB = B;
                    break;
            }
            
            board = new char[A][B];
            for (int i = 0; i < A; i++) {
                for (int j = 0; j < B; j++) {
                    board[i][j] = '.';
                }
            }

            Map<Character, List<int[]>> pieceMap = new LinkedHashMap<>();

            for (int i = startA; i < endA; i++) {
                String line = rawBoardLines.get(i);
                for (int j = startB; j < endB && j < line.length(); j++) {
                    char c = line.charAt(j);

                    if (c == '.' || c == goalPiece) continue;

                    int boardRow = i;
                    int boardCol = j;

                    switch (goalPlacement) {
                        case LEFT:
                            boardCol = j - 1;
                            break;
                        case TOP:
                            boardRow = i - 1;
                            break;
                        case RIGHT:
                        case BOTTOM:
                        default:
                            break;
                    }

                    if (boardRow < 0 || boardRow >= A || boardCol < 0 || boardCol >= B) continue;

                    board[boardRow][boardCol] = c;

                    pieceMap.putIfAbsent(c, new ArrayList<>());
                    pieceMap.get(c).add(new int[]{boardCol, boardRow});
                }
            }

            ids = new char[pieceMap.size()];
            puzzlePieces = new ArrayList<>();

            int idx = 0;
            for (Map.Entry<Character, List<int[]>> entry : pieceMap.entrySet()) {
                ids[idx] = entry.getKey();
                puzzlePieces.add(entry.getValue());
                idx++;
            }

            boolean foundPrimary = false;
            for (char id : ids) {
                if (id == primaryPiece) {
                    foundPrimary = true;
                    break;
                }
            }
            if (!foundPrimary && ids.length > 0) {
                throw new Error("No Primary Piece ('P') detected!");
            }
        } catch (IOException e) {
            throw new Error("Error reading file: " + e.getMessage());
        }
    }

    private int parsePositiveInt(String value, String errorMessage) {
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
                throw new Error(errorMessage + " must be a positive integer.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new Error(errorMessage + " is not a valid integer.");
        }
    }
}