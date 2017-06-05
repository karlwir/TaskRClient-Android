package se.taskr.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kawi01 on 2017-05-12.
 */

public class TaskRDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "taskr.db";
    private static final int DB_VERSION = 11;
    private static TaskRDbHelper instance;

    private static final String CREATE_TABLE_WORKITEMS =
            "CREATE TABLE " + TaskRDbContract.WorkItemsEntry.TABLE_NAME + " (" +
                    TaskRDbContract.WorkItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskRDbContract.WorkItemsEntry.COLUMN_NAME_ITEMKEY + " TEXT, " +
                    TaskRDbContract.WorkItemsEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                    TaskRDbContract.WorkItemsEntry.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                    TaskRDbContract.WorkItemsEntry.COLUMN_NAME_STATUS + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TaskRDbContract.UsersEntry.TABLE_NAME + " (" +
                    TaskRDbContract.UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskRDbContract.UsersEntry.COLUMN_NAME_ITEMKEY + " TEXT, " +
                    TaskRDbContract.UsersEntry.COLUMN_NAME_FIRSTNAME + " TEXT NOT NULL, " +
                    TaskRDbContract.UsersEntry.COLUMN_NAME_LASTNAME + " TEXT NOT NULL, " +
                    TaskRDbContract.UsersEntry.COLUMN_NAME_USERNAME + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_TEAMS =
            "CREATE TABLE " + TaskRDbContract.TeamsEntry.TABLE_NAME + " (" +
                    TaskRDbContract.TeamsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskRDbContract.TeamsEntry.COLUMN_NAME_ITEMKEY + " TEXT, " +
                    TaskRDbContract.TeamsEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                    TaskRDbContract.TeamsEntry.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_USER_WORKITEM =
            "CREATE TABLE " + TaskRDbContract.UserWorkItemEntry.TABLE_NAME + " (" +
                    TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_USERID + " INTEGER NOT NULL, " +
                    TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_WORKITEMID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_WORKITEMID + ") REFERENCES " + TaskRDbContract.WorkItemsEntry.TABLE_NAME + "(" + TaskRDbContract.WorkItemsEntry._ID + "), " +
                    "FOREIGN KEY(" + TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_USERID + ") REFERENCES " + TaskRDbContract.UsersEntry.TABLE_NAME + "(" + TaskRDbContract.UsersEntry._ID + " )," +
                    "CONSTRAINT unq UNIQUE(" + TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_USERID + ", " + TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_WORKITEMID + "));";

    private static final String CREATE_TABLE_USER_TEAM =
            "CREATE TABLE " + TaskRDbContract.UserTeamEntry.TABLE_NAME + " (" +
                    TaskRDbContract.UserTeamEntry.COLUMN_NAME_USERID + " INTEGER NOT NULL, " +
                    TaskRDbContract.UserTeamEntry.COLUMN_NAME_TEAMID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + TaskRDbContract.UserTeamEntry.COLUMN_NAME_TEAMID + ") REFERENCES " + TaskRDbContract.TeamsEntry.TABLE_NAME + "(" + TaskRDbContract.TeamsEntry._ID + "), " +
                    "FOREIGN KEY(" + TaskRDbContract.UserTeamEntry.COLUMN_NAME_USERID + ") REFERENCES " + TaskRDbContract.UsersEntry.TABLE_NAME + "(" + TaskRDbContract.UsersEntry._ID + " )," +
                    "CONSTRAINT unq UNIQUE(" + TaskRDbContract.UserTeamEntry.COLUMN_NAME_USERID + ", " + TaskRDbContract.UserTeamEntry.COLUMN_NAME_TEAMID + "));";

    private static final String DROP_TABLE_WORKITEMS =
            "DROP TABLE IF EXISTS " + TaskRDbContract.WorkItemsEntry.TABLE_NAME;

    private static final String DROP_TABLE_USERS =
            "DROP TABLE IF EXISTS " + TaskRDbContract.UsersEntry.TABLE_NAME;

    private static final String DROP_TABLE_TEAMS =
            "DROP TABLE IF EXISTS " + TaskRDbContract.TeamsEntry.TABLE_NAME;

    private static final String DROP_TABLE_USER_WORKITEM =
            "DROP TABLE IF EXISTS " + TaskRDbContract.UserWorkItemEntry.TABLE_NAME;

    private static final String DROP_TABLE_USER_TEAM =
            "DROP TABLE IF EXISTS " + TaskRDbContract.UserTeamEntry.TABLE_NAME;

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
        db.execSQL(CREATE_TABLE_TEAMS);
        db.execSQL(CREATE_TABLE_USER_WORKITEM);
        db.execSQL(CREATE_TABLE_USER_TEAM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_WORKITEMS);
        db.execSQL(DROP_TABLE_USERS);
        db.execSQL(DROP_TABLE_TEAMS);
        db.execSQL(DROP_TABLE_USER_TEAM);
        db.execSQL(DROP_TABLE_USER_WORKITEM);
        onCreate(db);
    }
}
