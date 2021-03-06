package se.kth.networking.java.first;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-04.
 */
public class Game {

    private String word;
    private int numberOfMistakes;
    private List<String> guesses;
    private static int MAX_MISTAKES = 10;

    public Game(String word){
        this.word = word;
        guesses = new ArrayList<>();
    }

    public String guess(String guess){
        if(guess.length() == 1){
            guesses.add(guess);

            if(!word.contains(guess)){
                numberOfMistakes ++;
            }

            return status();
        }else{
            return handleWord(guess);
        }
    }

    public boolean haveWon(){
        return !status().contains("-");
    }

    private String handleWord(String guessedWord){
        if(guessedWord.equalsIgnoreCase(word)){
            for (int i = 0; i < guessedWord.length(); i++){
                guesses.add(guessedWord.charAt(i) + "");
            }
        }else{
            numberOfMistakes++;
        }

        return status();
    }

    public String status(){
        String acc = "";

        for(int i = 0; i < word.length(); i++){
            String letter = word.charAt(i) + "";

            if(guesses.contains(letter)){
                acc += letter;
            }else{
                acc += "-";
            }
        }

        return acc;
    }

    public int getNumberOfMistakes() {
        return numberOfMistakes;
    }

    public int remainingMistakes() {
        return MAX_MISTAKES - getNumberOfMistakes();
    }

    public String getWord() {
        return word;
    }
}
