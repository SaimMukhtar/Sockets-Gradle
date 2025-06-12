package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.example.sudoku.protocol.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SudokuServer {
    private final int port;
    private final Server server;
    private final ConcurrentHashMap<String, Game> activeGames = new ConcurrentHashMap<>();

    public SudokuServer(int port) {
        this.port = port;
        LeaderboardManager leaderboardManager = new LeaderboardManager();
        SudokuGenerator sudokuGenerator = new SudokuGenerator();
        ProtocolHandler protocolHandler = new ProtocolHandler(leaderboardManager, sudokuGenerator);
        this.server = ServerBuilder.forPort(port)
                .addService(new SudokuServiceImpl(protocolHandler))
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server started on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server");
            try {
                SudokuServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Server shut down");
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private class SudokuServiceImpl extends SudokuServiceGrpc.SudokuServiceImplBase {
        private final ProtocolHandler protocolHandler;

        public SudokuServiceImpl(ProtocolHandler protocolHandler) {
            this.protocolHandler = protocolHandler;
        }

        @Override
        public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
            LoginResponse response = protocolHandler.handleLogin(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void startGame(StartGameRequest request, StreamObserver<GameBoard> responseObserver) {
            GameBoard response = protocolHandler.handleStartGame(request);
            String username = request.getUsername();
            activeGames.put(username, new Game(protocolHandler.getSudokuGenerator().generateSudoku(), request.getDifficulty()));
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void makeMove(MakeMoveRequest request, StreamObserver<GameBoard> responseObserver) {
            Game game = activeGames.get(request.getUsername());
            if (game == null) {
                responseObserver.onError(new IllegalStateException("No active game found for user"));
                return;
            }
            GameBoard response = protocolHandler.handleMakeMove(request, game);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getLeaderboard(LeaderboardRequest request, StreamObserver<Leaderboard> responseObserver) {
            Leaderboard response = protocolHandler.handleGetLeaderboard(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void quit(QuitRequest request, StreamObserver<QuitResponse> responseObserver) {
            QuitResponse response = protocolHandler.handleQuit(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;
        final SudokuServer server = new SudokuServer(port);
        server.start();
        server.blockUntilShutdown();
    }
}

