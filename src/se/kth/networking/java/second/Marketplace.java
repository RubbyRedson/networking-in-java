package se.kth.networking.java.second;

import se.kth.networking.java.first.Client;
import se.kth.networking.java.second.models.Item;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-11.
 */
public class Marketplace implements MarketplaceInterface {

    public static void main(String[] args){
        Marketplace server = new Marketplace();

        try {
            MarketplaceInterface marketplace = (MarketplaceInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("marketplace", marketplace);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void registerClient(Client client) {

    }

    @Override
    public void unregisterClient(Client client) {

    }

    @Override
    public void sellItem(Item item) {

    }

    @Override
    public void buyItem(Item item) {

    }

    @Override
    public List<Item> listItems() {
        return null;
    }
}
