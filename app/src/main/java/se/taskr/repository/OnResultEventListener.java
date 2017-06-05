package se.taskr.repository;

/**
 * Created by kawi01 on 2017-05-15.
 */

public interface OnResultEventListener<T> {
    void onResult(T result);
}
