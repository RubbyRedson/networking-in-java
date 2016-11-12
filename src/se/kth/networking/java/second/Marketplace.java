package se.kth.networking.java.second;

import se.kth.networking.java.second.models.Item;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-11.
 */
public class Marketplace implements MarketplaceInterface {

    HashMap<String, Client> clients;
    List<Item> store;

    public Marketplace(){
        clients = new HashMap<>();
        store = new ArrayList<>();
    }

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
        clients.put(userName, client);

        System.out.println("register in marketplace " + userName);
    }

    @Override
    public void unregisterClient(String userName, Client client) {
        System.out.println("unregister in marketplace " + userName);

        //Remove client from all registered clients
        clients.remove(userName);

        List<Item> toBeRemoved = new ArrayList<>();

        //Remove all items that have the same client?
        //Start by collecting all items that this client is selling
        for (int i = 0; i < store.size(); i++){
            if(store.get(i).getSeller().getClientname().equalsIgnoreCase(userName)){
                toBeRemoved.add(store.get(i));
            }
        }

        //Then collect all this client is buying
        for (int i = 0; i < store.size(); i++){
            if(store.get(i).getBuyer() != null &&  store.get(i).getBuyer().getClientname().equalsIgnoreCase(userName)){
                toBeRemoved.add(store.get(i));
            }
        }

        //Remove all items
        for (int i = 0; i < toBeRemoved.size(); i++){
            store.remove(toBeRemoved.get(i));
        }
    }

    @Override
    public void sellItem(Item item) {
        store.add(item);
    }

    @Override
    public void buyItem(String buyer, Item item) {

        for (int i = 0; i < store.size(); i++){
            if(store.get(i).getName().equalsIgnoreCase(item.getName())){
                item = store.get(i);
                item.setBuyer(clients.get(buyer));
                store.set(i, item);
            }
        }

        item.getSeller().youHaveABuyer(item);
    }

    @Override
    public List<Item> listItems() {
        return store;
    }
}
