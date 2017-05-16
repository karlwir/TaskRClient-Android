package taskr.se.taskr.repository;

/**
 * Created by kawi01 on 2017-05-15.
 */

interface OnResultEventListener<T> {
    void onResult(T result);
}
