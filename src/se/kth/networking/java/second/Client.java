package se.kth.networking.java.second;

import se.kth.id2212.ex2.bankrmi.Account;
import se.kth.id2212.ex2.bankrmi.Bank;
import se.kth.id2212.ex2.bankrmi.RejectedException;
import se.kth.networking.java.second.models.Item;
import se.kth.networking.java.second.models.StoreItem;
import se.kth.networking.java.second.models.Wish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.StringTokenizer;


public class Client implements Serializable {
    private static final transient String USAGE = "java bankrmi.Client <bank_url>";
    private static final transient String DEFAULT_BANK_NAME = "Nordea";
    private transient Account account;
    private Bank bankobj;
    private transient MarketplaceInterface marketplaceobj;
    transient boolean runNotificationChecker = false;
    private String bankname;
    private String clientname;

    private enum CommandName {
        newAccount, getAccount, deleteAccount, deposit, withdraw, balance, list,    //Banking commands
        buy, sell, wish, register, unregister, inspect,                             //Marketplace commands
        quit, help;                                                                 //Utility commands

        static boolean isBankingCommand(CommandName command) {
            switch (command) {
                case newAccount:
                case getAccount:
                case deleteAccount:
                case deposit:
                case withdraw:
                case balance:
                case list:
                    return true;
            }
            return false;
        }

        static boolean isMarketplaceCommand(CommandName command) {
            switch (command) {
                case buy: // buy MyMarketPlaceName AccountName GoodName MaxGoodPrice
                case sell: // sell MyMarketPlaceName GoodName GoodPrice
                case wish: // wish MyMarketPlaceName GoodName MaxGoodPrice
                case register: // register MyMarketPlaceName
                case unregister: // unregister MyMarketPlaceName
                case inspect: // inspect MyMarketPlaceName
                    return true;
            }
            return false;
        }
    }

