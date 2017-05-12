package taskr.se.taskr.model;

/**
 * Created by Kevin on 2017-05-11.
 */

public class WorkItem {
    private static final long DEFAULT_ID = -1;
    private static final String DEFAULT_ITEMKEY = null;

    private final long id;
    private final String itemKey;
    private final String title;
    private final String description;
    private String status;

    public WorkItem(long id, String itemKey, String title, String description, String status) {
        this.id = id;
        this.itemKey = itemKey;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public WorkItem(String title, String description, String status) {
        this.id = DEFAULT_ID;
        this.itemKey = DEFAULT_ITEMKEY;
        this.title = title;
        this.description = description;
        this.status = status;
    }


    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

}
