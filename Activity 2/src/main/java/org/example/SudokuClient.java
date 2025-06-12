package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.sudoku.protocol.*;

import java.util.Scanner;

public class SudokuClient {
    private final SudokuServiceGrpc.SudokuServiceBlockingStub blockingStub;
    private final Scanner scanner;
    private String username;

    public SudokuClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = SudokuServiceGrpc.newBlockingStub(channel);
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Sudoku Game!");
        login();
        displayMainMenu();
    }

    private void login() {
        System.out.print("Enter your username: ");
        username = scanner.nextLine();
        LoginRequest request = LoginRequest.newBuilder().setUsername(username).build();
        LoginResponse response = blockingStub.login(request);
        System.out.println(response.getMessage());
    }

    private void displayMainMenu() {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Play Game");
            System.out.println("2. View Leaderboard");
            System.out.println("3. Quit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    playGame();
                    break;
                case 2:
                    viewLeaderboard();
                    break;
                case 3:
                    quit();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void playGame() {
        System.out.print("Enter difficulty level (1-5): ");
        int difficulty = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        StartGameRequest request = StartGameRequest.newBuilder()
                .setDifficulty(difficulty)
                .setUsername(username)
                .build();
        GameBoard gameBoard = blockingStub.startGame(request);

        while (!gameBoard.getGameOver()) {
            displayBoard(gameBoard);
            System.out.println("Score: " + gameBoard.getScore());
            System.out.print("Enter row (0-8), column (0-8), and value (1-9) separated by spaces: ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            int value = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            MakeMoveRequest moveRequest = MakeMoveRequest.newBuilder()
                    .setUsername(username)
                    .setRow(row)
                    .setCol(col)
                    .setValue(value)
                    .build();

            try {
                gameBoard = blockingStub.makeMove(moveRequest);
            } catch (Exception e) {
                System.out.println("Error making move: " + e.getMessage());
                return;
            }
        }

        System.out.println("Game Over! Final Score: " + gameBoard.getScore());
    }

    private void displayBoard(GameBoard gameBoard) {
        System.out.println("Current Board:");
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("---------------------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                int value = gameBoard.getCells(i * 9 + j);
                System.out.print((value == 0 ? "." : value) + " ");
            }
            System.out.println();
        }
    }

    private void viewLeaderboard() {
        LeaderboardRequest request = LeaderboardRequest.newBuilder().build();
        Leaderboard leaderboard = blockingStub.getLeaderboard(request);
        System.out.println("\nLeaderboard:");
        for (LeaderboardEntry entry : leaderboard.getEntriesList()) {
            System.out.printf("%s: Score %d (Logins: %d)%n",
                    entry.getUsername(), entry.getScore(), entry.getLoginCount());
        }
    }

    private void quit() {
        QuitRequest request = QuitRequest.newBuilder().build();
        QuitResponse response = blockingStub.quit(request);
        System.out.println(response.getMessage());
    }

    public static void main(String[] args) {
        SudokuClient client = new SudokuClient("localhost", 50051);
        client.start();
    }
}

