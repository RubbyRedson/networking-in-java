package se.kth.networking.java.first;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-04.
 */
public class Game {

    String word;
    int numberOfMistakes;
    List<String> guesses;

    public Game(String word){
        this.word = word;
        guesses = new ArrayList<>();
    }

    public String guess(String guess){

        if(guess.length() == 1){
            guesses.add(guess);
        }else{

        }

        return status();
    }

    private String handleWord(String guessedWord){
        return "";
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


}
