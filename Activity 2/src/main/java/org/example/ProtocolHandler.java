package org.example;

import org.example.sudoku.protocol.*;

public class ProtocolHandler {
    private final LeaderboardManager leaderboardManager;
    private final SudokuGenerator sudokuGenerator;

    public ProtocolHandler(LeaderboardManager leaderboardManager, SudokuGenerator sudokuGenerator) {
        this.leaderboardManager = leaderboardManager;
        this.sudokuGenerator = sudokuGenerator;
    }

    public LoginResponse handleLogin(LoginRequest request) {
        String username = request.getUsername();
        leaderboardManager.incrementLogin(username);
        return LoginResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Login successful")
                .setMainMenu(MainMenu.newBuilder()
                        .addOptions("1. Play Game")
                        .addOptions("2. View Leaderboard")
                        .addOptions("3. Quit")
                        .build())
                .build();
    }

    public GameBoard handleStartGame(StartGameRequest request) {
        int difficulty = request.getDifficulty();
        Game game = new Game(sudokuGenerator.generateSudoku(), difficulty);
        return GameBoard.newBuilder()
                .addAllCells(game.getCurrentBoardAsList())
                .setGameOver(false)
                .setScore(game.getPoints())
                .build();
    }

    public GameBoard handleMakeMove(MakeMoveRequest request, Game game) {
        boolean moveResult = game.makeMove(request.getRow(), request.getCol(), request.getValue());
        return GameBoard.newBuilder()
                .addAllCells(game.getCurrentBoardAsList())
                .setGameOver(game.isComplete())
                .setScore(game.getPoints())
                .build();
    }

    public Leaderboard handleGetLeaderboard(LeaderboardRequest request) {
        return Leaderboard.newBuilder()
                .addAllEntries(leaderboardManager.getLeaderboardEntries())
                .build();
    }

    public QuitResponse handleQuit(QuitRequest request) {
        return QuitResponse.newBuilder()
                .setMessage("Goodbye!")
                .build();
    }

    public SudokuGenerator getSudokuGenerator() {
        return sudokuGenerator;
    }
}

