package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    private final int[][] solution;
    private final int[][] currentBoard;
    private final boolean[][] fixedCells;
    private int points;

    public Game(int[][] solution, int difficulty) {
        this.solution = solution;
        this.currentBoard = new int[9][9];
        this.fixedCells = new boolean[9][9];
        this.points = 0;
        initializeBoard(difficulty);
    }

    private void initializeBoard(int difficulty) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (Math.random() > difficulty / 20.0) {
                    currentBoard[i][j] = solution[i][j];
                    fixedCells[i][j] = true;
                } else {
                    currentBoard[i][j] = 0;
                    fixedCells[i][j] = false;
                }
            }
        }
    }

    public boolean makeMove(int row, int col, int value) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9 || value < 1 || value > 9) {
            return false;
        }
        if (fixedCells[row][col]) {
            points -= 2;
            return false;
        }
        if (currentBoard[row][col] == value) {
            return true;
        }
        if (solution[row][col] == value) {
            currentBoard[row][col] = value;
            return true;
        } else {
            points -= 2;
            return false;
        }
    }

    public boolean isComplete() {
        return Arrays.deepEquals(currentBoard, solution);
    }

    public void clearBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!fixedCells[i][j]) {
                    currentBoard[i][j] = 0;
                }
            }
        }
        points -= 5;
    }

    public List<Integer> getCurrentBoardAsList() {
        List<Integer> boardList = new ArrayList<>();
        for (int[] row : currentBoard) {
            for (int cell : row) {
                boardList.add(cell);
            }
        }
        return boardList;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }
}

