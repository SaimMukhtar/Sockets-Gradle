package Assign32starter;

import java.io.*;
import java.net.*;
import org.json.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8888;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public JSONObject sendRequest(JSONObject request) throws IOException {
        out.println(request.toString());
        String response = in.readLine();
        return new JSONObject(response);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void connect() throws IOException {
        connect(DEFAULT_HOST, DEFAULT_PORT);
    }
}