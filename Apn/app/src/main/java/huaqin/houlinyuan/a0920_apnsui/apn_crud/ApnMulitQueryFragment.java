package huaqin.houlinyuan.a0920_apnsui.apn_crud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
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

import java.util.ArrayList;
import java.util.HashMap;

import huaqin.houlinyuan.a0920_apnsui.BaseActivity;
import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.ui.adapters.BasePagerAdapter;
import huaqin.houlinyuan.a0920_apnsui.ui.fragments.NotesListFragment;

public class ApnMulitQueryFragment extends NotesListFragment {
    private static final String TAG = "hlyApnQueryFragment";
    private static final String TAGA = "FFAAApnQueryFragment";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ContentResolver resolver;

    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private int pxHeight;
    private int pxWidth;
    private int densityDpi;
    private float density;
    private int dpHeight;
    private int dpWidth;

    private ArrayList<String> attributeNameList;

    private HashMap<String, EditText> query_headedittext;
    private ArrayList<String> attrName_headSelectedList;

    private HashMap<String, EditText> query_footedittext;
    private ArrayList<String> attrName_footSelectedList;

    private String[] attrTypeArray;
    private boolean[] typeItemState;
    private int attrTypeList_size;

    private String[] mvno_typeArray;
    private boolean[] mvno_typeItemState;
    private int mvno_typeList_size;
    private String mvno_typeValue;

    private ArrayList<String> edittextList;

    private AppCompatCheckBox checkBox;

