package se.kth.networking.java.third;

import se.kth.networking.java.third.model.Item;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ClientInterface extends Remote {
    void print(String s) throws RemoteException;
    String getClientname() throws RemoteException;

    boolean buyCallback(Item item) throws RemoteException;
    void sellCallback(Item item) throws RemoteException;
}