package se.kth.networking.java.first;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Nick on 11/4/2016.
 */
public class GameHandlerTest {
    GameHandler gameHandler;

    @Before
    public void setup() {
            gameHandler = new GameHandler();

    }
//
//    @After
//    public void shutdown() throws IOException {
//        server.shutdown();
//    }


    @Test
    public void testReadWords() throws IOException {
        assertEquals("Word size should be 25143", 25143, gameHandler.wordCount());
    }

    @Test
    public void testRandomWord() throws IOException {
        assertNotNull(gameHandler.selectRandomWord(), "Word should not be null");
    }

}
