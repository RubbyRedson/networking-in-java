package se.kth.networking.java.third.data;

import se.kth.networking.java.third.model.Item;
import se.kth.networking.java.third.model.User;

import java.util.List;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public interface IRepository {
    void saveItem(Item item);
    List<User> getAllUsers();
}
