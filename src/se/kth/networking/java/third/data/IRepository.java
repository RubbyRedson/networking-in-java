package se.kth.networking.java.third.data;

import se.kth.id2212.ex2.bankrmi.Account;
import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.User;
import se.kth.networking.java.third.model.Wish;

import javax.security.auth.login.LoginException;
import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public interface IRepository {
    void saveItem(int userid, Item item);
    void saveWish(int userid, Wish wish);
    List<Item> getAllItems();
    Item getItemById(int id);
    Item updateItem(Item item);
    Wish getWishById(int id);
    void deleteWishById(int id);
    List<Wish> getAllWishes();
    User getUserById(int id);
    User login(String username, String password) throws LoginException;
    User register(String username, String password);
}
