package com.tuts.vijay.qikpic.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class QikPikContentProvider extends ContentProvider {
    public QikPikContentProvider() {
    }

    static final String PROVIDER_NAME = "com.tuts.vijay.qikpic.provider";
    static final String URL = "content://" + PROVIDER_NAME + "/qikpic";
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final Uri CONTENT_FILTER_URI = Uri.parse(URL + "/q");

    static final String _ID = "_id";

    private static HashMap<String, String> QIKPIC_PROJECTION_MAP;

    static final int QIKPICS = 1;
    static final int QIKPIC_ID = 2;
    static final int QIKPIC_QUERY = 3;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "qikpic", QIKPICS);
        uriMatcher.addURI(PROVIDER_NAME, "qikpic/#", QIKPIC_ID);
        uriMatcher.addURI(PROVIDER_NAME, "qikpic/q/*", QIKPIC_QUERY);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Qikpic_db";
    static final String QIKPIC_TABLE_NAME = "qikpics";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + QIKPIC_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " objectId TEXT unique, " +
                    " image TEXT NOT NULL, " +
                    " userId TEXT NOT NULL, " +
                    " createdAt TEXT NOT NULL, " +
                    " updatedAt TEXT NOT NULL, " +
                    " tags TEXT, " +
                    " thumbnail TEXT NOT NULL," +
                    " draft integer," +
                    " qikpicId TEXT unique" +
                    ");";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + QIKPIC_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        if (db == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        long rowID = 0;
        try {
            rowID = db.insertOrThrow(QIKPIC_TABLE_NAME, "", values);
            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }
        } catch (SQLiteConstraintException sq) {
            Log.e("test", "row already found");
            return null;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(QIKPIC_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case QIKPICS:
                qb.setProjectionMap(QIKPIC_PROJECTION_MAP);
                break;

            case QIKPIC_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            case QIKPIC_QUERY:
                Log.d("test", "query: " + uri.getPathSegments().get(2));
                qb.appendWhere("tags LIKE " + uri.getPathSegments().get(2));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Log.d("test", "Uri: " + uri);
        Cursor c = qb.query(db,	projection,	selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case QIKPICS:
                count = db.delete(QIKPIC_TABLE_NAME, selection, selectionArgs);
                break;

            case QIKPIC_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(QIKPIC_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case QIKPICS:
                count = db.update(QIKPIC_TABLE_NAME, values, selection, selectionArgs);
                break;

            case QIKPIC_ID:
                count = db.update(QIKPIC_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case QIKPICS:
                return "com.tuts.vijay.qikpic.dir";
            case QIKPIC_ID:
                return "com.tuts.vijay.qikpic.item";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}