package se.kth.networking.java.second;

import se.kth.id2212.ex2.bankrmi.Account;
import se.kth.id2212.ex2.bankrmi.Bank;
import se.kth.id2212.ex2.bankrmi.RejectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.StringTokenizer;

public class Client {
    private static final String USAGE = "java bankrmi.Client <bank_url>";
    private static final String DEFAULT_BANK_NAME = "Nordea";
    Account account;
    Bank bankobj;
    private String bankname;
    String clientname;

    static enum CommandName {
        newAccount, getAccount, deleteAccount, deposit, withdraw, balance, list,    //Banking commands
        buy, sell, wish, register, unregsiter, inspect,                             //Marketplace commands
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
                case buy:
                case sell:
                case wish:
                case register:
                case unregsiter:
                case inspect:
                    return true;
            }
            return false;
        }
    }

    ;

    public Client(String bankName) {
        this.bankname = bankName;
        try {
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            bankobj = (Bank) Naming.lookup(bankname);
        } catch (Exception e) {
            System.out.println("The runtime failed: " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connected to bank: " + bankname);
    }

    public Client() {
        this(DEFAULT_BANK_NAME);
    }

    public void run() {
        BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print(clientname + "@" + bankname + ">");
            try {
                String userInput = consoleIn.readLine();
                execute(parse(userInput));
            } catch (RejectedException re) {
                System.out.println(re);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        String userName = null;
        float amount = 0;
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

    void execute(Command command) throws RemoteException, RejectedException {
        if (command == null) {
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
                    //TODO call to marketplaceobj register
                    return;
                case unregsiter:
                    //TODO call to marketplaceobj register
                    return;
            }

        }
    }

    private class Command {
        private String userName;
        private float amount;
        private CommandName commandName;

        //Marketplace
        private String goodName;
        private float goodValue;

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

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setCommandName(CommandName commandName) {
            this.commandName = commandName;
        }

        public void setAmount(float amount) {
            if (CommandName.isBankingCommand(this.getCommandName()))
                this.amount = amount;
        }

        public float getGoodValue() {
            if (CommandName.isMarketplaceCommand(this.getCommandName()))
                return goodValue;
            else return -1;
        }

        public void setGoodValue(float goodValue) {
            if (CommandName.isMarketplaceCommand(this.getCommandName()))
                this.goodValue = goodValue;
        }

        public String getGoodName() {
            if (CommandName.isMarketplaceCommand(this.getCommandName()))
                return goodName;
            else return "";
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
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
}
