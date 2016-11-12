package se.kth.networking.java.second;

import se.kth.networking.java.second.models.Item;
import se.kth.networking.java.second.models.StoreItem;
import se.kth.networking.java.second.models.Wish;

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
    List<Wish> wishes;

    public Marketplace() {
        clients = new HashMap<>();
        store = new ArrayList<>();
        wishes = new ArrayList<>();
    }

    private static Registry lazyCreateRegistry(Marketplace server) {
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

    public static void main(String[] args) {
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
        List<Wish> toBeRemovedWishes = new ArrayList<>();

        //Remove all items that have the same client?
        //Start by collecting all items that this client is selling
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getSeller().getClientname().equalsIgnoreCase(userName)) {
                toBeRemoved.add(store.get(i));
            }
        }

        //Then collect all this client is buying
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getBuyer() != null && store.get(i).getBuyer().getClientname().equalsIgnoreCase(userName)) {
                toBeRemoved.add(store.get(i));
            }
        }

        //Then all wishes
        for (int i = 0; i < wishes.size(); i++){
            if(wishes.get(i).getWisher().getClientname().equalsIgnoreCase(userName)){
                toBeRemovedWishes.add(wishes.get(i));
            }
        }

        //Remove all items
        for (int i = 0; i < toBeRemoved.size(); i++) {
            store.remove(toBeRemoved.get(i));
        }

        //Remove all wishes
        for (int i = 0; i < toBeRemovedWishes.size(); i++){
            wishes.remove(toBeRemovedWishes.get(i));
        }
    }

    @Override
    public void sellItem(Item item) {

        store.add(item);

        //Check if someone is wishing for this
        for(int i = 0; i < wishes.size(); i++){
            if(wishes.get(i).getName().equalsIgnoreCase(item.getName())){

                //They should only be notified if the price is also fitting
                if(item.getPrice() <= wishes.get(i).getPrice()){
                    wishes.get(i).getWisher().someoneIsSellingYouWish(wishes.get(i), item);
                }
            }
        }
    }

    @Override
    public void wishItem(Wish wish) throws RemoteException {
        wishes.add(wish);
    }

    @Override
    public boolean buyItem(String buyer, Item item) {
        Client buyerClient = clients.get(buyer);
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getName().equalsIgnoreCase(item.getName()) && item.getBuyer() == null) {
                item = store.get(i);
                if (buyerClient != null && buyerClient.tryBuy(item)) {
                    item.setBuyer(buyerClient);
                    store.set(i, item);
                    item.getSeller().youHaveABuyer(item);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<StoreItem> listItems() {

        List<StoreItem> storeItems = new ArrayList<>();

        for(int i = 0; i < wishes.size(); i++){
            storeItems.add(wishes.get(i));
        }

        for(int i = 0; i < store.size(); i++){
            storeItems.add(store.get(i));
        }

        return storeItems;
    }
}
