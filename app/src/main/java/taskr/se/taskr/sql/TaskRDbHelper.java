package taskr.se.taskr.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import taskr.se.taskr.sql.TaskRDbContract.*;

/**
 * Created by kawi01 on 2017-05-12.
 */

public class TaskRDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "taskr.db";
    private static final int DB_VERSION = 10;
    private static TaskRDbHelper instance;

    private static final String CREATE_TABLE_WORKITEMS =
            "CREATE TABLE " + WorkItemsEntry.TABLE_NAME + " (" +
                    WorkItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkItemsEntry.COLUMN_NAME_ITEMKEY + " TEXT, " +
                    WorkItemsEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                    WorkItemsEntry.COLUMN_NAME_DESCRIPTION + " INTEGER NOT NULL, " +
                    WorkItemsEntry.COLUMN_NAME_STATUS + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + UsersEntry.TABLE_NAME + " (" +
                    UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UsersEntry.COLUMN_NAME_ITEMKEY + " TEXT, " +
                    UsersEntry.COLUMN_NAME_FIRSTNAME + " TEXT NOT NULL, " +
                    UsersEntry.COLUMN_NAME_LASTNAME + " INTEGER NOT NULL, " +
                    UsersEntry.COLUMN_NAME_USERNAME + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_USER_WORKITEM =
            "CREATE TABLE " + UserWorkItemEntry.TABLE_NAME + " (" +
                    UserWorkItemEntry.COLUMN_NAME_USERID + " INTEGER NOT NULL, " +
                    UserWorkItemEntry.COLUMN_NAME_WORKITEMID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + UserWorkItemEntry.COLUMN_NAME_WORKITEMID + ") REFERENCES " + WorkItemsEntry.TABLE_NAME + "(" + WorkItemsEntry._ID + "), " +
                    "FOREIGN KEY(" + UserWorkItemEntry.COLUMN_NAME_USERID + ") REFERENCES " + UsersEntry.TABLE_NAME + "(" + UsersEntry._ID + " )," +
                    "CONSTRAINT unq UNIQUE(" + UserWorkItemEntry.COLUMN_NAME_USERID + ", " + UserWorkItemEntry.COLUMN_NAME_WORKITEMID + "));";

    private static final String DROP_TABLE_WORKITEMS =
            "DROP TABLE IF EXISTS " + WorkItemsEntry.TABLE_NAME;

    private static final String DROP_TABLE_USERS =
            "DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME;

    private static final String DROP_TABLE_USER_WORKITEM =
            "DROP TABLE IF EXISTS " + UserWorkItemEntry.TABLE_NAME;

    public static synchronized TaskRDbHelper getInstance(Context context) {
        if(instance == null) {
            instance = new TaskRDbHelper(context);
        }
        return instance;
    }

    private TaskRDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORKITEMS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_USER_WORKITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_WORKITEMS);
        db.execSQL(DROP_TABLE_USERS);
        db.execSQL(DROP_TABLE_USER_WORKITEM);
        onCreate(db);
    }
}
