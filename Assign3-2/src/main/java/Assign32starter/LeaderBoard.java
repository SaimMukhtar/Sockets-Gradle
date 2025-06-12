package Assign32starter;

import org.json.*;
import java.util.*;
import java.io.*;

public class LeaderBoard {
    private Map<String, Integer> scores;
    private static final String LEADERBOARD_FILE = "leaderboard.dat";

    public LeaderBoard() {
        scores = new HashMap<>();
        loadLeaderboard();
    }

    public void updateScore(String name, int score) {
        scores.put(name, Math.max(scores.getOrDefault(name, 0), score));
        saveLeaderboard();
    }

    public JSONObject getLeaderboard() {
        JSONObject response = new JSONObject();
        JSONArray leaderboard = new JSONArray();
        scores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> {
                JSONObject player = new JSONObject();
                player.put("name", entry.getKey());
                player.put("score", entry.getValue());
                leaderboard.put(player);
            });
        response.put("type", "leaderboard");
        response.put("leaderboard", leaderboard);
        return response;
    }

    private void loadLeaderboard() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE))) {
            Object object = ois.readObject();
            if (object instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Integer> loadedScores = (Map<String, Integer>) object;
                scores = loadedScores;
            } else {
                System.out.println("Error: Deserialized object is not a Map<String, Integer>");
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, which is fine for a new leaderboard
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    

    private void saveLeaderboard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}