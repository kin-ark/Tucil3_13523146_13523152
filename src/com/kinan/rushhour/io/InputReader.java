package com.kinan.rushhour.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InputReader {
    public int A, B, N;
    public char[][] board;
    public char primaryPiece = 'P';
    public char goalPiece = 'K';
    public List<List<int[]>> puzzlePieces;
    public char[] ids;

    private List<String> rawBoardLines;
    public int[] goalPosition = null;
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
                if (line == null)
                {
                    if (!isGoalFound && i != A) {throw new Error("Unexpected end of file when reading board.");}
                    break;
                } 

                if (line.length() < B || line.length() > B + 1) {
                    throw new Error("Invalid line length at row " + i + ": must be " + B + " or " + (B + 1) + " characters.");
                }

                rawBoardLines.add(line);
                
                for (int j = 0; j < line.length(); j++) {
                    char c = line.charAt(j);
                    if (c == goalPiece) {
                        if (goalPosition != null) {
                            throw new Error("Multiple goal positions ('K') found.");
                        }

                        boolean outsideLeft = (i < A && j == 0);
                        boolean outsideTop = (i == 0 && j < B);
                        boolean outsideRight = (j == B && i < A);
                        boolean outsideBottom = (i == A && j < B);

                        if (outsideLeft || outsideTop || outsideRight || outsideBottom) {
                            goalPosition = new int[]{j, i};
                            isGoalFound = true;
                        } else {
                            throw new Error("Goal piece ('K') must be on the border.");
                        }
                    }
                }
            }

            if (goalPosition == null) {
                throw new Error("No goal position ('K') found.");
            }
            else
            {
                if (goalPosition[0] == 0 && goalPosition[1] < A && rawBoardLines.size() == A) { // outsideLeft
                    startA = 0;
                    endA = A;
                    startB = 1;
                    endB = B + 1;
                } else if (goalPosition[0] < B && goalPosition[1] == 0 && rawBoardLines.size() == A + 1) { // outsideTop
                    startA = 1;
                    endA = A + 1;
                    startB = 0;
                    endB = B;
                } else { // outsideRight & outsideBottom
                    startA = 0;
                    endA = A;
                    startB = 0;
                    endB = B;
                }
            }
            
            // Parse the rawBoardLines to board
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

                    if (goalPosition[0] == 0 && goalPosition[1] < A && rawBoardLines.size() == A) { // outsideLeft
                        boardCol = j - 1;
                    } else if (goalPosition[0] < B && goalPosition[1] == 0 && rawBoardLines.size() == A + 1) { // outsideTop
                        boardRow = i - 1;
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

            // Set primaryPiece = 'P' if present, otherwise use first
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
