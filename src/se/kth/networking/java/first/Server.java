package se.kth.networking.java.first;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick on 11/2/2016.
 */
public class Server {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private SecureRandom random;
    private List<String> words;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.words = new ArrayList<>();
        this.random = new SecureRandom();
        this.executorService = Executors.newFixedThreadPool(5);
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
        return msg != null && msg.contains(":");
    }

    private String handleMessage(String clientMessage){

        String response = "Bad request";

        if(isValidMessage(clientMessage)){
            String[] parts = clientMessage.split(":");
            switch (parts[0]){
                case "start_game":
                    break;
                case "give_up":
                    response = "gave over!";
                    break;
                case "guess":
                    break;
                case "status":
                    break;
            }
        }

        return response;

    }

    private void handleClient(Socket client) throws InterruptedException {
        executorService.execute(new ClientSocketHandler(client, new OnResponse<String>() {
            @Override
            public String onResponse(String response) {
                return handleMessage(response);
            }
        }));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.valueOf(args[0]);
        Server server = new Server(port);
        server.readWords();
        server.start();
    }

    public void readWords() throws IOException {
        String line;
        URL url = getClass().getResource("words.txt");
        try (
                InputStream fis = new FileInputStream(new File(url.getPath()));
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        }
    }

    public String selectRandomWord() {
        return words.get(random.nextInt(words.size())).toLowerCase().trim();
    }

    int wordCount() {
        return words.size();
    }
}
