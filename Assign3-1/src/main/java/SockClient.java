import java.io.*;
import java.net.*;

public class SockClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Server address
        int port = 12345; // Server port number

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server at " + serverAddress + ":" + port);

            // Send a message to the server
            System.out.print("Enter a message to send to the server: ");
            String message = userInput.readLine();
            out.println(message);
            System.out.println("Sent to server: " + message);

            // Receive the response from the server
            String response = in.readLine();
            System.out.println("Received from server: " + response);

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
