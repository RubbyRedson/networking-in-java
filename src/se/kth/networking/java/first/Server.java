package se.kth.networking.java.first;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

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

    private void handleClient(Socket client) throws InterruptedException {
        executorService.execute(new ClientSocketHandler(client, new OnResponse<String>() {
            @Override
            public String onResponse(String response) {


                System.out.println("In server: " + response);

                return "Something something";
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

    private String selectRandomWord() {
        return words.get(random.nextInt(words.size())).toLowerCase().trim();
    }
}
