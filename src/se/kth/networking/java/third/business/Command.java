package se.kth.networking.java.third.business;

import se.kth.networking.java.third.Client;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public class Command {
    private String username = null;
    private String password = null;
    private float amount = Float.MIN_VALUE;
    private Client.CommandName commandName;

    //Marketplace
    private String goodName = null;
    private float goodValue = Float.MIN_VALUE;

    public float getAmount() {
        return amount;
    }

    public Client.CommandName getCommandName() {
        return commandName;
    }

    public Command() {
    }

    private Command(Client.CommandName commandName) {
        this.commandName = commandName;
    }

    public void setCommandName(Client.CommandName commandName) {
        this.commandName = commandName;
    }

    public void setAmount(float amount) {
        if (Client.CommandName.isBankingCommand(this.getCommandName()))
            this.amount = amount;
    }

    public float getGoodValue() {
        if (Client.CommandName.isMarketplaceCommand(this.getCommandName()))
            return goodValue;
        else return -1;
    }

    public void setGoodValue(float goodValue) {
        if (Client.CommandName.isMarketplaceCommand(this.getCommandName()))
            this.goodValue = goodValue;
    }

    public String getGoodName() {
        if (Client.CommandName.isMarketplaceCommand(this.getCommandName()))
            return goodName;
        else return "";
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Command{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", amount=" + amount +
                ", commandName=" + commandName +
                ", goodName='" + goodName + '\'' +
                ", goodValue=" + goodValue +
                '}';
    }
}