package se.kth.networking.java.first;

import java.io.*;
import java.net.Socket;

/**
 * Created by Nick on 11/2/2016.
 */
public class ClientSocketHandler implements Runnable {
    private Socket client;
    private static String END = "End";
    private OnResponse<String> onResponse;

    public ClientSocketHandler(Socket client, OnResponse<String> onResponse) {
        this.client = client;
        this.onResponse = onResponse;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
            handle(reader, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
            if (reader != null) try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(BufferedReader reader, PrintWriter writer) throws IOException {
        String acc = "";
        String input = reader.readLine();


        //Read the message
        String str;
        while ((str = reader.readLine()) != null && !END.equalsIgnoreCase(input)) {
            acc += str;
            writer.write("Ack\n");
            writer.flush();
        }



        /*
        while (input != null && !END.equalsIgnoreCase(input)) {
            System.out.println(input);
            writer.write("Cabbage\n");
            writer.flush();
            input = reader.readLine();
            acc += input;
        }
        */

        onResponse.onResponse(acc);
        System.out.println("Finished communication with the client");
    }
}
