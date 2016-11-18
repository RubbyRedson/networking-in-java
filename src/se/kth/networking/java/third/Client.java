package se.kth.networking.java.third;


import se.kth.id2212.ex2.bankrmi.Account;
import se.kth.id2212.ex2.bankrmi.Bank;
import se.kth.id2212.ex2.bankrmi.RejectedException;
import se.kth.networking.java.third.business.Command;
import se.kth.networking.java.third.business.MarketplaceInterface;
import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.StoreItem;
import se.kth.networking.java.third.model.User;
import se.kth.networking.java.third.model.Wish;
import se.kth.networking.java.third.presentation.Application;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
@SuppressWarnings("Duplicates")
public class Client extends UnicastRemoteObject implements ClientInterface, User {
    private static final String USAGE = "java bankrmi.Client <bank_url>";
    private static final String DEFAULT_BANK_NAME = "Nordea";
    private Account account;
    private Bank bankobj;
    private MarketplaceInterface marketplaceobj;
    private String bankname;
    private int id;
    private String username;
    private String password;
    private Application app;

    public enum CommandName {
        newAccount, getAccount, deleteAccount, deposit, withdraw, balance, list,    //Banking commands
        buy, sell, wish, register, unregister, inspect,                             //Marketplace commands
        login, logout, quit, help;                                                  //Utility commands

        public static boolean isBankingCommand(CommandName command) {
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

        public static boolean isMarketplaceCommand(CommandName command) {
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

    private Client(String bankName) throws RemoteException {
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

    public Client(Application app) throws RemoteException {
        this(DEFAULT_BANK_NAME);
        this.app = app;
    }

    public boolean buyCallback(Item item) {
        if (item == null) return false;
        try {
            if (bankobj.getAccount(getUsername()).getBalance() >= item.getPrice()) {
                bankobj.getAccount(getUsername()).withdraw(item.getPrice());
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

    @Override
    public void sellCallback(Item item) throws RemoteException {
        try {
            bankobj.getAccount(getUsername()).deposit(item.getPrice());
        } catch (RejectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userLoginCallback(User user) throws RemoteException, BusinessLogicException {
        if (user == null) throw new BusinessLogicException("Login failed!");
        setId(user.getId());
    }

    @Override
    public void userRegisterCallback(User user) throws RemoteException {
        setId(user.getId());
    }

    public void execute(Command command) throws RemoteException, RejectedException, MalformedURLException, NotBoundException, BusinessLogicException {
        print("Execute " + command.getCommandName() + " command");
        switch (command.getCommandName()) {
            case list:
                try {
                    for (String accountHolder : bankobj.listAccounts()) {
                        this.print(accountHolder);
                    }
                } catch (Exception e) {
                    this.print("An error occurred: " + e.getMessage());
                    return;
                }
                return;
            case quit:
                System.exit(0);
            case help:
                for (CommandName commandName : CommandName.values()) {
                    this.print(commandName.toString());
                }
                return;
            case login:
                setUsername(command.getUsername());
                setPassword(command.getPassword());
                marketplaceobj.loginClient(command.getUsername(), command.getPassword(), this);
                if (bankobj.getAccount(getUsername()) == null) bankobj.newAccount(command.getUsername());
                else account = bankobj.getAccount(getUsername());
                break;

            case logout:
                setId(-1);
                setUsername(null);
                setPassword(null);
                account = null;
        }

        // all further commands require a name to be specified
        String userName = command.getUsername();

        if (userName == null) {
            username = getUsername();
        }

        if (CommandName.isBankingCommand(command.getCommandName())) {
            switch (command.getCommandName()) {
                case newAccount:
                    bankobj.newAccount(userName);
                    return;
                case deleteAccount:
                    bankobj.deleteAccount(userName);
                    return;
            }

            // all further commands require a Account reference
            Account acc = bankobj.getAccount(getUsername());
            if (acc == null) {
                this.print("No account for " + getUsername());
                return;
            } else {
                account = acc;
            }

            switch (command.getCommandName()) {
                case getAccount:
                    this.print(account.toString());
                    break;
                case deposit:
                    account.deposit(command.getAmount());
                    break;
                case withdraw:
                    account.withdraw(command.getAmount());
                    break;
                case balance:
                    this.print("Balance: $" + account.getBalance());
                    break;
                default:
                    this.print("Illegal command");
            }
        } else if (CommandName.isMarketplaceCommand(command.getCommandName())) {
            switch (command.getCommandName()) {
                case register:
                    marketplaceobj.registerClient(command.getUsername(), command.getPassword(), this);
                    return;
                case unregister:
                    marketplaceobj.unregisterClient(getId());
                    setId(-1);
                    setUsername(null);
                    setPassword(null);
                    return;

                //these would require the client to be registered
                case inspect:
                    this.print("I have " + marketplaceobj.listItems().size() + " items in the store");
                    List<StoreItem> store = marketplaceobj.listItems();
                    for (StoreItem aStore : store) {
                        this.print(aStore.print());
                    }
                    return;
                case buy:
                    marketplaceobj.buyItem(getId(), new Item(command.getGoodName(), Float.MIN_VALUE, getId()));
                    return;
                case sell:
                    Item item = new Item(command.getGoodName(), command.getGoodValue(), getId());
                    marketplaceobj.sellItem(getId(), item);
                    print("You are now selling " + item.getName() + " for " + item.getPrice());
                    return;
                case wish:
                    Wish wish = new Wish(command.getGoodName(), command.getGoodValue(), this.getId());
                    marketplaceobj.wishItem(getId(), wish);
                    return;
            }
        }
    }

    @Override
    public void print(String s) throws RemoteException {
        app.println(s);
    }

    @Override
    public String getClientname() throws RemoteException {
        return getUsername();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
