package se.kth.networking.java.third.business;

import se.kth.networking.java.third.BusinessLogicException;
import se.kth.networking.java.third.ClientInterface;
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
    void loginClient(String userName, String password, ClientInterface client) throws RemoteException, NotBoundException, MalformedURLException, BusinessLogicException;
    void unregisterClient(int userId) throws RemoteException;
    void sellItem(int userId, Item item) throws RemoteException, BusinessLogicException;
    boolean buyItem(int userId, Item item) throws RemoteException, BusinessLogicException;
    List<StoreItem> listItems() throws RemoteException;
    void wishItem(int userId, Wish wish) throws RemoteException;
}