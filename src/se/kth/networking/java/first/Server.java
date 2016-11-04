package se.kth.networking.java.first;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by Nick on 11/2/2016.
 */
public class Server {
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public void start() throws IOException, InterruptedException {
        while(true) {
            Socket client = serverSocket.accept();
            handleClient(client);
        }
    }

    private void handleClient(Socket client) throws InterruptedException {
        executorService.execute(new ClientSocketHandler(client, new OnResponse<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("In server: " + response);

            }
        }));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.valueOf(args[0]);
        Server server = new Server(port);
        server.start();
    }


}
