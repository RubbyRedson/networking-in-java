package se.kth.networking.java.third.business;


import se.kth.networking.java.third.BusinessLogicException;
import se.kth.networking.java.third.Client;
import se.kth.networking.java.third.ClientInterface;
import se.kth.networking.java.third.data.Database;
import se.kth.networking.java.third.data.IRepository;
import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.StoreItem;
import se.kth.networking.java.third.model.User;
import se.kth.networking.java.third.model.Wish;

import javax.security.auth.login.LoginException;
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
    private IRepository database;

    private Marketplace(IRepository repo) {
        clients = new HashMap<>();
        store = new ArrayList<>();
        wishes = new ArrayList<>();
        this.database = repo;
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
        Marketplace server = new Marketplace(new Database());
        Registry registry = lazyCreateRegistry(server);
    }

    @Override
    public synchronized void registerClient(String userName, String password, ClientInterface client) throws RemoteException, NotBoundException, MalformedURLException {

        User user = database.register(userName, password);
        client.userRegisterCallback(user);

        clients.put(userName, client);
        System.out.println("register in marketplace " + userName);
    }

    @Override
    public void loginClient(String userName, String password, ClientInterface client) throws RemoteException, NotBoundException, MalformedURLException, BusinessLogicException {
        User user = null;
        try {
            user = database.login(userName, password);
        } catch (LoginException e) {
            e.printStackTrace();
        }

        clients.put(userName, client);

        client.userLoginCallback(user);
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

        User user = database.getUserById(userId);
        if(!clients.containsKey(user.getUsername())){
            throw new RemoteException("No such client registered!\n" + user.getUsername());
        }

        database.saveItem(userId, item);

        List<Wish> wishes =  database.getAllWishes();

        for (int i = 0; i < wishes.size(); i++){
            Wish wish = wishes.get(i);
            if (item.getPrice() <= wish.getPrice()) {

                //We have a wisher!
                getClientById(wish.getWisher()).print("An object that is in your " +
                        "wishlist is being sold A " + item.getName() + " for " + item.getPrice());
            }
        }

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

    private ClientInterface getClientById(int userId){
        ClientInterface client = null;
        User user = database.getUserById(userId);
        if (user != null){
            ClientInterface currClient = null;
            String name = null;
            try {
                currClient = clients.get(user.getUsername());
                name = currClient.getClientname();

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if(name != null && name.equals(user.getUsername())){
                client = currClient;
            }
        }

        return client;
    }

    @Override
    public synchronized void wishItem(int userId, Wish wish) throws RemoteException {

        database.saveWish(userId, wish);

        List<Item> allItems = database.getAllItems();

        for (Item item : allItems) {
            if (item.getPrice() <= wish.getPrice()) {


                ClientInterface client = getClientById(userId);

                client.print("An object that is in your " +
                        "wishlist is being sold! A " + item.getName() + " for " + item.getPrice());
            }
        }
    }

    @Override
    public synchronized boolean buyItem(int userIrd, Item item) throws RemoteException {

        User buyer = database.getUserById(userIrd);


        if(!clients.containsKey(buyer.getUsername())){
            throw new RemoteException("No such client registered!\n" + buyer);
        }

        Item dbItem = database.getItemByName(item.getName());

        ClientInterface buyerClient = getClientById(userIrd);
        ClientInterface sellerClient = getClientById(dbItem.getSeller());


        if (dbItem.getBuyer() <= 0) {
            if (buyerClient != null && buyerClient.buyCallback(item)) {
                dbItem.setBuyer(buyer.getId());

                //save to db
                database.updateItem(dbItem);

                sellerClient.sellCallback(dbItem);
                sellerClient.print(dbItem.getName() +
                        " was bought, you earned " + dbItem.getPrice());
                removeFulfilledWishes(buyerClient.getClientname(), dbItem);


                System.out.println("Item was bought: " + item.print());
                return true;
            } else {
                buyerClient.print("Insufficient funds");
                return false;
            }
        }


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

        User user = database.getUserByUsername(client);
        database.deleteWish(user.getId(), purchase.getName());

        /*
        System.out.println("NOt implemented");

        List<Wish> newWishes = new ArrayList<>();
        for (Wish wish : wishes) {

            ClientInterface wisher = getClientById(wish.getWisher());

            if (!wisher.getClientname().equals(client) || !wish.getName().equalsIgnoreCase(purchase.getName())) {
                newWishes.add(wish);
            }
        }
        wishes = newWishes;
        */
    }

    @Override
    public synchronized List<StoreItem> listItems() {
        List<StoreItem> storeItems = new ArrayList<>();
        storeItems.addAll(database.getAllItems());
        storeItems.addAll(database.getAllWishes());

        return storeItems;
    }
}
