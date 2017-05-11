package taskr.se.taskr.model;

/**
 * Created by Kevin on 2017-05-11.
 */

public class WorkItem {

    private final String title;
    private final String description;
    private ItemStatus status;

    public WorkItem(String title, String description, ItemStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public enum ItemStatus {
        UNSTARTED,STARTED,DONE
    }
}
