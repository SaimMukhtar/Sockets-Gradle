package org.example;

import org.example.sudoku.protocol.LeaderboardEntry;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.ser";
    private final ConcurrentHashMap<String, PlayerScore> leaderboard;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LeaderboardManager() {
        leaderboard = loadLeaderboard();
    }

    private ConcurrentHashMap<String, PlayerScore> loadLeaderboard() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE))) {
            return (ConcurrentHashMap<String, PlayerScore>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ConcurrentHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ConcurrentHashMap<>();
        }
    }

    public void saveLeaderboard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(leaderboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateScore(String playerName, int score) {
        lock.writeLock().lock();
        try {
            PlayerScore playerScore = leaderboard.computeIfAbsent(playerName, PlayerScore::new);
            playerScore.updateScore(score);
            saveLeaderboard();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<LeaderboardEntry> getLeaderboardEntries() {
        lock.readLock().lock();
        try {
            List<LeaderboardEntry> entries = new ArrayList<>();
            for (PlayerScore score : leaderboard.values()) {
                entries.add(LeaderboardEntry.newBuilder()
                        .setUsername(score.getName())
                        .setScore(score.getHighScore())
                        .setLoginCount(score.getLoginCount())
                        .build());
            }
            entries.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
            return entries;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void incrementLogin(String playerName) {
        lock.writeLock().lock();
        try {
            PlayerScore playerScore = leaderboard.getOrDefault(playerName, new PlayerScore(playerName));
            playerScore.incrementLoginCount();
            saveLeaderboard();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static class PlayerScore implements Serializable {
        private final String name;
        private int highScore;
        private int loginCount;

        public PlayerScore(String name) {
            this.name = name;
            this.highScore = 0;
            this.loginCount = 0;
        }

        public void updateScore(int score) {
            if (score > highScore) {
                highScore = score;
            }
        }

        public void incrementLoginCount() {
            loginCount++;
        }

        public String getName() {
            return name;
        }

        public int getHighScore() {
            return highScore;
        }

        public int getLoginCount() {
            return loginCount;
        }
    }
}

