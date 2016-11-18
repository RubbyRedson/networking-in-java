package se.kth.networking.java.third.model;

import java.io.Serializable;

/**
 * Created by victoraxelsson on 2016-11-15.
 */
public interface User extends Serializable {
    int getId();
    String getPassword();
    String getUsername();
}
