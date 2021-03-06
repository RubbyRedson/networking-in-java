package se.kth.networking.java.first;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick on 11/2/2016.
 */
public class Server {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private GameHandler gameHandler;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(5);
        gameHandler = new GameHandler();
    }

    public void start() throws IOException, InterruptedException {
        while(true) {
            Socket client = serverSocket.accept();
            handleClient(client);
        }
    }

    public void shutdown() {
        try {
            this.executorService.shutdown();
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidMessage(String msg){
        return msg != null && !msg.trim().equalsIgnoreCase(":") && msg.contains(":");
    }

    private String handleMessage(String clientMessage, String username){

        String response = "Bad request";

        if(isValidMessage(clientMessage)){
            String[] parts = clientMessage.split(":");
            switch (parts[0]){
                case "start_game":
                    response = gameHandler.startANewGame(username);
                    break;
                case "give_up":
                    response = gameHandler.giveUp(username);
                    break;
                case "guess":
                    response = gameHandler.guess(username, parts[1].trim());
                    break;
                case "status":
                    response = gameHandler.status(username);
                    break;
                case "error":
                    gameHandler.error(username);
                    break;
            }
        }

        return response;

    }

    private void handleClient(Socket client) throws InterruptedException {
        executorService.execute(new ClientSocketHandler(client, new OnResponse<String>() {
            @Override
            public String onResponse(String response, String username) {
                return handleMessage(response, username);
            }
        }));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.valueOf(args[0]);
        Server server = new Server(port);
        server.start();
    }



}
