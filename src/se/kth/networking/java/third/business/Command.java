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

    public static Command createLoginCommand(String username, String password) {
        Command result = new Command();
        result.setCommandName(Client.CommandName.login);
        result.setUsername(username);
        result.setPassword(password);
        return result;
    }

    public static Command createLogoutCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.logout);
        return result;
    }

    public static Command createListCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.list);
        return result;
    }

    public static Command createHelpCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.help);
        return result;
    }

    public static Command createQuitCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.quit);
        return result;
    }

    public static Command createRegisterCommand(String username, String password) {
        Command result = new Command();
        result.setUsername(username);
        result.setPassword(password);
        result.setCommandName(Client.CommandName.register);
        return result;
    }

    public static Command createUnregisterCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.unregister);
        return result;
    }

    public static Command createInspectCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.inspect);
        return result;
    }

    public static Command createNewAccountCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.newAccount);
        return result;
    }

    public static Command createDeleteAccountCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.deleteAccount);
        return result;
    }

    public static Command createBalanceCommand() {
        Command result = new Command();
        result.setCommandName(Client.CommandName.balance);
        return result;
    }

    public static Command createDepositCommand(float amount) {
        Command result = new Command();
        result.setCommandName(Client.CommandName.deposit);
        result.setAmount(amount);
        return result;
    }

    public static Command createWithdrawCommand(float amount) {
        Command result = new Command();
        result.setCommandName(Client.CommandName.withdraw);
        result.setAmount(amount);
        return result;
    }

    public static Command createBuyCommand(String goodName) {
        Command result = new Command();
        result.setCommandName(Client.CommandName.buy);
        result.setGoodName(goodName);
        return result;
    }

    public static Command createSellCommand(String goodName, float goodValue) {
        Command result = new Command();
        result.setCommandName(Client.CommandName.sell);
        result.setGoodName(goodName);
        result.setGoodValue(goodValue);
        return result;
    }

    public static Command createWishCommand(String goodName, float goodValue) {
        Command result = new Command();
        result.setCommandName(Client.CommandName.wish);
        result.setGoodName(goodName);
        result.setGoodValue(goodValue);
        return result;
    }

    private Command() {
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