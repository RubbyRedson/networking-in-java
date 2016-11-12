package se.kth.networking.java.second;

import se.kth.networking.java.second.models.Item;
import se.kth.networking.java.second.models.StoreItem;
import se.kth.networking.java.second.models.Wish;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-11.
 */
public interface MarketplaceInterface extends Remote {
    void registerClient(String userName, Client client) throws RemoteException;
    void unregisterClient(String userName, Client client) throws RemoteException;
    void sellItem(Item item) throws RemoteException;
    boolean buyItem(String buyer, Item item) throws RemoteException;
    List<Item> listItems() throws RemoteException;
}
