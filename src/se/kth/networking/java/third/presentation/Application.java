package se.kth.networking.java.third.presentation;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import se.kth.id2212.ex2.bankrmi.RejectedException;
import se.kth.networking.java.third.BusinessLogicException;
import se.kth.networking.java.third.Client;
import se.kth.networking.java.third.business.Command;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by victoraxelsson on 2016-11-16.
 */
public class Application extends javafx.application.Application {
    private Client client;
    Label lbl;
    TextField firstTextField;
    TextField secondTextField;
    Label firstLabel;
    Label secondLabel;
    Client.CommandName commandName = Client.CommandName.register;
    ComboBox commands;

    public Application() throws RemoteException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        grid.add(scenetitle, 0, 0, 2, 1);

        firstLabel = new Label("Username");
        grid.add(firstLabel, 0, 2);

        secondLabel = new Label("Password");
        grid.add(secondLabel, 0, 3);

        firstTextField = new TextField();
        grid.add(firstTextField, 1, 2, 2, 1);
        secondTextField = new TextField();
        grid.add(secondTextField, 1, 3, 2, 1);

        lbl = new Label();
        lbl.setWrapText(true);
        ScrollPane sp = new ScrollPane();
        sp.setContent(lbl);
        grid.add(sp, 0, 0, 4, 1);