    private Client(String bankName) {
        this.bankname = bankName;
        try {
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            bankobj = (Bank) Naming.lookup(bankname);
            marketplaceobj = (MarketplaceInterface) Naming.lookup("marketplace"); //TODO constant
        } catch (Exception e) {


            System.out.println("The runtime failed: " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connected to bank: " + bankname);
    }

    private Client() {
        this(DEFAULT_BANK_NAME);
    }

    public String getClientname() {
        return clientname;
    }

    boolean buyCallback(Item item) {
        if (item == null) return false;
        try {
            if (bankobj.getAccount(clientname).getBalance() >= item.getPrice()) {
                bankobj.getAccount(clientname).withdraw(item.getPrice());
                return true;
            } else {
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (RejectedException e) {
            return false;
        }

    }

    private void run() {
        BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print(clientname + "@" + bankname + ">");
            try {
                String userInput = consoleIn.readLine();
                execute(parse(userInput));
            } catch (RejectedException re) {
                System.out.println(re);
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void runNotificationChecker(String clientName) {
        new Thread() {
            public void run() {
                while (runNotificationChecker) {
                    try {
                        List<String> notifications =  marketplaceobj.checkNotifications(clientname);
                        if (notifications.size() > 0) {
                            System.out.println("\n");
                            notifications.forEach(System.out::println);
                            System.out.print(clientname + "@" + bankname + ">");
                        }
                    } catch (RemoteException e) {
                        runNotificationChecker = false;
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private Command parse(String userInput) {
        if (userInput == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(userInput);
        if (tokenizer.countTokens() == 0) {
            return null;
        }

        CommandName commandName = null;
        String userName;
        float amount;
        int userInputTokenNo = 1;

        //Marketplace
        String goodName;
        float goodValue = 0;

        Command result = new Command();

        while (tokenizer.hasMoreTokens()) {
            switch (userInputTokenNo) {
                case 1:
                    try {
                        String commandNameString = tokenizer.nextToken();
                        commandName = CommandName.valueOf(CommandName.class, commandNameString);
                        result.setCommandName(commandName);
                    } catch (IllegalArgumentException commandDoesNotExist) {
                        System.out.println("Illegal command");
                        return null;
                    }
                    break;
                case 2:
                    userName = tokenizer.nextToken();
                    result.setUserName(userName);
                    break;
                case 3:
                    if (CommandName.isBankingCommand(commandName)) {
                        try {
                            amount = Float.parseFloat(tokenizer.nextToken());
                            result.setAmount(amount);
                        } catch (NumberFormatException e) {
                            System.out.println("Illegal amount");
                            return null;
                        }
                    } else if (CommandName.isMarketplaceCommand(commandName)) {
                        //if buy, sell, or wish here should be a name of the good, other commands should not get here
                        goodName = tokenizer.nextToken();
                        result.setGoodName(goodName);
                    }
                    break;
                case 4:
                    try {
                        goodValue = Float.parseFloat(tokenizer.nextToken());
                        result.setGoodValue(goodValue);
                    } catch (NumberFormatException e) {
                        System.out.println("Illegal amount");
                        return null;
                    }
                    break;
                default:
                    System.out.println("Illegal command");
                    return null;
            }
            userInputTokenNo++;
        }
        return result;
    }

    private void execute(Command command) throws RemoteException, RejectedException, MalformedURLException, NotBoundException {
        String validity = isCommandValid(command);
        if (validity != null) {
            System.out.println(validity);
            return;
        }

        switch (command.getCommandName()) {
            case list:
                try {
                    for (String accountHolder : bankobj.listAccounts()) {
                        System.out.println(accountHolder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                return;
            case quit:
                System.exit(0);
            case help:
                for (CommandName commandName : CommandName.values()) {
                    System.out.println(commandName);
                }
                return;

        }

        // all further commands require a name to be specified
        String userName = command.getUserName();
        if (userName == null) {
            userName = clientname;
        }

        if (userName == null) {
            System.out.println("name is not specified");
            return;
        }

        if (CommandName.isBankingCommand(command.getCommandName())) {
            switch (command.getCommandName()) {
                case newAccount:
                    clientname = userName;
                    bankobj.newAccount(userName);
                    return;
                case deleteAccount:
                    clientname = userName;
                    bankobj.deleteAccount(userName);
                    return;
            }

            // all further commands require a Account reference
            Account acc = bankobj.getAccount(userName);
            if (acc == null) {
                System.out.println("No account for " + userName);
                return;
            } else {
                account = acc;
                clientname = userName;
            }

            switch (command.getCommandName()) {
                case getAccount:
                    System.out.println(account);
                    break;
                case deposit:
                    account.deposit(command.getAmount());
                    break;
                case withdraw:
                    account.withdraw(command.getAmount());
                    break;
                case balance:
                    System.out.println("balance: $" + account.getBalance());
                    break;
                default:
                    System.out.println("Illegal command");
            }
        }
        else if (CommandName.isMarketplaceCommand(command.getCommandName())) {
            switch (command.getCommandName()) {
                case register:
                    System.out.println("Inside register " + command);
                    marketplaceobj.registerClient(command.getUserName(), this);
                    this.runNotificationChecker = true;
                    runNotificationChecker(command.getUserName());
                    return;
                case unregister:
                    System.out.println("Inside unregister " + command);
                    marketplaceobj.unregisterClient(command.getUserName(), this);
                    this.runNotificationChecker = false;
                    return;

                //these would require the client to be registered
                case inspect:
                    System.out.println("I have " + marketplaceobj.listItems().size() + " items in the store");
                    List<StoreItem> store = marketplaceobj.listItems();
                    for (StoreItem aStore : store) {
                        System.out.println(aStore.print());
                    }
                    return;
                case buy:
                    try {
                        if (marketplaceobj.buyItem(getClientname(), new Item(command.getGoodName(), Float.MIN_VALUE, this))) {
                            System.out.println("You bought a " + command.getGoodName());
                        } else {
                            System.out.println("You were not able to buy a " + command.getGoodName());
                        }
                    } catch (RemoteException e) {
                        System.out.println("You were not able to buy a " + command.getGoodName() + "\n" + e.getMessage());
                    }
                    return;
                case sell:
                    Item item = new Item(command.goodName, command.goodValue, this);
                    marketplaceobj.sellItem(command.getUserName(), item);
                    return;
                case wish:
                    Wish wish = new Wish(command.goodName, command.getGoodValue(), this);
                    marketplaceobj.wishItem(wish);

                    return;
            }
        }
    }

    private class Command {
        private String userName = null;
        private float amount = Float.MIN_VALUE;
        private CommandName commandName;

        //Marketplace
        private String goodName = null;
        private float goodValue = Float.MIN_VALUE;

        private String getUserName() {
            return userName;
        }

        private float getAmount() {
            return amount;
        }

        private CommandName getCommandName() {
            return commandName;
        }

        private Command() {
        }

        private Command(Client.CommandName commandName, String userName) {
            this.commandName = commandName;
            this.userName = userName;
        }

        void setUserName(String userName) {
            this.userName = userName;
        }

        void setCommandName(CommandName commandName) {
            this.commandName = commandName;
        }

        void setAmount(float amount) {
            if (CommandName.isBankingCommand(this.getCommandName()))
                this.amount = amount;
        }

        float getGoodValue() {
            if (CommandName.isMarketplaceCommand(this.getCommandName()))
                return goodValue;
            else return -1;
        }

        void setGoodValue(float goodValue) {
            if (CommandName.isMarketplaceCommand(this.getCommandName()))
                this.goodValue = goodValue;
        }

        String getGoodName() {
            if (CommandName.isMarketplaceCommand(this.getCommandName()))
                return goodName;
            else return "";
        }

        void setGoodName(String goodName) {
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

    public static void main(String[] args) {
        if ((args.length > 1) || (args.length > 0 && args[0].equals("-h"))) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bankName;
        if (args.length > 0) {
            bankName = args[0];
            new Client(bankName).run();
        } else {
            new Client().run();
        }
    }

    private static String isCommandValid(Command command) {
        if (command == null) return "Incorrect command, please try again";
        System.out.println(command);
        if (CommandName.isBankingCommand(command.getCommandName())) {
            switch (command.getCommandName()) {
                case newAccount:
                case deleteAccount:
                case getAccount:
                case balance:
                    if (command.getAmount() != Float.MIN_VALUE) return "Incorrect command, please try again";
                    return null;
                case deposit:
                case withdraw:
                    if (command.getAmount() < 0 || command.getAmount() == Float.MIN_VALUE) return "Incorrect parameters for the command '" + command.getCommandName() +"', " +
                            "you can only use positive values for this command, please try again";
            }
        }
        else if (CommandName.isMarketplaceCommand(command.getCommandName())) {
            if (command.getAmount() != Float.MIN_VALUE) return "Incorrect command, please try again";
            switch (command.getCommandName()) {
                case register:
                case unregister:
                case inspect:
                    if (command.getGoodName() != null) return "You should not specify parameters for the command "
                            + command.getCommandName() +", please try again";
                    if (command.getGoodValue() != Float.MIN_VALUE) return "You should not specify parameters for the command "
                            + command.getCommandName() +" , " + "please try again";
                    return null;
                case buy:
                    if (command.getGoodName() == null) return "There was no name of the good specified for the command "
                            + command.getCommandName() +", please try again";
                    if (command.getGoodValue() != Float.MIN_VALUE) return "Incorrect parameter for the command '" + command.getCommandName() +"', " +
                            "you can only use a good name for this command, please try again";
                    else return null;
                case wish:
                case sell:
                    if (command.getGoodName() == null) return "There was no name of the good specified for the command '"
                            + command.getCommandName() +"', " + "please try again";
                    if (command.getGoodValue() == Float.MIN_VALUE || command.getGoodValue() <= 0) return "There was no price of the good specified for the command '"
                            + command.getCommandName() +"', please try again";
                    else return null;
            }

        }
        return null;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientname='" + clientname + '\'' +
                '}';
    }
}
