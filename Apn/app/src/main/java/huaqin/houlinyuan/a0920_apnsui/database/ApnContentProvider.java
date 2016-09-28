package huaqin.houlinyuan.a0920_apnsui.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ApnContentProvider extends ContentProvider {
    private static final String TAG = "hlyAPNContentProvider";

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int APNS = 1;
    private static final int APN = 2;
    private ApnDatabase dbHelper;
    static
    {
        matcher.addURI(Apns.AUTHORITY, "apns", APNS);
        matcher.addURI(Apns.AUTHORITY, "apn/#", APN);
    }
    @Override
    public boolean onCreate() {
        Log.d(TAG, "--------onCreate------");
        dbHelper = new ApnDatabase(this.getContext(), "mydb.db3", 1);


        //解析apns-conf.xml文件，初始化数据到数据库中 begin
        return true;
    }
    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri))
        {
            case APN:
                return "vnd.android.cursor.item/hly.apnprovider.type";
            case APNS:
                return "vnd.android.cursor.dir/hly.apnprovider.type";
            default:
                throw new IllegalArgumentException("未知 Uri:" + uri);
        }
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (matcher.match(uri))
        {
            case APN:
                int id = (int)ContentUris.parseId(uri);
                String whereClause = " " + Apns.Apn._ID + "=" + id;

                if ((selection != null) && (!"".equals(selection.trim())))
                {
                    whereClause = whereClause + " and " + selection;
                }
                Log.d(TAG, "--------query------whereClause=" + whereClause);
                Cursor cursor1 = db.query("apntable",null,whereClause,null,null,null,null);
                Log.d(TAG, "--------query------cursor1=" + cursor1);
                return cursor1;
            case APNS:
                Cursor cursor2 = db.query("apntable", projection, selection, selectionArgs, null, null, sortOrder);
                return cursor2;
            default:
                throw new IllegalArgumentException("未知 Uri:" + uri);

        }
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = 0;
        switch (matcher.match(uri))
        {
            case APNS:
                rowId  = db.insert("apntable", null, values);
                if (rowId > 0) {
                    return ContentUris.withAppendedId(uri, rowId);
                }
                break;
            default:
                throw new IllegalArgumentException("未知Uri :" + uri);


        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int num = 0;
        switch (matcher.match(uri))
        {
            case APNS:
                num = db.update("apntable", values, selection, selectionArgs);
                break;
            case APN:
                long id = ContentUris.parseId(uri);
                String whereClause = Apns.Apn._ID + "=" + id;
                if ((selection != null) && (!"".equals(selection.trim())))
                {
                    whereClause = whereClause + " and " + selection;
                }
                Log.d(TAG, "--------update------whereClause=" + whereClause);
                Log.d(TAG, "--------update------values=" + values.toString());
                num = db.update("apntable", values, whereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知Uri :" + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int num = 0;
        switch (matcher.match(uri))
        {
            case APN:
                long id = ContentUris.parseId(uri);
                String whereClause = Apns.Apn._ID + "=" + id;
                if ((selection != null) && (!"".equals(selection.trim())))
                {
                    whereClause = whereClause + " and " + selection;
                }
                Log.d(TAG,"-------id------" + whereClause);
                num = db.delete("apntable", whereClause, selectionArgs);
                break;
            case APNS:
                num = db.delete("apntable", selection, selectionArgs);
                break;
            default:
                Log.d(TAG , "--------delete 未知Uri--------Uri = " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }
}
