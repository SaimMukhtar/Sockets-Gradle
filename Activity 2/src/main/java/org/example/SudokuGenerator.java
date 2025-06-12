package org.example;

import java.util.Random;

public class SudokuGenerator {
    private static final int GRID_SIZE = 9;

    public int[][] generateSudoku() {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        fillDiagonal(grid);
        solveSudoku(grid);
        return grid;
    }

    private void fillDiagonal(int[][] grid) {
        for (int i = 0; i < GRID_SIZE; i += 3) {
            fillBox(grid, i, i);
        }
    }

    private void fillBox(int[][] grid, int row, int col) {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int num;
                do {
                    num = random.nextInt(9) + 1;
                } while (!isValid(grid, row + i, col + j, num));
                grid[row + i][col + j] = num;
            }
        }
    }

    private boolean isValid(int[][] grid, int row, int col, int num) {
        for (int x = 0; x < GRID_SIZE; x++) {
            if (grid[row][x] == num) return false;
        }

        for (int x = 0; x < GRID_SIZE; x++) {
            if (grid[x][col] == num) return false;
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i + startRow][j + startCol] == num) return false;
            }
        }

        return true;
    }

    private boolean solveSudoku(int[][] grid) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (grid[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num;
                            if (solveSudoku(grid)) {
                                return true;
                            } else {
                                grid[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
}

