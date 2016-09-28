package huaqin.houlinyuan.a0920_apnsui.database;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import huaqin.houlinyuan.a0920_apnsui.apn_crud.InitDataBaseFragment;

public class InitDatabaseIntentService extends IntentService {
    private final static String TAG = "hlyInitService";
    private static final int INIT_DATABASE_ERROR = 1231;
    private static final int INIT_DATABASE_GETATTRNAMELIST_DOING_MSG = 1232;
    private static final int INIT_DATABASE_GETATTRNAMELIST_DONE_MSG = 1233;
    private static final int INIT_DATABASE_INSERT_DOING_MSG = 1234;
    private static final int INIT_DATABASE_INSERT_DONE_MSG = 1235;

    private static  int APN_COUNT;
    private static int INSERT_COUNT;
    private ContentResolver resolver;
    private   ArrayList<String> attributeNameList;
    private  ArrayList<String> attrTypeList;
    private  ArrayList<String> mvno_typeList;


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private  int initDatabase_count;


    public InitDatabaseIntentService() {
        super("InitDatabaseIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        preferences = getSharedPreferences("initdatabasecount",MODE_PRIVATE);
        editor = preferences.edit();
        initDatabase_count = preferences.getInt("initcount", 0);
        Log.d(TAG,"---------InitDatabaseIntentService---------");

        resolver = getContentResolver();
        //读取apns_conf.xml文件并解析，读取这个文件数据到数据库并初始化数据库
        Resources resources = getResources();
        AssetManager am = getAssets();
        attributeNameList = new ArrayList<String>();
        attrTypeList = new ArrayList<String>();
        mvno_typeList = new ArrayList<String>();
        try {
            InputStream inputStreamA = am.open("apns-conf.xml");
            InputStream inputStreamB = am.open("apns-conf.xml");
            getAttributeNameList(inputStreamA);
            analyzeXML(inputStreamB);
        } catch (IOException e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("warning", "xml文件提取错误!");
            msg.setData(bundle);
            msg.what = INIT_DATABASE_ERROR;
            InitDataBaseFragment.initDBHandler.sendMessage(msg);

            e.printStackTrace();
        }
    }
    public void getAttributeNameList(InputStream inputStream)
    {
        APN_COUNT = 0;
        Log.d(TAG,"---------getAttributeNameList-----begin-----");
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(inputStream,"UTF-8");
            int eventType = xmlPullParser.getEventType();

            boolean ATTR_NAME_IS_EXIST = true;
            while (eventType != xmlPullParser.END_DOCUMENT) {
                Log.d(TAG, "----------init get---------");
                if(eventType == xmlPullParser.START_DOCUMENT)
                {
                    //文档开始时处理事件
                    Log.d(TAG, "----------init get----START_DOCUMENT-----");
                }
                if (eventType == xmlPullParser.START_TAG) {
                    //元素开始事件
                    Log.d(TAG, "----------init get----START_TAG-----");
                    String tagName = xmlPullParser.getName();
                    if ("apn".equals(tagName)) {
                        APN_COUNT++;
                        //XML文件
                        String carrierValue = xmlPullParser.getAttributeValue(null, "carrier");
                        if (carrierValue != null)
                        {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("doing", "提取XML字段列表ing ,Carrier:" + carrierValue);
                            msg.setData(bundle);
                            msg.what = INIT_DATABASE_GETATTRNAMELIST_DOING_MSG;
                            InitDataBaseFragment.initDBHandler.sendMessage(msg);
                        }
                        else
                        {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("warning", "插入数据库,xml文件格式错误!");
                            msg.setData(bundle);
                            msg.what = INIT_DATABASE_ERROR;
                            InitDataBaseFragment.initDBHandler.sendMessage(msg);
                        }
                        //循环一个apn标签内所有属性，循环次数为属性个数
                        for (int i=0;i <  xmlPullParser.getAttributeCount();i++)
                        {
                            Log.d(TAG, "-----------AttrName-----------i= " + i);
                            ATTR_NAME_IS_EXIST = true;
                            //获取标签内第i个属性名
                            String attrNmae = xmlPullParser.getAttributeName(i);
                            Log.d(TAG, "-----------AttrName-----------AttrName= " + attrNmae);
                            if((attrNmae != null) && (!"".equals(attrNmae.trim())))
                            {
                                Log.d(TAG, "-----------AttrName-----------A ");
                                for (int k = 0;k < attributeNameList.size();k++)
                                {
                                    if (attrNmae.trim().equals(attributeNameList.get(k).trim()))
                                    {
                                        Log.d(TAG, "-----------AttrName-----------K= " + k);
                                        ATTR_NAME_IS_EXIST = false;

                                        break;
                                    }
                                }
                                if (ATTR_NAME_IS_EXIST)
                                {
                                    Log.d(TAG, "-----------AttrName--OK");
                                    attributeNameList.add(attrNmae);
                                }

                            }
                            else  //若属性名为空，说明循环到标签底了，结束循环
                            {
                                break;
                            }
                        }
                    }
                }
                eventType = xmlPullParser.next();


            }
        } catch (XmlPullParserException e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("warning", "获取字段名，xml文件解析错误!");
            msg.setData(bundle);
            msg.what = INIT_DATABASE_ERROR;
            InitDataBaseFragment.initDBHandler.sendMessage(msg);

            e.printStackTrace();
        } catch (IOException e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("warning", "获取字段名，字符流错误!");
            msg.setData(bundle);
            msg.what = INIT_DATABASE_ERROR;
            InitDataBaseFragment.initDBHandler.sendMessage(msg);

            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("warning", "获取字段名，字符流关闭错误!");
            msg.setData(bundle);
            msg.what = INIT_DATABASE_ERROR;
            InitDataBaseFragment.initDBHandler.sendMessage(msg);

            e.printStackTrace();
        }
        //向MainMenuActivity Handler发送提取字段名成功消息
        Message msg = new Message();
        msg.what = INIT_DATABASE_GETATTRNAMELIST_DONE_MSG;
        Bundle bundle = new Bundle();
        bundle.putInt("apncount", APN_COUNT);
        msg.setData(bundle);
        InitDataBaseFragment.initDBHandler.sendMessage(msg);
        editor.putInt("attributeNameList_size",attributeNameList.size());
        for (int i = 0;i < attributeNameList.size();i++)
        {
            editor.putString("attributeNameList_" + i,attributeNameList.get(i));
        }
        editor.commit();
        //改变APNDatabase中创建数据库表的语句。
        StringBuilder newCREATE_TABLE = new StringBuilder();
        newCREATE_TABLE.append("create table apntable (_id integer primary key autoincrement ");
        for (int i = 0;i < attributeNameList.size();i++)
        {
            newCREATE_TABLE.append(", " + attributeNameList.get(i));
            if (i == (attributeNameList.size() - 1))
            {
                newCREATE_TABLE.append(" )");
            }
        }
        ApnDatabase.CREATE_TABLE = newCREATE_TABLE.toString();
        Log.d(TAG,"---------getAttributeNameList-----end-----SQL=" + ApnDatabase.CREATE_TABLE);
    }



