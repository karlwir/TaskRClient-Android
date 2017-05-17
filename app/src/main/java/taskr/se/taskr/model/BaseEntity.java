package taskr.se.taskr.model;

/**
 * Created by kawi01 on 2017-05-17.
 */

public abstract class BaseEntity {
    protected static final long DEFAULT_ID = 0;
    protected static final String DEFAULT_ITEMKEY = null;

    protected final long id;
    protected final String itemKey;

    protected BaseEntity(long id, String itemKey) {
        this.id = id;
        this.itemKey = itemKey;
    }

    public String getItemKey() {
        return itemKey;
    }

    public long getId() {
        return id;
    }
}
