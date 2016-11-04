package se.kth.networking.java.first;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-04.
 */
public class GameHandler {

    private SecureRandom random;
    private List<String> words;

    public GameHandler(){
        this.words = new ArrayList<>();
        this.random = new SecureRandom();
        try {
            this.readWords();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readWords() throws IOException {
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

    String selectRandomWord() {
        return words.get(random.nextInt(words.size())).toLowerCase().trim();
    }

    int wordCount() {
        return words.size();
    }

    public String startANewGame(String username){
        System.out.println("starting a new game");

        return "A new game was started";
    }

    public String giveUp(String username) {
        return "Giving up";
    }

    public String guess(String username, String part) {

        return "guessing";
    }

    public String status(String username) {
        return "status";
    }
}