    public void  analyzeXML(InputStream inputStream)
    {
        INSERT_COUNT = 0;
        Log.d(TAG,"---------analyzeXML-----begin-----");
        //清空数据库中的数据
        resolver.delete(Apns.Apn.DICT_CONTENT_URI, null, null);
        boolean TYPE_IS_EXIST = true;
        boolean MVNO_TYPE_IS_EXIST = true;
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();

            xmlPullParser.setInput(inputStream,"UTF-8");
            int eventType = xmlPullParser.getEventType();
            int count = 0;
            while (eventType != xmlPullParser.END_DOCUMENT) {
                Log.d(TAG, "----------init insert---------");
                if(eventType == xmlPullParser.START_DOCUMENT)
                {
                    //文档开始时处理事件
                    Log.d(TAG, "----------init insert----START_DOCUMENT-----");
                }
                if (eventType == xmlPullParser.START_TAG) {
                    //元素开始事件
                    Log.d(TAG, "----------init insert----START_TAG-----");
                    String tagName = xmlPullParser.getName();
                    if ("apn".equals(tagName)) {
                        INSERT_COUNT++;
                        ContentValues contentValues = new ContentValues();
                        for (int i = 0; i < attributeNameList.size(); i++) {
                            String attrName = attributeNameList.get(i);
                            String attrValue = xmlPullParser.getAttributeValue(null, attrName);
                            if (attrValue == null) {
                                contentValues.put(attrName, "null");
                            } else if ("".equals(attrValue.trim())) {
                                contentValues.put(attrName, "");
                            } else {
                                contentValues.put(attrName, attrValue);
                            }
                        }
                        // 解析出 type 类型有哪几种
                        String attrTypeValue = xmlPullParser.getAttributeValue(null, "type");
                        Log.d(TAG, "----------------type=" + attrTypeValue);
                        String[] attrTypeArray = attrTypeValue.split(",");
                        TYPE_IS_EXIST = false;

                        for (int i = 0;i < attrTypeArray.length;i++)
                        {
                            for (int k = 0; k < attrTypeList.size(); k++)
                            {
                                if (attrTypeList.get(k).equals(attrTypeArray[i].trim()))
                                {
                                    TYPE_IS_EXIST = true;
                                    break;
                                }
                                else if(!"".equals(attrTypeArray[i].trim()))
                                {
                                    TYPE_IS_EXIST = false;
                                }
                            }
                            if ((!TYPE_IS_EXIST) && (!"".equals(attrTypeArray[i].trim())) && (!"null".equals(attrTypeArray[i].trim())))
                            {
                                attrTypeList.add(attrTypeArray[i].trim());
                            }
                            Log.d(TAG, "----------------attrTypeArray=" + attrTypeArray[i]);
                        }
                        Log.d(TAG, "----------------attrTypeList=" + attrTypeList.toString());
                        ///////
                        // 解析出 mvno_type 类型有哪几种
                        String mvno_typeValue = xmlPullParser.getAttributeValue(null, "mvno_type");
                        Log.d(TAG, "----------------type=" + attrTypeValue);
                        MVNO_TYPE_IS_EXIST = false;
                        if ((mvno_typeValue != null) && (!"".equals(mvno_typeValue.trim())) && (!"null".equals(mvno_typeValue.trim())))
                        {
                            for (int k = 0; k < mvno_typeList.size(); k++) {
                                if (mvno_typeList.get(k).equals(mvno_typeValue.trim())) {
                                    MVNO_TYPE_IS_EXIST = true;
                                    break;
                                } else if (!"".equals(mvno_typeValue.trim())) {
                                    MVNO_TYPE_IS_EXIST = false;
                                }
                            }
                            if (!MVNO_TYPE_IS_EXIST) {
                                mvno_typeList.add(mvno_typeValue.trim());
                            }
                        }
                        Log.d(TAG, "----------------mvno_typeList=" + mvno_typeList.toString());
                        ///////
                        resolver.insert(Apns.Apn.DICT_CONTENT_URI, contentValues);
                        //向MainMenuActivity Handler 发送插入数据信息
                        String carrierValue = xmlPullParser.getAttributeValue(null, "carrier");
                        if (carrierValue == null)
                        {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("warning", "插入数据库,xml文件格式错误!");
                            msg.setData(bundle);
                            msg.what = INIT_DATABASE_ERROR;
                            InitDataBaseFragment.initDBHandler.sendMessage(msg);
                        }
                        else {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("doing", "解析xml,插入数据库ing ,Carrier:" + carrierValue);
                            bundle.putInt("apncount", APN_COUNT);
                            bundle.putInt("insertcount", INSERT_COUNT);
                            msg.setData(bundle);
                            msg.what = INIT_DATABASE_INSERT_DOING_MSG;
                            InitDataBaseFragment.initDBHandler.sendMessage(msg);
                        }



                        count++;
                        Log.d(TAG, "--------init insert-----------count=" + count);
                        Log.d(TAG, "--------init insert-----------values=" + contentValues.toString());

                    }
                }
                if (eventType == xmlPullParser.END_DOCUMENT) {
                    Log.d(TAG, "----------init insert----END_DOCUMENT-----");
                }
                eventType = xmlPullParser.next();
                Log.d(TAG, "----------init insert-------END--");

            }
        } catch (XmlPullParserException e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("warning", "插入数据库，xml文件解析错误!");
            msg.setData(bundle);
            msg.what = INIT_DATABASE_ERROR;
            InitDataBaseFragment.initDBHandler.sendMessage(msg);

            e.printStackTrace();
        } catch (IOException e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("warning", "插入数据库，字符流错误!");
            msg.setData(bundle);
            msg.what = INIT_DATABASE_ERROR;
            InitDataBaseFragment.initDBHandler.sendMessage(msg);

            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("warning", "插入数据库，字符流关闭错误!");
            msg.setData(bundle);
            msg.what = INIT_DATABASE_ERROR;
            InitDataBaseFragment.initDBHandler.sendMessage(msg);

            e.printStackTrace();
        }

        editor.putInt("attrTypeList_size", attrTypeList.size());
        for (int q = 0;q < attrTypeList.size();q++)
        {
            editor.putString("attrTypeList_" + q,attrTypeList.get(q));
        }
        editor.commit();

        editor.putInt("mvno_typeList_size", mvno_typeList.size());
        for (int q = 0;q < mvno_typeList.size();q++)
        {
            editor.putString("mvno_typeList_" + q,mvno_typeList.get(q));
        }
        editor.commit();

        initDatabase_count++;
        editor.putInt("initcount", initDatabase_count);
        editor.commit();
        Log.d(TAG,"---------insert-----end-----attrTypeList=" + attrTypeList.toString());
        Log.d(TAG,"---------insert-----end-----mvno_typeList=" + mvno_typeList.toString());
        //向MainMenuActivity Handler发送提取字段名成功消息
        Message msg = new Message();
        msg.what = INIT_DATABASE_INSERT_DONE_MSG;
        InitDataBaseFragment.initDBHandler.sendMessage(msg);

    }

}
