package taskr.se.taskr.sql;

import android.provider.BaseColumns;

/**
 * Created by kawi01 on 2017-05-12.
 */

public class TaskRDbContract {

    private TaskRDbContract() {}

    public static class WorkItemsEntry implements BaseColumns {
        public static final String TABLE_NAME = "workitems";
        public static final String COLUMN_NAME_ITEMKEY = "itemkey";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    public static class UsersEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_ITEMKEY = "itemkey";
        public static final String COLUMN_NAME_FIRSTNAME = "firstname";
        public static final String COLUMN_NAME_LASTNAME = "lastname";
        public static final String COLUMN_NAME_USERNAME = "username";
    }

}
