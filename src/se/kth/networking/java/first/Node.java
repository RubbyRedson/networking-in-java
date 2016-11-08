package se.kth.networking.java.first;

/**
 * Created by victoraxelsson on 2016-11-06.
 */
public class Node {

    private int id;
    private String ip;
    private int port;

    public Node(String ip, int port) {
        id = (ip + port).hashCode();
        this.ip = ip;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
