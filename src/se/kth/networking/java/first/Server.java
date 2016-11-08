package se.kth.networking.java.first;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick on 11/2/2016.
 */
public class Server {

    private RingHandler ringHandler;
    private ClientAcceptor acceptor;


    public Server(int port) {

        //Please fix dynamic ip
        ringHandler = new RingHandler("127.0.0.1", port);

        try {
            acceptor = new ClientAcceptor(port, new OnResponse<String>(){
                @Override
                public String onResponse(String response, Node node) {
                    return handleMessage(response, node);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidMessage(String msg){
        return msg != null && !msg.trim().equalsIgnoreCase(":") && msg.contains(":");
    }

    private String handleMessage(String clientMessage, Node node){

        String response = "Bad request";

        if(isValidMessage(clientMessage)){
            String[] parts = clientMessage.split(":");
            switch (parts[0]){
                case "notify":
                    response = ringHandler.notifyPredecessor(node);
                    break;
                case "probe":
                    ringHandler.handleProbe(clientMessage, node);
                    break;
            }
        }

        return response;
    }

    public void sendNotify(String ip, int port){
        ringHandler.sendNotify(ip, port);
    }

    public void probe(){
        ringHandler.probe();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.valueOf(args[0]);

        Server server1 = new Server(5050);
        server1.start();

        Server server2 = new Server(6060);
        server2.start();

        Thread.sleep(3000);

        Server server3 = new Server(7070);
        server3.start();

        server1.sendNotify(server2.getRingHandler().getIp(), server2.getRingHandler().getPort());
        server2.sendNotify(server3.getRingHandler().getIp(), server3.getRingHandler().getPort());
        server3.sendNotify(server1.getRingHandler().getIp(), server1.getRingHandler().getPort());

        Thread.sleep(3000);

        server1.probe();
        System.out.println("done");
    }

    private void start() {
        acceptor.start();
    }

    public RingHandler getRingHandler() {
        return ringHandler;
    }
}