    //搜索点击事件
    private ArrayList<String> selectedTypeList;
    private MulitQueryCallbacks mCallbacks;
    public interface MulitQueryCallbacks
    {
        public void mulitQeruyStartToFragment(int fragmentId);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallbacks = (MulitQueryCallbacks)activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("initdatabasecount",getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        getWindowDatil();
        attributeNameList = new ArrayList<String>();
        int attributeNameList_size = preferences.getInt("attributeNameList_size",0);
        for(int i = 0;i < attributeNameList_size;i++)
        {
            attributeNameList.add(preferences.getString("attributeNameList_" + i,"null"));
        }
        Log.d(TAG , "-----onCreate------");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apnquery, container, false);
        initHeadView(view);
        initFootView(view);
        //check点击 添加 虚拟卡查询条件事件 begin
        final LinearLayout foot_layout = (LinearLayout)view.findViewById(R.id.apnquery_footlayout);
        checkBox = (AppCompatCheckBox)view.findViewById(R.id.apnquery_virtualquery);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked())
                {
                    if (foot_layout == null)
                    {
                        initFootView(view);
                    }
                    else
                    {
                        foot_layout.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    for (int i = 0;i < attributeNameList.size();i++)
                    {
                        EditText editText = query_footedittext.get(attributeNameList.get(i));
                        if (editText != null)
                        {
                            editText.setText("");
                        }
                    }
                    if (foot_layout != null)
                    {
                        foot_layout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        //check点击 添加 虚拟卡查询条件事件 end

        //给父activity的toolbar上的搜索图标添加点击事件 begin
        final BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.mMenu.findItem(R.id.menu_main_mulitquery).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_main_mulitquery:
                        //按搜索按钮后，系统收集填写信息，验证再进行搜索

                        StringBuffer whereClause = new StringBuffer();
                        whereClause.append(" 1=1 ");

                        for (int i = 0;i < attrName_footSelectedList.size();i++)
                        {
                            if ((attrName_footSelectedList.get(i) != null) &&
                                    (!"carrier".equals(attrName_footSelectedList.get(i).trim())) && (!"mcc".equals(attrName_footSelectedList.get(i).trim()))
                                    && (!"mnc".equals(attrName_footSelectedList.get(i).trim())) && (!"type".equals(attrName_footSelectedList.get(i).trim()))
                                    && (!"mvno_type".equals(attrName_footSelectedList.get(i).trim())))
                            {

                                EditText foot_edittext = query_footedittext.get(attrName_footSelectedList.get(i));
                                if ((foot_edittext.getText() != null) &&(!"".equals(foot_edittext.getText().toString().trim())))
                                {
                                    whereClause.append(" and " + attrName_footSelectedList.get(i).trim() + " like '%" + foot_edittext.getText().toString().trim() + "%' ");

                                    Log.d(TAG, "-----attrName_footSelectedList------attrMame=" + attrName_footSelectedList.get(i));
                                }
                            }
                            else if ((attrName_footSelectedList.get(i) != null) && ("mvno_type".equals(attrName_footSelectedList.get(i).trim())))
                            {
                                if ((mvno_typeValue != null) && (!"".equals(mvno_typeValue.trim())))
                                {
                                    whereClause.append(" and " + attrName_footSelectedList.get(i).trim() + " = '" + mvno_typeValue + "' ");
                                }
                            }
                        }
                        Log.d(TAG, "----------Search contentValues=" + attrName_footSelectedList.size());
                        for (int i = 0;i < attrName_headSelectedList.size();i++)
                        {

                            if ((attrName_headSelectedList.get(i) != null) &&
                                    (("carrier".equals(attrName_headSelectedList.get(i).trim())) || ("mcc".equals(attrName_headSelectedList.get(i).trim())) || ("mnc".equals(attrName_headSelectedList.get(i).trim()))))
                            {
                                EditText head_edittext = query_headedittext.get(attrName_headSelectedList.get(i));

                                if ((head_edittext.getText() != null) &&(!"".equals(head_edittext.getText().toString().trim())))
                                {
                                    whereClause.append(" and " + attrName_headSelectedList.get(i) + " like '%" + head_edittext.getText().toString().trim() + "%'");
                                    Log.d(TAG, "-----attrName_headSelectedList------attrMame=" + attrName_headSelectedList.get(i));
                                }
                            }

                        }

                        //获取选取的type值
                        if (selectedTypeList != null)
                        {
                            for (int i = 0;i < selectedTypeList.size();i++)
                            {
                                if ((selectedTypeList.get(i) != null) && (!"".equals(selectedTypeList.get(i).trim())))
                                {
                                    whereClause.append(" and type = '" + selectedTypeList.get(i) + "' ");
                                }
                            }
                        }


                        Log.d(TAG, "----------Search contentValues=" + attrName_headSelectedList.size());
                        Log.d(TAG, "----------Search contentValues=" + whereClause.toString());
                        //获取到whereclause后存储值到sp中，并且启动apnlistshowfragment begin
                        editor = preferences.edit();
                        editor.putString("whereCalues", whereClause.toString());
                        editor.commit();
                        baseActivity.setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
                        mCallbacks.mulitQeruyStartToFragment(BasePagerAdapter.APNLISTSHOW_FUNCTION);
                        //获取到whereclause后存储值到sp中，并且启动apnlistshowfragment end
                        break;
                }
                return false;
            }
        });
        //给父activity的toolbar上的搜索图标添加点击事件 end
        Log.d(TAG , "-----onCreateView------");
        return view;
    }
    public void initHeadView(View view)
    {
        //head 部分组件添加 begin
        query_headedittext = new HashMap<String, EditText>();
        attrName_headSelectedList = new ArrayList<String>();
        //获取type类型列表
        attrTypeList_size = preferences.getInt("attrTypeList_size",0);
        attrTypeArray = new String[attrTypeList_size];
        for(int i = 0;i < attrTypeArray.length;i++)
        {
            attrTypeArray[i] = preferences.getString("attrTypeList_" + i,"null");
            Log.d(TAG , "-----onCreate------attrTypeList=" + attrTypeArray[i]);
        }

        LinearLayout head_layout = (LinearLayout)view.findViewById(R.id.apnquery_headlayout);

        //直从其中挑选实体卡查询条件 carrier、mnc、mcc
        for (int i = 0;i < attributeNameList.size();i++)
        {
            if ((attributeNameList.get(i) != null) &&
                    (("carrier".equals(attributeNameList.get(i).trim())) || ("mcc".equals(attributeNameList.get(i).trim()))
                            || ("mnc".equals(attributeNameList.get(i).trim())))) {

                LinearLayout branch_layout = new LinearLayout(getActivity());
                branch_layout.setOrientation(LinearLayout.VERTICAL);
                branch_layout.setMinimumHeight(dpHeight / 15);

                TextView attrName_textview = new TextView(getActivity());
                attrName_textview.setText(attributeNameList.get(i));
                attrName_textview.setTextSize(14);
                attrName_textview.getPaint().setFakeBoldText(true);

                EditText attrValue_edittext = new EditText(getActivity());
                attrValue_edittext.setMaxLines(1);

                branch_layout.setPadding(16, 16, 0, 0);
                branch_layout.addView(attrName_textview);
                branch_layout.addView(attrValue_edittext);
                head_layout.addView(branch_layout);
                attrName_headSelectedList.add(attributeNameList.get(i));
                query_headedittext.put(attributeNameList.get(i), attrValue_edittext);
            }
            //给Type设置多选
            else if ((attributeNameList.get(i) != null) && ("type".equals(attributeNameList.get(i).trim())))
            {
                LinearLayout branch_layout = new LinearLayout(getActivity());
                branch_layout.setOrientation(LinearLayout.VERTICAL);
                branch_layout.setMinimumHeight(dpHeight / 15);

                final TextView attrName_textview = new TextView(getActivity());
                attrName_textview.setText(attributeNameList.get(i));
                attrName_textview.setTextSize(14);
                attrName_textview.getPaint().setFakeBoldText(true);

                final TextView attrValue_textview = new TextView(getActivity());
                attrValue_textview.setMaxLines(1);
                attrValue_textview.setText("点击选择Type类型");
//                attrValue_edittext.setEnabled(false);

                //attrValue_edittext 设置点击监听事件，弹出多选dialog
                attrValue_textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //创建 多选 dialog begin
                        edittextList = new ArrayList<String>();
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
                                    edittextList.add(attrTypeArray[i]);
                                }
                                else
                                {
                                    edittextList.remove(attrTypeArray[i]);
                                }
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                StringBuffer edittext = new StringBuffer();
                                for (int k = 0;k < edittextList.size();k++) {
                                    if (k == 0)
                                    {
                                        edittext.append(edittextList.get(k));
                                    }
                                    else
                                    {
                                        edittext.append("," + edittextList.get(k));
                                    }
                                }
                                attrValue_textview.setText(edittext.toString());
                                //   mCallbacks.sendSelectedTypeList(edittextList);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                attrValue_textview.setText("");
                                //mCallbacks.sendSelectedTypeList(null);
                            }
                        });
                        builder.create().show();
                        //创建 多选 dialog end
                    }
                });

                branch_layout.setPadding(16, 16, 0, 0);
                branch_layout.addView(attrName_textview);
                branch_layout.addView(attrValue_textview);
                head_layout.addView(branch_layout);
