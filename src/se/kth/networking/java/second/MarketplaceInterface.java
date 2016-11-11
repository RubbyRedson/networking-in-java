package se.kth.networking.java.second;

import se.kth.networking.java.first.Client;
import se.kth.networking.java.second.models.Item;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-11.
 */
public interface MarketplaceInterface extends Remote {
    void registerClient(Client client) throws RemoteException;
    void unregisterClient(Client client) throws RemoteException;
    void sellItem(Item item) throws RemoteException;
    void buyItem(Item item) throws RemoteException;
    List<Item> listItems() throws RemoteException;
}
