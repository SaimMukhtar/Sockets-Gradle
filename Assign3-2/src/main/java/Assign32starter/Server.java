package Assign32starter;

import java.io.*;
import java.net.*;
import org.json.*;

public class Server {
    private static final int PORT = 8888;
    private static LeaderBoard leaderBoard;

    public static void main(String[] args) {
        leaderBoard = new LeaderBoard();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, leaderBoard).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private LeaderBoard leaderBoard;
    private PrintWriter out;
    private BufferedReader in;
    private GameState gameState;
    private String playerName;

    public ClientHandler(Socket socket, LeaderBoard leaderBoard) {
        this.socket = socket;
        this.leaderBoard = leaderBoard;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                JSONObject request = new JSONObject(inputLine);
                JSONObject response = handleRequest(request);
                out.println(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject handleRequest(JSONObject request) {
        String type = request.getString("type");
        JSONObject response = new JSONObject();

        try {
            switch (type) {
                case "hello":
                    response.put("type", "request_name_age");
                    break;
                case "name_age":
                    playerName = request.getString("name");
                    int age = request.getInt("age");
                    response.put("type", "greeting");
                    response.put("message", "Welcome, " + playerName + "! You are " + age + " years old.");
                    break;
                case "start":
                    int rounds = request.getInt("rounds");
                    gameState = new GameState(rounds);
                    response = gameState.startGame();
                    break;
                case "guess":
                    response = gameState.makeGuess(request.getString("guess"));
                    break;
                case "skip":
                    response = gameState.skipWonder();
                    break;
                case "next":
                    response = gameState.getNextHint();
                    break;
                case "remaining":
                    response = gameState.getRemainingHints();
                    break;
                case "next_round":
                    response = gameState.startNextRound();
                    break;
                case "leaderboard":
                    response = leaderBoard.getLeaderboard();
                    break;
                default:
                    response.put("type", "error");
                    response.put("message", "Invalid request type");
            }
        } catch (Exception e) {
            response.put("type", "error");
            response.put("message", "Server error: " + e.getMessage());
        }

        if (gameState != null && gameState.isGameOver()) {
            int finalScore = gameState.getFinalScore();
            leaderBoard.updateScore(playerName, finalScore);
            response.put("type", "game_over");
            response.put("score", finalScore);
        }

        return response;
    }
}