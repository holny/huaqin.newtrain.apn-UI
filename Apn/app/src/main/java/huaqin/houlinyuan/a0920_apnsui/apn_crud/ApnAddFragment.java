package huaqin.houlinyuan.a0920_apnsui.apn_crud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

import huaqin.houlinyuan.a0920_apnsui.BaseActivity;
import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.database.Apns;
import huaqin.houlinyuan.a0920_apnsui.ui.adapters.BasePagerAdapter;
import huaqin.houlinyuan.a0920_apnsui.ui.fragments.NotesListFragment;
/**
 * Created by ubuntu on 16-9-21.
 */
public class ApnAddFragment extends NotesListFragment {
    private static final String TAG = "hlyApnAddFragment";
    private static final String TAGA = "FFAAApnAddFragment";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ContentResolver resolver;

    // type 类型，这个字段是默认存在的 typevalueStrBuffer是selectedTypeList中间加逗号，好插入到数据库中与以前的格式对应
    private String[] attrTypeArray;
    private boolean[] typeItemState;
    private int attrTypeList_size;
    private ArrayList<String> selectedTypeList;
    private StringBuffer typevalueStrBuffer;
    //
    private ArrayList<String> attributeNameList;
    private HashMap<String,EditText> viewIdMap;
    private LinearLayout main_layout;
    //window
    private Resources resources;
    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private int pxHeight;
    private int pxWidth;
    private int densityDpi;
   // private float density;
    private int dpHeight;
   // private int dpWidth;
    private ApnAddCallbacks mCallbacks;
    public interface ApnAddCallbacks
    {
        public void apnAddStartToFragment(int fragmentId);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallbacks = (ApnAddCallbacks)activity;
    }
    public static ApnAddFragment newInstance() {
        return new ApnAddFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("initdatabasecount",getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        resolver = getActivity().getContentResolver();
        getWindowDatil();

        attributeNameList = new ArrayList<String>();
        int attributeNameList_size = preferences.getInt("attributeNameList_size",0);
        for(int i = 0;i < attributeNameList_size;i++)
        {
            attributeNameList.add(preferences.getString("attributeNameList_" + i,"no"));

        }

        //获取type类型列表
        attrTypeList_size = preferences.getInt("attrTypeList_size",0);
        attrTypeArray = new String[attrTypeList_size];
        for(int i = 0;i < attrTypeArray.length;i++)
        {
            attrTypeArray[i] = preferences.getString("attrTypeList_" + i,"null");
            Log.d(TAG , "-----onCreate------attrTypeList=" + attrTypeArray[i]);
        }


        Log.d(TAG , "-----onCreate------");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apnadd, container, false);

        //动态添加组件 begin
        viewIdMap = new HashMap<String,EditText>();
        main_layout = (LinearLayout)view.findViewById(R.id.apnadd_mainlayout);
        for (int i = 0;i < attributeNameList.size();i++)
        {
            final String attrName = attributeNameList.get(i);
            Log.d(TAG , "-----initView------attrName=" + attrName);
            if ((attrName != null) && ("type".equals(attrName.trim())))
            {
                Log.d(TAG , "-----initView------type=" + attrName);
                LinearLayout branch_layout = new LinearLayout(getActivity());
                branch_layout.setOrientation(LinearLayout.VERTICAL);
                branch_layout.setMinimumHeight(dpHeight/8);

                final TextView attrName_textview = new TextView(getActivity());
                attrName_textview.setText(attributeNameList.get(i));
                attrName_textview.setTextSize(14);
                attrName_textview.getPaint().setFakeBoldText(true);

                final TextView attrValue_textview = new TextView(getActivity());
                attrValue_textview.setMaxLines(1);
                attrValue_textview.setText("点击选择Type类型");


                attrName_textview.setTextSize(16);
                attrName_textview.getPaint().setFakeBoldText(true);
                attrName_textview.setText(attrName);
                attrValue_textview.setTextSize(10);
                attrValue_textview.setMaxLines(1);
                attrValue_textview.setMaxWidth(25);
                attrValue_textview.setWidth(pxWidth * (3 / 4));
                attrValue_textview.setText("");

                //attrValue_edittext 设置点击监听事件，弹出多选dialog
                attrValue_textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //创建 多选 dialog begin
                        selectedTypeList = new ArrayList<String>();
                        attrValue_textview.setText("");
                        typeItemState = new boolean[attrTypeList_size];
                        for (int i = 0;i < attrTypeList_size;i++)
                        {
                            typeItemState[i] = false;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setIcon(R.drawable.multiselecticon);
                        builder.setTitle("选择Type");

                        builder.setMultiChoiceItems(attrTypeArray, typeItemState, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                if (b)
                                {
                                    selectedTypeList.add(attrTypeArray[i]);
                                }
                                else
                                {
                                    selectedTypeList.remove(attrTypeArray[i]);
                                }
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                typevalueStrBuffer = new StringBuffer();
                                for (int k = 0;k < selectedTypeList.size();k++) {
                                    if (k == 0)
                                    {
                                        typevalueStrBuffer.append(selectedTypeList.get(k));
                                    }
                                    else
                                    {
                                        typevalueStrBuffer.append("," + selectedTypeList.get(k));
                                    }
                                }
                                attrValue_textview.setText(typevalueStrBuffer.toString());
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                attrValue_textview.setText("");
                                typevalueStrBuffer = null;
                            }
                        });
                        builder.create().show();
                        //创建 多选 dialog end


                    }
                });

                branch_layout.setPadding(16, 16, 0, 0);
                branch_layout.addView(attrName_textview);
                branch_layout.addView(attrValue_textview);

                main_layout.addView(branch_layout);
            }
            else {

                LinearLayout branch_layout = new LinearLayout(getActivity());
                branch_layout.setOrientation(LinearLayout.VERTICAL);
                branch_layout.setMinimumHeight(dpHeight / 8);

                TextView attrName_textview = new TextView(getActivity());
                MaterialEditText attrValue_edittext = new MaterialEditText(getActivity());


                attrName_textview.setTextSize(16);
                attrName_textview.getPaint().setFakeBoldText(true);
                attrName_textview.setText(attrName);
                attrValue_edittext.setTextSize(10);
                attrValue_edittext.setMaxLines(1);
                attrValue_edittext.setMaxWidth(25);
                attrValue_edittext.setWidth(pxWidth * (3 / 4));
                attrValue_edittext.setText("");

                viewIdMap.put(attrName, attrValue_edittext);

                branch_layout.setPadding(16, 16, 0, 0);
                branch_layout.addView(attrName_textview);
                branch_layout.addView(attrValue_edittext);

                main_layout.addView(branch_layout);
            }
        }




        //动态添加组件 end
        //给父activity的menu add item 添加点击事件  begin
        BaseActivity baseActivity = (BaseActivity)getActivity();

        baseActivity.mMenu.findItem(R.id.menu_main_add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.d(TAG , "-----onMenuItemClick------");
                switch (menuItem.getItemId())
                {
                    case R.id.menu_main_add:
                        ContentValues contentValues = new ContentValues();
                        StringBuffer warning = new StringBuffer();
                        StringBuffer whereClause = new StringBuffer();
                        ArrayList<String> whereClauseArgs = new ArrayList<String>();
                        whereClause.append(" 1=1 ");
                        for (int i = 0;i < viewIdMap.size();i++)
                        {
                            final String attrName = attributeNameList.get(i);
                            EditText attrView_edittext = new EditText(getActivity());
                            attrView_edittext = viewIdMap.get(attrName);
                            if ((attrName != null) && (!"type".equals(attrName.trim())))
                            {
                                if ("carrier".equals(attrName.trim()) && ((attrView_edittext.getText() == null) || ("".equals(attrView_edittext.getText().toString().trim())))) {
                                    warning.append("carrier不能为空!");
                                } else if ("mcc".equals(attrName.trim()) && ((attrView_edittext.getText() == null) || ("".equals(attrView_edittext.getText().toString().trim())))) {
                                    warning.append("mcc不能为空!");
                                } else if ("mnc".equals(attrName.trim()) && ((attrView_edittext.getText() == null) || ("".equals(attrView_edittext.getText().toString().trim())))) {
                                    warning.append("mnc不能为空!");
                                }
                                if ((attrView_edittext.getText() != null) && (!"".equals((attrView_edittext.getText().toString().trim())))) {
                                    whereClause.append(" and " + attrName + "=? ");
                                    whereClauseArgs.add(attrView_edittext.getText().toString().trim());
                                    contentValues.put(attrName, attrView_edittext.getText().toString().trim());
                                }

                            }
                            else if ((attrName != null) && ("type".equals(attrName.trim())))
                            {
                                if ((selectedTypeList != null) && (selectedTypeList.size() >= 0))
                                {
                                    for (int k = 0;k < selectedTypeList.size();k++)
                                    {
                                        if (!"".equals(selectedTypeList.get(k).trim()))
                                        {
                                            whereClause.append(" and " + attrName + " like ? ");
                                            whereClauseArgs.add(selectedTypeList.get(k).trim());
                                        }
                                    }
                                    contentValues.put(attrName, typevalueStrBuffer.toString());
                                }
                            }
                        }
                        if ((warning != null) && (warning.length() > 1))
                        {
                            Toast toast2 = Toast.makeText(getActivity(), warning.toString(), Toast.LENGTH_SHORT);
                            toast2.show();
                            break;
                        }
                        else
                        {
                            String[] selectionArgs = new String[whereClauseArgs.size()];
                            for (int i = 0;i < whereClauseArgs.size();i++)
                            {
                                selectionArgs[i] = whereClauseArgs.get(i);
                            }

                            Log.d(TAG, "------warning=null-------where="+ whereClause + "---whereClauseArgs=" + whereClauseArgs);
                            Cursor cursor = resolver.query(Apns.Apn.DICT_CONTENT_URI, null, whereClause.toString(),selectionArgs, null);
                            if (cursor.moveToFirst())
                            {
                                Toast toast2 = Toast.makeText(getActivity(), "已有重复数据", Toast.LENGTH_SHORT);
                                toast2.show();
                            }
                            else
                            {
                                cursor.close();
                                Log.d(TAG, "------insert-------contentValues="+ contentValues);
                                Uri resultUri = resolver.insert(Apns.Apn.DICT_CONTENT_URI, contentValues);
                                if (resultUri != null) {
                                    long _id = ContentUris.parseId(resultUri);
                                    Toast toast2 = Toast.makeText(getActivity(), "插入成功,id=" + _id, Toast.LENGTH_SHORT);
                                    toast2.show();
                                    addSuccessShowDialog();
                                }
                                else
                                {
                                    Toast toast2 = Toast.makeText(getActivity(), "插入失败", Toast.LENGTH_SHORT);
                                    toast2.show();
                                }

                            }

                        }
                        Toast toastadd = Toast.makeText(getActivity(), "添加!", Toast.LENGTH_SHORT);
                        toastadd.show();
                        break;
                }




                return false;
            }
        });
        //给父activity的menu add item 添加点击事件  end
        Log.d(TAG , "-----onCreateView------");
        return view;
    }
    public void addSuccessShowDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("添加成功！");
        dialog.setPositiveButton("回主界面", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //返回 父 activity
                Intent intent = NavUtils.getParentActivityIntent(getActivity());
                if (NavUtils.shouldUpRecreateTask(getActivity(), intent))
                {
                    TaskStackBuilder.create(getActivity()).addNextIntentWithParentStack(intent).startActivities();
                }
                else
                {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            }
        });
        dialog.setNeutralButton("继续添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("查看Apn列表", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                BaseActivity baseActivity = (BaseActivity)getActivity();
                baseActivity.mMenu.findItem(R.id.menu_main_add).setVisible(false);
                baseActivity.setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
                Log.d(TAG , "-----查看Apn列表------");
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }




    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_apnadd;
    }

    @Override
    protected int getNumColumns() {
        return 1;
    }
    public void getWindowDatil()
    {
        windowManager = getActivity().getWindowManager();
        displayMetrics = this.getResources().getDisplayMetrics();
        resources = getResources();
        pxHeight = displayMetrics.heightPixels;         //获取屏幕高像素
        pxWidth = displayMetrics.widthPixels;           //获取屏幕宽像素
        densityDpi = displayMetrics.densityDpi;         //指每英寸中的像素数.假定设备分辨率为320*240，屏幕长2英寸宽1.5英寸，dpi=320/2=240/1.5=160
      //  density = displayMetrics.density;               //指每平方英寸中的像素数,与DPI转坏关系：density的值为dpi/160
        //px转化为dip(dp),一般推荐使用dp，而不是px
        //以下两种方法都行,注意上面是densityDpi,下面是像素密度density
        dpHeight = (pxHeight*160)/densityDpi;
       // dpWidth = (pxWidth*160)/densityDpi;

//      dpHeightA = (int)(pxHeight / density +0.5f);
//      dpWidthA = (int)(pxWidth / density +0.5f);

//      pxHeight = windowManager.getDefaultDisplay().getHeight(); //这个得出的是 像素，也就是跟上面的 pxHeight相等
//      pxWidth = windowManager.getDefaultDisplay().getWidth();
    }
}