        //dropdown
        commands = new ComboBox();
        commands.setValue("Register");
        commands = notLoggedIn(commands);
        commands.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                if (t1 != null)
                    switch (t1) {
                        case "Register":
                            commandName = Client.CommandName.register;
                            twoVisible();
                            firstLabel.setText("Username");
                            secondLabel.setText("Password");
                            break;
                        case "Login":
                            commandName = Client.CommandName.login;
                            twoVisible();
                            firstLabel.setText("Username");
                            secondLabel.setText("Password");
                            break;
                        case "Logout":
                            commandName = Client.CommandName.logout;
                            noneVisible();
                            break;
                        case "Balance":
                            commandName = Client.CommandName.balance;
                            noneVisible();
                            break;
                        case "List":
                            commandName = Client.CommandName.list;
                            noneVisible();
                            break;
                        case "Inspect":
                            commandName = Client.CommandName.inspect;
                            noneVisible();
                            break;
                        case "Unregister":
                            commandName = Client.CommandName.unregister;
                            noneVisible();
                            break;
                        case "Wish":
                            commandName = Client.CommandName.wish;
                            twoVisible();
                            firstLabel.setText("Good Name");
                            secondLabel.setText("Price");
                            break;
                        case "Sell":
                            commandName = Client.CommandName.sell;
                            twoVisible();
                            firstLabel.setText("Good Name");
                            secondLabel.setText("Price");
                            break;
                        case "Buy":
                            commandName = Client.CommandName.buy;
                            oneVisible();
                            firstLabel.setText("Good Name");
                            break;
                        case "Deposit":
                            commandName = Client.CommandName.deposit;
                            oneVisible();
                            firstLabel.setText("Amount");
                            break;
                        case "Withdraw":
                            commandName = Client.CommandName.withdraw;
                            oneVisible();
                            firstLabel.setText("Amount");
                            break;
                    }
            }
        });
        grid.add(commands, 1, 1, 3, 1);

        Button btn = new Button("Execute");
        btn.setOnAction(event -> {
            String validate = validateForm();
            if (validate != null) {
                lbl.setText(lbl.getText() + "\n" + validate);
                return;
            }
            Command command = null;
            switch (commandName) {
                case register:
                    command = Command.createRegisterCommand(firstTextField.getText(), secondTextField.getText());
                    break;
                case unregister:
                    command = Command.createUnregisterCommand();
                    break;
                case login:
                    command = Command.createLoginCommand(firstTextField.getText(), secondTextField.getText());
                    break;
                case logout:
                    command = Command.createLogoutCommand();
                    break;
                case balance:
                    command = Command.createBalanceCommand();
                    break;
                case deposit:
                    command = Command.createDepositCommand(Float.parseFloat(firstTextField.getText()));
                    break;
                case withdraw:
                    command = Command.createWithdrawCommand(Float.parseFloat(firstTextField.getText()));
                    break;
                case inspect:
                    command = Command.createInspectCommand();
                    break;
                case list:
                    command = Command.createListCommand();
                    break;
                case buy:
                    command = Command.createBuyCommand(firstTextField.getText());
                    break;
                case sell:
                    command = Command.createSellCommand(firstTextField.getText(), Float.parseFloat(secondTextField.getText()));
                    break;
                case wish:
                    command = Command.createWishCommand(firstTextField.getText(), Float.parseFloat(secondTextField.getText()));
                    break;
            }
            try {
                client.execute(command);
                firstTextField.setText("");
                secondTextField.setText("");
                if (command.getCommandName() == Client.CommandName.login) {
                    commands = fullList(commands);
                } else if (command.getCommandName() == Client.CommandName.logout || command.getCommandName() == Client.CommandName.unregister) {
                    commands = notLoggedIn(commands);
                }
            } catch (Exception e) {
                println(e.getMessage());
            }
            //do stuff
        });
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 3, 1);

        Scene scene = new Scene(grid, 1024, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Marketplace application");
        primaryStage.show();
        client = new Client(this);
    }

    private String validateForm() {
        if (!firstTextField.isEditable() && !secondTextField.isEditable()) return null;
        if (firstTextField.isEditable()) {
            String first = firstTextField.getText();
            switch (firstLabel.getText()) {
                case "Username":
                    if (first == null || first.trim().isEmpty() || first.trim().length() < 3) {
                        return firstLabel.getText() + " must be at least 3 chars long";
                    } else {
                        return null;
                    }
                case "Good Name":
                    if (first == null || first.trim().isEmpty()) return firstLabel.getText() + " can't be empty";
                    break;
                case "Amount":
                    if (first == null || first.trim().isEmpty()) return firstLabel.getText() + " can't be empty";
                    try {
                        float parsed = Float.parseFloat(first);
                        if (parsed <= 0) return firstLabel.getText() + " must contain a positive number";
                    } catch (NumberFormatException e) {
                        return firstLabel.getText() + " must contain a number";
                    }
            }
        }

        if (secondTextField.isEditable()) {
            String second = secondTextField.getText();
            switch (secondLabel.getText()) {
                case "Password":
                    if (second == null || second.trim().isEmpty()) return secondLabel.getText() + " can't be empty";
                    if (second.length() < 8) return secondLabel.getText() + " must contain at least 8 characters";
                    break;
                case "Price":
                    if (second == null || second.trim().isEmpty()) return secondLabel.getText() + " can't be empty";
                    try {
                        float price = Float.parseFloat(second);
                        if (price <= 0) return secondLabel.getText() + " must contain a positive number";
                    } catch (NumberFormatException exc) {
                        return secondLabel.getText() + " must contain a number";
                    }
                    break;
            }
        }

        return null;
    }

    private void noneVisible() {
        firstTextField.setEditable(false);
        secondTextField.setEditable(false);
        firstTextField.setText("");
        secondTextField.setText("");
        firstTextField.setVisible(false);
        secondTextField.setVisible(false);
        firstLabel.setVisible(false);
        secondLabel.setVisible(false);
    }

    private void twoVisible() {
        firstTextField.setEditable(true);
        secondTextField.setEditable(true);
        firstTextField.setText("");
        secondTextField.setText("");
        firstTextField.setVisible(true);
        secondTextField.setVisible(true);
        firstLabel.setVisible(true);
        secondLabel.setVisible(true);
    }

    private void oneVisible() {
        firstTextField.setEditable(true);
        secondTextField.setEditable(false);
        firstTextField.setText("");
        secondTextField.setText("");
        firstTextField.setVisible(true);
        secondTextField.setVisible(false);
        firstLabel.setVisible(true);
        secondLabel.setVisible(false);
    }

    private ComboBox fullList(ComboBox comboBox) {
        comboBox.getItems().removeAll(comboBox.getItems());
        comboBox.getItems().addAll(
                "Deposit",
                "Withdraw",
                "Balance",
                "List",
                "Buy",
                "Sell",
                "Wish",
                "Inspect",
                "Logout"
        );
        comboBox.setValue("Deposit");
        commandName = Client.CommandName.deposit;
        return comboBox;
    }

    private ComboBox notLoggedIn(ComboBox comboBox) {
        comboBox.getItems().removeAll(comboBox.getItems());
        comboBox.getItems().addAll(
                "Register",
                "Login"
        );
        comboBox.setValue("Register");
        commandName = Client.CommandName.register;
        return comboBox;
    }

    public void println(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lbl.setText(lbl.getText() + "\n------------------\n" + s);
            }
        });
    }
}
