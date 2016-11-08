package se.kth.networking.java.first;


import java.io.IOException;
import java.net.Socket;

/**
 * Created by victoraxelsson on 2016-11-04.
 */
public class RingHandler {

    Node predecessor;
    Node successor;
    private int serverId;
    private int port;
    private String ip;

    public RingHandler(String ip, int port){
        serverId = ("127.0.0.1" + port).hashCode();
        this.ip = ip;
        this.port = port;
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
            if(between(n.getId(), predecessor.getId(), serverId)){
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

    public int getServerId() {
        return serverId;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
