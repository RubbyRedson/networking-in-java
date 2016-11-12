package se.kth.networking.java.second.models;

import se.kth.networking.java.second.Client;

import java.io.Serializable;

/**
 * Created by victoraxelsson on 2016-11-11.
 */
public class Item implements Serializable, StoreItem {
    private String name;
    private float price;
    private String currency;
    private  Client seller;
    private  Client buyer;

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

    public Client getBuyer() {
        return buyer;
    }

    public void setBuyer(Client buyer) {
        this.buyer = buyer;
    }

    public Client getSeller() {
        return seller;
    }

    @Override
    public String print() {
        String msg = "";
        msg += name + ", " + price + " " + currency + ", seller:" + seller.getClientname();

        if(buyer != null){
            msg += ", Buyer:" + buyer.getClientname();
        }else{
            msg += ", Buyer: currently up for grabs";
        }

        return msg;
    }
}
