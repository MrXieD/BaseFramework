package com.example.contactroom;

import android.database.sqlite.SQLiteDatabase;

/**
 * Helper class for working with the SQLiteDatabase.
 */
public class SqliteDatabaseTestHelper {


    public static void createTable(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS user (user_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + " user_name TEXT NOT NULL,"
                + "phone_numbers TEXT NOT NULL"
                + " )");

        db.execSQL("CREATE TABLE IF NOT EXISTS 'group' (group_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + " group_name TEXT NOT NULL"
                + " )");

        db.execSQL("CREATE TABLE IF NOT EXISTS 'user_group_cross_ref' (group_id INTEGER NOT NULL,"
                + " user_id INTEGER NOT NULL,"
                + " primary key(user_id,group_id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS call_record (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + " phone_number TEXT NOT NULL,"
                + "record_date INTEGER NOT NULL,"
                + "record_type INTEGER NOT NULL,"
                + "record_duration TEXT NOT NULL)");

        db.execSQL("CREATE INDEX index_user_group_cross_ref_group_id on user_group_cross_ref(group_id)");


        db.close();
    }

    public static void clearDatabase(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS 'group' ");
        db.execSQL("DROP INDEX index_user_group_cross_ref_group_id");
        db.execSQL("DROP TABLE IF EXISTS user_group_cross_ref");
        db.execSQL("DROP TABLE IF EXISTS call_record");
        db.close();
    }
}
