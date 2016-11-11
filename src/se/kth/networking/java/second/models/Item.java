package se.kth.networking.java.second.models;

/**
 * Created by victoraxelsson on 2016-11-11.
 */
public class Item {
    String name;
    float price;
    String currency;

    public Item(String name, float price){
        this(name, price, "SEK");
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
}
