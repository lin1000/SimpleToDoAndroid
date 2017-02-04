package com.codepath.simpletoto.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 *  This class is a utility (helper) class for SQLite database.
 *  Created by lin1000 on 2017/2/5.
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "simpleTodoDatabase";
    private static final int DATABASE_VERSION = 1;

    //Table Info
    private static final String TABLE_ITEM = "ITEM";
    private static final String COLUMN_ITEM_ID = "ITEM_ID";
    private static final String COLUMN_ITEM_PRIORITY  = "ITEM_PRIORITY";
    private static final String COLUMN_ITEM_CONTENT  = "ITEM_CONTENT";
    private static final String COLUMN_ITEM_START_DATE = "ITEM_START_DATE";
    private static final String COLUMN_ITEM_DUE_DATE = "ITEM_DUE_DATE";
    private static final String COLUMN_ITEM_IS_COMPLETE  = "ITEM_IS_COMPLETE";

    //Singleton instance
    private static TodoDatabaseHelper s;

    public static TodoDatabaseHelper getInstance(Context context) {
        if (s == null) {
            synchronized(TodoDatabaseHelper.class) {
                if(s == null) {
                    s = new TodoDatabaseHelper(context);
                    System.out.println("A new instance of TodoDatabaseHelper "+ s + " has been created.");
                }
            }
        }
        return s;
    }

   /**
      * Constructor should be private to prevent direct instantiation.
      * Make a call to the static method "getInstance()" instead.
      */
    private TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEM +
                "(" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                COLUMN_ITEM_PRIORITY + " TEXT," +
                COLUMN_ITEM_CONTENT + " TEXT," +
                COLUMN_ITEM_START_DATE + " TEXT," +
                COLUMN_ITEM_DUE_DATE + " TEXT," +
                COLUMN_ITEM_IS_COMPLETE + " TEXT" +
                ")";

        db.execSQL(CREATE_ITEM_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
            onCreate(db);
        }
    }

    public void addItem(Item item){
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEM_ID, item.ITEM_ID);
            values.put(COLUMN_ITEM_PRIORITY, item.ITEM_PRIORITY);
            values.put(COLUMN_ITEM_CONTENT, item.ITEM_CONTENT);
            values.put(COLUMN_ITEM_START_DATE, item.ITEM_START_DATE);
            values.put(COLUMN_ITEM_DUE_DATE, item.ITEM_DUE_DATE);
            values.put(COLUMN_ITEM_IS_COMPLETE, item.ITEM_IS_COMPLETE);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_ITEM, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add item to database");
        } finally {
            db.endTransaction();
        }
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        // SELECT * FROM ITEM
        String ITEM_SELECT_QUERY =
                String.format("SELECT * FROM %s ORDER BY ITEM_ID ASC",
                        TABLE_ITEM);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEM_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Item newItem = new Item();
                    newItem.ITEM_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_ID));
                    newItem.ITEM_PRIORITY = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_PRIORITY));
                    newItem.ITEM_CONTENT = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_CONTENT));
                    newItem.ITEM_START_DATE = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_START_DATE));
                    newItem.ITEM_DUE_DATE = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_DUE_DATE));
                    newItem.ITEM_IS_COMPLETE = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_IS_COMPLETE));
                    items.add(newItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    public void deleteItemByItemContent(String itemContent) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_ITEM, "ITEM_CONTENT='"+ itemContent +"'", null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }

    // Update
    public int updateItem(Item item, Item itemOriginal) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rtn=0;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            //values.put(COLUMN_ITEM_ID, item.ITEM_ID);
            values.put(COLUMN_ITEM_PRIORITY, item.ITEM_PRIORITY);
            values.put(COLUMN_ITEM_CONTENT, item.ITEM_CONTENT);
            values.put(COLUMN_ITEM_START_DATE, item.ITEM_START_DATE);
            values.put(COLUMN_ITEM_DUE_DATE, item.ITEM_DUE_DATE);
            values.put(COLUMN_ITEM_IS_COMPLETE, item.ITEM_IS_COMPLETE);
            System.out.println("update item.ITEM_CONTENT=" + item.ITEM_CONTENT);
            rtn=  db.update(TABLE_ITEM, values, COLUMN_ITEM_CONTENT + " = ?",
                    new String[]{String.valueOf(itemOriginal.ITEM_CONTENT)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
        System.out.println("rtn="+rtn);
        return rtn;
    }

}
