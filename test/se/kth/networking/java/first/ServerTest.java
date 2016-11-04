package se.kth.networking.java.first;

import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Nick on 11/4/2016.
 */
public class ServerTest {
    Server server;

    @Before
    public void setup() throws IOException {
        try {
            server = new Server(9090);
//            server.readWords();
        } catch (IOException e) {
            server = new Server(9091);
//            server.readWords();
        }
    }

    @After
    public void shutdown() throws IOException {
        server.shutdown();
    }

//    @Test
//    public void testReadWords() throws IOException {
//        assertEquals("Word size should be 25143", 25143, server.wordCount());
//    }
//
//    @Test
//    public void testRandomWord() throws IOException {
//        assertNotNull(server.selectRandomWord(), "Word should not be null");
//    }

}
