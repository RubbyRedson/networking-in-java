package se.kth.networking.java.second.models;

import se.kth.networking.java.second.Client;

import java.io.Serializable;

/**
 * Created by victoraxelsson on 2016-11-12.
 */
public class Wish implements Serializable, StoreItem{

    private String name;
    private float price;
    private String currency;
    private Client wisher;

    public Wish(String name, float price, Client wisher){
        this(name, price, wisher, "SEK");
    }

    public Wish(String name, float price, Client wisher, String currency){
        this.name = name;
        this.price = price;
        this.wisher = wisher;
        this.currency = currency;
    }

    @Override
    public String print() {
        String msg = "";
        msg += wisher.getClientname() + " is wishing for the item: " + name + ", with a max price " + price + " " + currency;

        return msg;
    }

    public Client getWisher() {
        return wisher;
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
}