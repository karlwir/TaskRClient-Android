package taskr.se.taskr.model;

/**
 * Created by Kevin on 2017-05-11.
 */

public class WorkItem {

    private final String title;
    private final String description;
    private String status;

    public WorkItem(String title, String description, String status) {
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
