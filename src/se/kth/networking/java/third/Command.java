package se.kth.networking.java.third;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public class Command {
    private String userName = null;
    private float amount = Float.MIN_VALUE;
    private Client.CommandName commandName;

    //Marketplace
    private String goodName = null;
    private float goodValue = Float.MIN_VALUE;

    public String getUserName() {
        return userName;
    }

    public float getAmount() {
        return amount;
    }

    public Client.CommandName getCommandName() {
        return commandName;
    }

    public Command() {
    }

    private Command(Client.CommandName commandName, String userName) {
        this.commandName = commandName;
        this.userName = userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return "Command{" +
                "userName='" + userName + '\'' +
                ", amount=" + amount +
                ", commandName=" + commandName +
                ", goodName='" + goodName + '\'' +
                ", goodValue=" + goodValue +
                '}';
    }
}