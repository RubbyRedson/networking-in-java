package se.kth.networking.java.second;

import se.kth.networking.java.second.models.Item;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Nick on 11/15/2016.
 */
public interface ClientInterface extends Remote {
    void print(String s) throws RemoteException;
    String getClientname() throws RemoteException;

    boolean buyCallback(Item item) throws RemoteException;
    void sellCallback(Item item) throws RemoteException;
}
