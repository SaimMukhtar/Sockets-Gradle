import java.io.*;
import java.net.*;

public class SockServer {
    public static void main(String[] args) {
        int port = 12345; // Define the server port number
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port + "...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Read message from client
                    String clientMessage = in.readLine();
                    System.out.println("Received from client: " + clientMessage);

                    // Send response back to client
                    String serverResponse = "Hello from server! You sent: " + clientMessage;
                    out.println(serverResponse);
                    System.out.println("Sent to client: " + serverResponse);
                    
                } catch (IOException e) {
                    System.err.println("Error in communication with client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
