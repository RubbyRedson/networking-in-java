package se.kth.networking.java.first;

import java.io.*;
import java.net.Socket;

/**
 * Created by Nick on 11/2/2016.
 */
public class Client {
    private Socket socket;
    private static String ENDLINE = "\n";
    private static String END = "End";

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
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String input = keyboardInput(consoleReader);

        do {
            writer.write(input + ENDLINE);
            writer.flush();
            String reply = reader.readLine();
            System.out.println(reply);
            input = keyboardInput(consoleReader);
        } while (!(input == null || END.equalsIgnoreCase(input.trim())));

        writer.write(END + ENDLINE);
        writer.flush();

    }

    private String keyboardInput(BufferedReader consoleReader) throws IOException {
        System.out.print("Waiting for input: ");
        return consoleReader.readLine();
    }
}
