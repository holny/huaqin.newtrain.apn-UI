package huaqin.houlinyuan.a0920_apnsui.apn_crud;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.database.Apns;
import huaqin.houlinyuan.a0920_apnsui.ui.adapters.ActivityPagerAdapter;

public class ApnDatilActivity extends AppCompatActivity {
    private final static String TAG = "hlyAPNDatilActivity";
    private LinearLayout main_layout;
    private ContentResolver resolver;
    private ArrayList<String> attributeNameList;
    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private int pxHeight;
    private int pxWidth;
    private int densityDpi;
    private float density;
    private int dpHeight;
    private int dpWidth;
    private Resources resources;
    private final int REQUEST_CODE = 1234;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apndatil);
        //新添加 begin
        setupActionBar();





        //新添加  end
        preferences = getSharedPreferences("initdatabasecount",MODE_PRIVATE);
        editor = preferences.edit();
        getWindowDatil();
        resolver = getContentResolver();
        attributeNameList = new ArrayList<String>();
        int attributeNameList_size = preferences.getInt("attributeNameList_size",0);
        for(int i = 0;i < attributeNameList_size;i++)
        {
            attributeNameList.add(preferences.getString("attributeNameList_" + i,"no"));

        }


        initView();
    }
    private void setupActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }




    public void getWindowDatil()
    {
        windowManager = this.getWindowManager();
        displayMetrics = this.getResources().getDisplayMetrics();
        resources = getResources();
        pxHeight = displayMetrics.heightPixels;         //获取屏幕高像素
        pxWidth = displayMetrics.widthPixels;           //获取屏幕宽像素
        densityDpi = displayMetrics.densityDpi;         //指每英寸中的像素数.假定设备分辨率为320*240，屏幕长2英寸宽1.5英寸，dpi=320/2=240/1.5=160
        density = displayMetrics.density;               //指每平方英寸中的像素数,与DPI转坏关系：density的值为dpi/160
        //px转化为dip(dp),一般推荐使用dp，而不是px
        //以下两种方法都行,注意上面是densityDpi,下面是像素密度density
        dpHeight = (pxHeight*160)/densityDpi;
        dpWidth = (pxWidth*160)/densityDpi;

//      dpHeightA = (int)(pxHeight / density +0.5f);
//      dpWidthA = (int)(pxWidth / density +0.5f);

//      pxHeight = windowManager.getDefaultDisplay().getHeight(); //这个得出的是 像素，也就是跟上面的 pxHeight相等
//      pxWidth = windowManager.getDefaultDisplay().getWidth();
    }
    public void initView()
    {
        //往viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        LayoutInflater inflater = getLayoutInflater();
        //实例化apn_datil layout 并 插入到 viewpager中
        View view = inflater.inflate(R.layout.apn_datil, null);
        main_layout = (LinearLayout)view.findViewById(R.id.apndatil_mainlayout);
        ActivityPagerAdapter adapter = new ActivityPagerAdapter(view, this, null);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());


        final int itemId = getIntent().getIntExtra("number",-1);
        //main_layout = (LinearLayout)findViewById(R.id.apndatil_mainlayout);
        Log.d(TAG, "---------------id=" + itemId);
        //根据itemId查询数据库数据。
        Uri uri = ContentUris.withAppendedId(Apns.Apn.APN_CONTENT_URI,itemId);
        Cursor cursor = resolver.query(uri, null, null, null, null, null);


        if (cursor.moveToFirst())
        {
            Log.d(TAG, "-------cursor--------moveToFirst");
            for (int i = 0;i < attributeNameList.size();i++)
            {
                final String attrName = attributeNameList.get(i);
                Log.d(TAG, "---------------attrName=" + attrName);
                Log.d(TAG, "---------------cursor=" + cursor.getString(1));
                final String attrValue = cursor.getString(cursor.getColumnIndex(attrName));
                if ((attrValue == null) || ("null".equals(attrValue.trim())))
                {
                    continue;
                }
                else if ((attrValue != null) && ("".equals(attrValue.trim())))
                {
                    LinearLayout branch_layout = new LinearLayout(this);
                    branch_layout.setOrientation(LinearLayout.VERTICAL);
                    branch_layout.setMinimumHeight(dpHeight/15);
                    TextView attrName_textview = new TextView(this);
                    TextView attrValue_textview = new TextView(this);
                    attrName_textview.setTextSize(16);
                    attrName_textview.getPaint().setFakeBoldText(true);
                    attrValue_textview.setTextSize(10);
                    attrName_textview.setText(attrName);
                    attrValue_textview.setText("");
                    branch_layout.setPadding(16, 16, 0, 0);
                    branch_layout.addView(attrName_textview);
                    branch_layout.addView(attrValue_textview);
                    branch_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ApnDatilActivity.this, ApnUpgradeDialogActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("attrName", attrName);
                            bundle.putString("attrValue", attrValue);
                            bundle.putInt("_id", itemId+1);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    });
                    main_layout.addView(branch_layout);
                    Log.d(TAG, "---------------attrValue=2" + attrValue);
                }
                else if ((attrValue != null) && (!"".equals(attrValue.trim())) && (!"null".equals(attrValue.trim())))
                {
                    final LinearLayout branch_layout = new LinearLayout(this);
                    branch_layout.setOrientation(LinearLayout.VERTICAL);
                    branch_layout.setMinimumHeight(dpHeight/15);
                    TextView attrName_textview = new TextView(this);
                    final TextView attrValue_textview = new TextView(this);
                    attrName_textview.setTextSize(16);
                    attrName_textview.getPaint().setFakeBoldText(true);
                    attrValue_textview.setTextSize(10);
                    attrName_textview.setText(attrName);
                    attrValue_textview.setText(attrValue);
                    branch_layout.setPadding(16, 16, 0, 0);
                    branch_layout.addView(attrName_textview);
                    branch_layout.addView(attrValue_textview);
                    branch_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ApnDatilActivity.this, ApnUpgradeDialogActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("attrName", attrName);
                            bundle.putString("attrValue", attrValue);
                            bundle.putInt("_id", itemId);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    });
                    main_layout.addView(branch_layout);
                    Log.d(TAG, "---------------attrValue=3" + attrValue);
                }
            }
        }
        cursor.close();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == 4321)
        {
            Log.d(TAG, "------onActivityResult---------");
            main_layout.removeAllViews();
            initView();
        }
    }

}
