package Assign32starter;

import org.json.*;
import java.util.*;
import java.io.File;

public class GameState {
    private List<String> wonders;
    private int currentWonder;
    private int currentHint;
    private int totalRounds;
    private int currentRound;
    private int score;

    public GameState(int rounds) {
        wonders = Arrays.asList("Colosseum", "GrandCanyon", "Stonehenge");
        totalRounds = rounds;
        currentRound = 1;
        score = 0;
        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(wonders);
        currentWonder = 0;
        currentHint = 1;
    }

    public JSONObject startGame() {
        JSONObject response = new JSONObject();
        response.put("type", "game_start");
        response.put("total_rounds", totalRounds);
        response.put("current_round", currentRound);
        response.put("hint", getHintPath());
        System.out.println("Answer: " + wonders.get(currentWonder)); // Print answer for grading
        return response;
    }

    public JSONObject makeGuess(String guess) {
        JSONObject response = new JSONObject();
        response.put("type", "guess_result");
        if (guess.equalsIgnoreCase(wonders.get(currentWonder))) {
            score += (5 - currentHint) * 5;
            response.put("result", "Correct! You earned " + ((5 - currentHint) * 5) + " points.");
            response.put("correct", true);
            response.put("score", score);
        } else {
            response.put("result", "Incorrect. Try again!");
            response.put("correct", false);
        }
        return response;
    }

    public JSONObject skipWonder() {
        nextWonder();
        return getNextHint();
    }

    public JSONObject getNextHint() {
        JSONObject response = new JSONObject();
        if (currentHint < 4) {
            currentHint++;
        }
        response.put("type", "hint");
        response.put("hint", getHintPath());
        response.put("current_round", currentRound);
        System.out.println("Answer: " + wonders.get(currentWonder)); // Print answer for grading
        return response;
    }

    public JSONObject getRemainingHints() {
        JSONObject response = new JSONObject();
        response.put("type", "remaining_hints");
        response.put("remaining", 5 - currentHint);
        return response;
    }

    public JSONObject startNextRound() {
        currentRound++;
        nextWonder();
        return getNextHint();
    }

    private void nextWonder() {
        currentWonder = (currentWonder + 1) % wonders.size();
        currentHint = 1;
    }

    private String getHintPath() {
        String baseDir = System.getProperty("user.dir");
        String basePath = baseDir + File.separator + "img" + File.separator + wonders.get(currentWonder) + currentHint;
        File pngFile = new File(basePath + ".png");
        File jpgFile = new File(basePath + ".jpg");

        System.out.println("Attempting to load image: " + basePath + ".png or " + basePath + ".jpg");

        if (pngFile.exists()) {
            return pngFile.getAbsolutePath();
        } else if (jpgFile.exists()) {
            return jpgFile.getAbsolutePath();
        } else {
            System.err.println("Image file not found: " + basePath + ".png or " + basePath + ".jpg");
            return baseDir + File.separator + "img" + File.separator + "questions.jpg"; // Fallback image
        }
    }

    public boolean isGameOver() {
        return currentRound > totalRounds;
    }

    public int getFinalScore() {
        return score;
    }
}