//                attrName_headSelectedList.add(attributeNameList.get(i));
//                query_headedittext.put(attributeNameList.get(i), attrValue_edittext);
            }
        }
        //head 部分组件添加 end
    }
    public void initFootView(View view)
    {
        //foot 部分组件添加 begin
        LinearLayout foot_layout = (LinearLayout)view.findViewById(R.id.apnquery_footlayout);
        query_footedittext = new HashMap<String, EditText>();
        attrName_footSelectedList = new ArrayList<String>();
        //获取mvno_type类型列表
        mvno_typeList_size = preferences.getInt("mvno_typeList_size",0);
        mvno_typeArray = new String[mvno_typeList_size];
        for(int i = 0;i < mvno_typeArray.length;i++)
        {
            mvno_typeArray[i] = preferences.getString("mvno_typeList_" + i,"null");
            Log.d(TAG , "-----onCreate------mvno_typeArray=" + mvno_typeArray[i]);
        }

        //直从其中挑选虚拟卡查询条件
        for (int i = 0;i < attributeNameList.size();i++)
        {
            if ((attributeNameList.get(i) != null) &&
                    (!"carrier".equals(attributeNameList.get(i).trim())) && (!"mcc".equals(attributeNameList.get(i).trim()))
                    && (!"mnc".equals(attributeNameList.get(i).trim())) && (!"type".equals(attributeNameList.get(i).trim())) && (!"mvno_type".equals(attributeNameList.get(i).trim())))
            {

                LinearLayout branch_layout = new LinearLayout(getActivity());
                branch_layout.setOrientation(LinearLayout.VERTICAL);
                branch_layout.setMinimumHeight(dpHeight / 15);

                TextView attrName_textview = new TextView(getActivity());
                attrName_textview.setText(attributeNameList.get(i));
                attrName_textview.setTextSize(14);
                attrName_textview.getPaint().setFakeBoldText(true);

                EditText attrValue_edittext = new EditText(getActivity());
                attrValue_edittext.setMaxLines(1);

                branch_layout.setPadding(16, 16, 0, 0);
                branch_layout.addView(attrName_textview);
                branch_layout.addView(attrValue_edittext);
                foot_layout.addView(branch_layout);
                attrName_footSelectedList.add(attributeNameList.get(i));
                query_footedittext.put(attributeNameList.get(i), attrValue_edittext);
            }
            else if ((attributeNameList.get(i) != null) && ("mvno_type".equals(attributeNameList.get(i).trim())))
            {
                LinearLayout branch_layout = new LinearLayout(getActivity());
                branch_layout.setOrientation(LinearLayout.VERTICAL);
                branch_layout.setMinimumHeight(dpHeight / 15);

                TextView attrName_textview = new TextView(getActivity());
                attrName_textview.setText(attributeNameList.get(i));
                attrName_textview.setTextSize(14);
                attrName_textview.getPaint().setFakeBoldText(true);

                final TextView attrValue_textview = new TextView(getActivity());
                attrValue_textview.setMaxLines(1);
                attrValue_textview.setText("点击添加mvno_type类型");

                //attrValue_textview 设置点击监听事件，弹出多选dialog
                attrValue_textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //创建 多选 dialog begin
                        mvno_typeValue = new String();
                        attrValue_textview.setText("");
                        mvno_typeItemState = new boolean[mvno_typeList_size];
                        for (int i = 0;i < mvno_typeList_size;i++)
                        {
                            mvno_typeItemState[i] = false;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setIcon(R.drawable.singlecheck);
                        builder.setTitle("选择mvno_type");
                        builder.setSingleChoiceItems(mvno_typeArray, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mvno_typeValue = mvno_typeArray[i];
                                dialogInterface.dismiss();
                                attrValue_textview.setText(mvno_typeValue);
                                //    mCallbacks.sendChiocedMvnoType(mvno_typeValue);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                attrValue_textview.setText("");
                                //  mCallbacks.sendChiocedMvnoType(null);
                            }
                        });
                        builder.create().show();
                        //创建 多选 dialog end
                    }
                });
                branch_layout.setPadding(16, 16, 0, 0);
                branch_layout.addView(attrName_textview);
                branch_layout.addView(attrValue_textview);
                foot_layout.addView(branch_layout);
                attrName_footSelectedList.add(attributeNameList.get(i));
            }
        }


        //foot 部分组件添加 end


    }

    public static ApnMulitQueryFragment newInstance() {
        return new ApnMulitQueryFragment();
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_apnquery;
    }

    @Override
    protected int getNumColumns() {
        return 0;
    }
    public void getWindowDatil()
    {
        windowManager = getActivity().getWindowManager();
        displayMetrics = this.getResources().getDisplayMetrics();
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
}
