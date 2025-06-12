package Assign32starter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ClientGui implements Assign32starter.OutputPanel.EventHandlers {
    private JDialog frame;
    private PicturePanel picPanel;
    private OutputPanel outputPanel;
    private Client client;
    private String playerName;
    private int playerAge;
    private int totalRounds;
    private int currentRound;

    public ClientGui(String host, int port) {
        initializeGUI();

        client = new Client();
        try {
            client.connect(host, port);
            sendHello();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeGUI() {
        frame = new JDialog();
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        picPanel = new PicturePanel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.BOTH;
        frame.add(picPanel, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.75;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        
        outputPanel = new OutputPanel();
        outputPanel.addEventHandlers(this);
        frame.add(outputPanel, c);
    }

    public void show(boolean makeModal) {
        frame.pack();
        frame.setModal(makeModal);
        frame.setVisible(true);
    }

    private void sendHello() throws IOException {
        JSONObject request = new JSONObject();
        request.put("type", "hello");
        JSONObject response = client.sendRequest(request);
        handleServerResponse(response);
    }

    private void handleServerResponse(JSONObject response) throws IOException {
        String type = response.getString("type");
        switch (type) {
            case "request_name_age":
                requestNameAndAge();
                break;
            case "greeting":
                displayGreeting(response.getString("message"));
                showMainMenu();
                break;
            case "game_start":
                startGame(response);
                break;
            case "hint":
                displayHint(response);
                break;
            case "guess_result":
                handleGuessResult(response);
                break;
            case "game_over":
                handleGameOver(response);
                break;
            case "leaderboard":
                displayLeaderboard(response);
                break;
            case "error":
                displayError(response.getString("message"));
                break;
            default:
                displayError("Unknown response type from server");
        }
    }

    private void requestNameAndAge() {
        playerName = JOptionPane.showInputDialog(frame, "Enter your name:");
        String ageStr = JOptionPane.showInputDialog(frame, "Enter your age:");
        try {
            playerAge = Integer.parseInt(ageStr);
            try {
                sendNameAndAge();
            } catch (IOException e) {
                e.printStackTrace();
                displayError("Error sending name and age to the server.");
            }
        } catch (NumberFormatException e) {
            displayError("Invalid age. Please enter a number.");
            requestNameAndAge();
        }
    }
    
    private void sendNameAndAge() throws IOException {
        JSONObject request = new JSONObject();
        request.put("type", "name_age");
        request.put("name", playerName);
        request.put("age", playerAge);
        JSONObject response = client.sendRequest(request);
        handleServerResponse(response);
    }

    private void displayGreeting(String message) {
        outputPanel.appendOutput(message);
    }

    private void showMainMenu() {
        String[] options = {"See Leaderboard", "Play Game", "Quit"};
        int choice = JOptionPane.showOptionDialog(frame, "Choose an option:", "Main Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        try {
            switch (choice) {
                case 0:
                    requestLeaderboard();
                    break;
                case 1:
                    requestGameStart();
                    break;
                case 2:
                    quit();
                    break;
                default:
                    showMainMenu();
            }
        } catch (IOException e) {
            displayError("Error communicating with server: " + e.getMessage());
        }
    }

    private void requestLeaderboard() throws IOException {
        JSONObject request = new JSONObject();
        request.put("type", "leaderboard");
        JSONObject response = client.sendRequest(request);
        handleServerResponse(response);
    }

    private void requestGameStart() throws IOException {
        String roundsStr = JOptionPane.showInputDialog(frame, "How many rounds would you like to play?");
        try {
            int rounds = Integer.parseInt(roundsStr);
            JSONObject request = new JSONObject();
            request.put("type", "start");
            request.put("rounds", rounds);
            JSONObject response = client.sendRequest(request);
            handleServerResponse(response);
        } catch (NumberFormatException e) {
            displayError("Invalid number of rounds. Please enter a number.");
            requestGameStart();
        }
    }

    private void startGame(JSONObject response) {
        totalRounds = response.getInt("total_rounds");
        currentRound = 1;
        outputPanel.appendOutput("Starting game with " + totalRounds + " rounds.");
        displayHint(response);
    }

    private void displayHint(JSONObject response) {
        String imagePath = response.getString("hint");
        outputPanel.appendOutput("Round " + currentRound + " of " + totalRounds);
        outputPanel.appendOutput("Enter your guess or type 'skip', 'next', or 'remaining'");
    
        try {
            // Check if imagePath is valid and display the image
            if (imagePath != null && !imagePath.isEmpty()) {
                picPanel.insertImage(imagePath, 1, 1);
            } else {
                displayError("Invalid image path provided by the server.");
            }
        } catch (Exception e) {
            // Catch any exception that may arise (such as IOException)
            displayError("Error displaying image: " + e.getMessage());
        }
    }
    

    private void handleGuessResult(JSONObject response) throws IOException {
        String result = response.getString("result");
        outputPanel.appendOutput(result);
        if (response.getBoolean("correct")) {
            currentRound++;
            if (currentRound <= totalRounds) {
                JSONObject nextRound = client.sendRequest(new JSONObject().put("type", "next_round"));
                handleServerResponse(nextRound);
            } else {
                handleGameOver(response);
            }
        }
    }

    private void handleGameOver(JSONObject response) {
        int finalScore = response.getInt("score");
        outputPanel.appendOutput("Game Over! Your final score is: " + finalScore);
        try {
            requestLeaderboard();
        } catch (IOException e) {
            displayError("Error fetching leaderboard: " + e.getMessage());
        }
        showMainMenu();
    }

    private void displayLeaderboard(JSONObject response) {
        JSONArray leaderboard = response.getJSONArray("leaderboard");
        StringBuilder sb = new StringBuilder("Leaderboard:\n");
        for (int i = 0; i < leaderboard.length(); i++) {
            JSONObject entry = leaderboard.getJSONObject(i);
            sb.append(i + 1).append(". ")
              .append(entry.getString("name")).append(": ")
              .append(entry.getInt("score")).append("\n");
        }
        outputPanel.appendOutput(sb.toString());
        showMainMenu();
    }

    private void displayError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void quit() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            displayError("Error closing the client.");
        }
        System.exit(0);
    }

    @Override
    public void submitClicked() {
        String input = outputPanel.getInputText();
        try {
            JSONObject request = new JSONObject();
            switch (input.toLowerCase()) {
                case "skip":
                    request.put("type", "skip");
                    break;
                case "next":
                    request.put("type", "next");
                    break;
                case "remaining":
                    request.put("type", "remaining");
                    break;
                default:
                    request.put("type", "guess");
                    request.put("guess", input);
            }
            JSONObject response = client.sendRequest(request);
            handleServerResponse(response);
        } catch (IOException e) {
            displayError("Error communicating with server: " + e.getMessage());
        }
        outputPanel.setInputText("");
    }
    
    @Override
    public void inputUpdated(String input) {
        // This method can be used to provide real-time feedback as the user types
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String host = "localhost";
                int port = 8888;
                ClientGui main = new ClientGui(host, port);
                main.show(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error starting client: " + e.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}