package se.kth.networking.java.third;

import se.kth.networking.java.third.model.Item;

import se.kth.networking.java.third.model.StoreItem;
import se.kth.networking.java.third.model.Wish;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-16.
 */
public interface MarketplaceInterface extends Remote {
    void registerClient(String userName, String password, ClientInterface client) throws RemoteException, NotBoundException, MalformedURLException;
    void unregisterClient(int userId) throws RemoteException;
    void sellItem(int userId, Item item) throws RemoteException;
    boolean buyItem(int userId, Item item) throws RemoteException;
    List<StoreItem> listItems() throws RemoteException;
    void wishItem(int userId, Wish wish) throws RemoteException;
}