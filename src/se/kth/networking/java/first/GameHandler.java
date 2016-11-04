package se.kth.networking.java.first;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by victoraxelsson on 2016-11-04.
 */
public class GameHandler {

    private SecureRandom random;
    private List<String> words;
    private Map<String, Game> games;

    public GameHandler(){
        this.words = new ArrayList<>();
        this.random = new SecureRandom();
        this.games = new HashMap<>();
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
        Game game = new Game(selectRandomWord());
        games.put(username, game);
        System.out.println(game.getWord());
        return status(username);
    }

    public String giveUp(String username) {
        if (games.containsKey(username)) {
            String word = games.get(username).getWord();
            games.remove(username);
            return "You have given up! Gave over, you loose. The word was " + word;
        } else {
            return "You have no game, what a shame!";
        }
    }

    public String guess(String username, String guess) {
        if (games.containsKey(username)) {
            Game game = games.get(username);
            String word = game.guess(guess);
            if (game.haveWon()) {
                return "You win! The word was " + word + ". Score: " + game.remainingMistakes();
            }
            int remaining = game.remainingMistakes();
            if (remaining > 0)
                return word + " " + remaining;
            else {
                games.remove(username);
                return "Gave over, you loose. The word was " + game.getWord();
            }
        } else {
            return "You have no game, what a shame!";
        }
    }

    public String status(String username) {
        if (games.containsKey(username)) {
            Game game = games.get(username);
            return game.status() + " " + game.remainingMistakes();
        } else {
            return "You have no game, what a shame!";
        }
    }

    public void error(String username) {
        games.remove(username);
    }
}
