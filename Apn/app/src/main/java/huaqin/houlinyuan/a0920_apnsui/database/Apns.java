package huaqin.houlinyuan.a0920_apnsui.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ubuntu on 16-8-29.
 */
public class Apns {
    public static final String AUTHORITY = "hly.provider.apn";
    public static final class Apn implements BaseColumns
    {
        public final static String _ID = "_id";
        public final static String CARRIER = "carrier";
        public final static String MCC = "mcc";
        public final static String MNC = "mnc";
        public final static String APN = "apn";
        public final static String USER = "user";
        public final static String SERVER = "server";
        public final static String PASSWORD = "password";
        public final static String PROXY = "proxy";
        public final static String PORT = "port";
        public final static String MMSPROXY = "mmsproxy";
        public final static String MMSPORT = "mmsport";
        public final static String MMSC = "mmsc";
        public final static String TYPE = "type";
        public final static String PROTOCOL = "protocol";

        public final static Uri DICT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/apns");
        public final static Uri APN_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/apn");
    }
}
