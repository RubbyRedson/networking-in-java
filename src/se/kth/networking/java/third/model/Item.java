package se.kth.networking.java.third.model;

import se.kth.networking.java.second.Client;
import se.kth.networking.java.second.ClientInterface;
import se.kth.networking.java.second.models.StoreItem;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public class Item implements Serializable, StoreItem {
    private String name;
    private float price;
    private String currency;
    private ClientInterface seller;
    private ClientInterface buyer;

    public Item(String name, float price, Client seller){
        this(name, price, "SEK");

        this.seller = seller;
    }

    public Item(String name, float price, String currency){
        this.name = name;
        this.price = price;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public ClientInterface getBuyer() {
        return buyer;
    }

    public void setBuyer(ClientInterface buyer) {
        this.buyer = buyer;
    }

    public ClientInterface getSeller() {
        return seller;
    }

    @Override
    public String print() throws RemoteException {
        String msg = "";
        msg += name + ", " + price + " " + currency + ", seller:" + seller.getClientname();

        if(buyer != null){
            msg += ", Buyer:" + buyer.getClientname();
        }else{
            msg += ", Buyer: currently up for grabs";
        }

        return msg;
    }

    public void setSeller(ClientInterface seller) {
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", seller=" + seller +
                ", buyer=" + buyer +
                '}';
    }
}

