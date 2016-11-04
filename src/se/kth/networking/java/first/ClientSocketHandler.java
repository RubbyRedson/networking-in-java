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

        String username = client.getInetAddress().getHostName() + ":" + client.getPort();

        //Read the message
        String str;
        while ((str = reader.readLine()) != null && !END.equalsIgnoreCase(str)) {
            String res = onResponse.onResponse(str, username);
            writer.write(res + "\n");
            writer.flush();
        }

        System.out.println("Finished communication with the client");
    }
}
