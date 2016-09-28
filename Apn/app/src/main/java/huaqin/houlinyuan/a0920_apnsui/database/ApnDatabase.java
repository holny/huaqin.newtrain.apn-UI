package huaqin.houlinyuan.a0920_apnsui.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ubuntu on 16-8-29.
 */
public class ApnDatabase extends SQLiteOpenHelper {
    private final static String TAG = "hlySQLiteOpenHelper";
    public static String CREATE_TABLE; //创建表，要等到initDatabaseIntentService getAttributeNameList完成得到xml字段列表才能创建
    public ApnDatabase(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "--------onCreate------");
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
