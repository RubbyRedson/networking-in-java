package se.kth.networking.java.first;

/**
 * Created by victoraxelsson on 2016-11-04.
 */
public interface OnResponse<T> {
     void onResponse(T response);
}
