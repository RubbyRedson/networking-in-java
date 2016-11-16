package se.kth.networking.java.third.business;


import se.kth.networking.java.third.ClientInterface;
import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.StoreItem;
import se.kth.networking.java.third.model.Wish;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-16.
 */
public class Marketplace implements MarketplaceInterface {

    private HashMap<String, ClientInterface> clients;
    private List<Item> store;
    private List<Wish> wishes;

    private Marketplace() {
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
    }

    @Override
    public synchronized void registerClient(String userName, String password, ClientInterface client) throws RemoteException, NotBoundException, MalformedURLException {
        clients.put(userName, client);
        System.out.println("register in marketplace " + userName);
    }

    @Override
    public synchronized void unregisterClient(int userId) throws RemoteException {
        System.out.println("unregister in marketplace " + userId);

        //Remove client from all registered clients
        //clients.remove(userName);

        List<Item> toBeRemoved = new ArrayList<>();
        List<Wish> toBeRemovedWishes = new ArrayList<>();

        /*
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
        for (int i = 0; i < wishes.size(); i++) {
            if (wishes.get(i).getWisher().getClientname().equalsIgnoreCase(userName)) {
                toBeRemovedWishes.add(wishes.get(i));
            }
        }

        //Remove all items
        for (int i = 0; i < toBeRemoved.size(); i++) {
            store.remove(toBeRemoved.get(i));
        }

        //Remove all wishes
        for (int i = 0; i < toBeRemovedWishes.size(); i++) {
            wishes.remove(toBeRemovedWishes.get(i));
        }
        */
    }

    @Override
    public synchronized void sellItem(int userId, Item item) throws RemoteException {
        /*
        if (!clients.containsKey(username))
            throw new RemoteException("No such client registered!\n" + username);
        item.setSeller(clients.get(username));
        store.add(item);
        System.out.println("Item for sale added: " + item.print());

        //Check if someone is wishing for this
        for (int i = 0; i < wishes.size(); i++) {
            if (wishes.get(i).getName().equalsIgnoreCase(item.getName())) {

                //They should only be notified if the price is also fitting
                if (item.getPrice() <= wishes.get(i).getPrice()) {
                    wishes.get(i).getWisher().print("An object that is in your " +
                            "wishlist is being sold A " + item.getName() + " for " + item.getPrice());
                }
            }
        }
        */

    }

    @Override
    public synchronized void wishItem(int userId, Wish wish) throws RemoteException {
        wishes.add(wish);
        System.out.println("Wish added: " + wish.print());
        for (Item item : store) {
            if (item.getPrice() <= wish.getPrice()) {
                wish.getWisher().print("An object that is in your " +
                        "wishlist is being sold! A " + item.getName() + " for " + item.getPrice());
            }
        }
    }

    @Override
    public synchronized boolean buyItem(int userIrd, Item item) throws RemoteException {
        /*
        if (!clients.containsKey(buyer))
            throw new RemoteException("No such client registered!\n" + buyer);
        ClientInterface buyerClient = clients.get(buyer);
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getName().equalsIgnoreCase(item.getName())) {
                item = store.get(i);
                if (item.getBuyer() == null) {
                    if (buyerClient != null && buyerClient.buyCallback(item)) {
                        item.setBuyer(buyerClient);
                        store.set(i, item);
                        item.getSeller().sellCallback(item);
                        item.getSeller().print(item.getName() +
                                " was bought, you earned " + item.getPrice());
                        item.getBuyer().print("You bought a " + item.getName() +
                                " for " + item.getPrice());
                        removeFulfilledWishes(buyerClient.getClientname(), item);
                        System.out.println("Item was bought: " + item.print());
                        return true;
                    } else {
                        buyerClient.print("Insufficient funds");
                        return false;
                    }
                }
            }
        }
        buyerClient.print("No such item found for sale!");
        */
        return false;
    }

    private void removeFulfilledWishes(String client, Item purchase) throws RemoteException {
        List<Wish> newWishes = new ArrayList<>();
        for (Wish wish : wishes) {
            if (!wish.getWisher().getClientname().equals(client) || !wish.getName().equalsIgnoreCase(purchase.getName())) {
                newWishes.add(wish);
            }
        }
        wishes = newWishes;
    }

    @Override
    public synchronized List<StoreItem> listItems() {

        List<StoreItem> storeItems = new ArrayList<>();

        for (int i = 0; i < wishes.size(); i++) {
            storeItems.add(wishes.get(i));
        }

        for (int i = 0; i < store.size(); i++) {
            storeItems.add(store.get(i));
        }

        return storeItems;
    }
}
