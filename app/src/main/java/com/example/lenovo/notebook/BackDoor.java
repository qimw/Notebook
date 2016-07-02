package com.example.lenovo.notebook;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.lenovo.notebook.Db.DatabaseHelper;

/**
 * Created by lenovo on 2016/5/27.
 */
public class BackDoor extends ContentProvider {
    private static final int ARTICLE_ONE_DIR = 0;
    private static final int ARTICLE_ONE_ITEM= 1;
    private static UriMatcher uriMatcher;
    private static DatabaseHelper databaseHelper;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.example.lenovo.notebook.provider","article_one",ARTICLE_ONE_DIR);
        uriMatcher.addURI("com.example.lenovo.notebook.provider","article_one/#",ARTICLE_ONE_ITEM);
    }
    @Override
    public boolean onCreate(){
        databaseHelper = new DatabaseHelper(getContext(),"data",1);
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int deleteRows = 0;
        switch (uriMatcher.match(uri)){
            case ARTICLE_ONE_DIR:
                deleteRows = database.delete("article_one",selection,selectionArgs);
                break;
            case ARTICLE_ONE_ITEM:
                String articleId = uri.getPathSegments().get(1);
                deleteRows = database.delete("article_one","id == ?",new String[]{articleId});
                break;
            default:
                break;
        }
        return deleteRows;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = null;
        switch(uriMatcher.match(uri)){
            case ARTICLE_ONE_DIR:
                cursor = database.query("article_one",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ARTICLE_ONE_ITEM:
                String articleId = uri.getPathSegments().get(1);
                cursor = database.query("article_one",projection,"id == ?",new String[]{articleId},null,null,sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case ARTICLE_ONE_DIR:
                return "vnd.android.cursor.dir/com.example.lenovo.notebook.provider.article_one";
            case ARTICLE_ONE_ITEM:
                return "vnd.android.cursor.item/com.example.lenovo.notebook.provider.article_one";
            default:
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case ARTICLE_ONE_DIR:
            case ARTICLE_ONE_ITEM:
                long newArticleId = database.insert("article_ont",null,values);
                uriReturn = Uri.parse("content://com.example.lenovo.nootebook./article_one/" + newArticleId);
                break;
                default:
                    break;
        }
        return uriReturn;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                SQLiteDatabase database = databaseHelper.getWritableDatabase();
                int updataRows = 0;
                switch (uriMatcher.match(uri)){
                case ARTICLE_ONE_DIR:
                updataRows = database.update("article_one",values,selection,selectionArgs);
                break;
                case ARTICLE_ONE_ITEM:
                    String bookId = uri.getPathSegments().get(1);
                    updataRows = database.update("article_one",values,"id == ?",new String[]{bookId});
                    break;
                default:
                    break;
        }
        return updataRows;
    }
}
