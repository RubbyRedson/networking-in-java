package se.kth.networking.java.third.data;

import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.Notification;
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
    void saveNotification(Notification notification);
    List<Notification> getUserNotifications(int userId);
    void deleteUserNotifications(int userId);

    List<Item> getAllItems();
    Item getItemById(int id);
    Item getItemByName(String name);
    Item updateItem(Item item);
    Wish getWishById(int id);
    void deleteWish(int userId, String goodName);
    List<Wish> getAllWishes();
    User getUserById(int id);
    User getUserByUsername(String username);
    User login(String username, String password) throws LoginException;
    User register(String username, String password);



}
