package se.kth.networking.java.first;


import java.io.IOException;
import java.net.Socket;

/**
 * Created by victoraxelsson on 2016-11-04.
 */
public class RingHandler {

    Node predecessor;
    Node successor;
    Node self;
    private int port;
    private String ip;

    public RingHandler(String ip, int port){
        this.ip = ip;
        this.port = port;
        this.self = new Node(ip, port);
    }

    public void probe(){
        try {
            String msg = "probe:" + self.getIp() + "," + self.getPort() + "," + self.toString();
            Client c = new Client(successor.getAsSocket(), msg, null);
            c.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleProbe(String clientMessage, Node node){
        String[] parts = clientMessage.split(":");
        String[] args = parts[1].split(",");

        Node initiator = new Node(args[0], Integer.parseInt(args[1]));
        if(initiator.getId() == self.getId()){
            System.out.println("I got it back from the ring, " + clientMessage);
        }else{
            try {
                String msg = clientMessage + "," + self.toString();
                Client c = new Client(successor.getAsSocket(), msg, null);
                c.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendNotify(String rIp, int rPort){
        try {
            String msg = "notify:" + ip + "," + port;
            Client s = new Client(new Socket(rIp, rPort), msg, new OnResponse<String>() {
                @Override
                public String onResponse(String response, Node node) {

                    if(response.equalsIgnoreCase("accept")){
                        successor = node;
                    }else{
                        // Now what?
                    }

                    return null;
                }
            });
            s.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String notifyPredecessor(Node n){
        if(predecessor == null){

            // We don't have any predecessor, life is good
            predecessor = n;
            return "accept";
        }else{

            //This should be our new predecessor
            if(between(n.getId(), predecessor.getId(), self.getId())){
                predecessor = n;
                return "accept";
            }else{
                return "deny";
            }
        }
    }

    private boolean between(int key, int from, int to){
        if(from < to){
            return (key > from) && (key <= to);
        }else if(from > to){
            return (key > from) || (key <= to);
        }else {
            return true;
        }
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
