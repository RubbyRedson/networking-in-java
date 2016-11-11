package se.kth.networking.java.second;

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


    private static Registry lazyCreateRegistry(Marketplace server){
        Registry registry = null;
        MarketplaceInterface marketplace = null;
        try {
            marketplace = (MarketplaceInterface) UnicastRemoteObject.exportObject(server, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            registry = LocateRegistry.getRegistry(1099);
            registry.bind("marketplace", marketplace);
        } catch (RemoteException e) {
            try {
                registry = LocateRegistry.createRegistry(1099);
                registry.bind("marketplace", marketplace);
            } catch (RemoteException | AlreadyBoundException e1) {
                e1.printStackTrace();
            }
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        return registry;
    }

    public static void main(String[] args){
        Marketplace server = new Marketplace();
        Registry registry = lazyCreateRegistry(server);
        System.out.println("Grvy");

    }

    @Override
    public void registerClient(String userName, Client client) {
        System.out.println("register in marketplace " + userName);
    }

    @Override
    public void unregisterClient(String userName, Client client) {
        System.out.println("unregister in marketplace " + userName);

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
