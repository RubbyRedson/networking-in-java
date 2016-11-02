package se.kth.networking.java.first;

import java.io.*;
import java.net.Socket;

/**
 * Created by Nick on 11/2/2016.
 */
public class Client {
    Socket socket;
    private static String END = "End\n";

    public Client(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        if (args.length >= 2) {
            String host = args[0];
            int port = Integer.valueOf(args[1]);
            Client client = new Client(new Socket(host, port));
            client.start();
        }
    }

    public void start() throws IOException {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            handleClient(reader, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
        }
    }

    private void handleClient(BufferedReader reader, PrintWriter writer) throws IOException {
        writer.write("Potatoes\n");
        writer.flush();

        writer.write(END);
        writer.flush();

        String reply = reader.readLine();
        System.out.println(reply);
    }
}
