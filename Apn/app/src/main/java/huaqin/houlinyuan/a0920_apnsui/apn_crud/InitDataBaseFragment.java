package huaqin.houlinyuan.a0920_apnsui.apn_crud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.ui.adapters.BasePagerAdapter;
import huaqin.houlinyuan.a0920_apnsui.ui.fragments.NotesListFragment;

public class InitDataBaseFragment extends NotesListFragment {
	private static final String TAG = "hlyInitDataBaseFragment";
	private static final String TAGA = "FFAA";

	private static final int INIT_DATABASE_ERROR = 1231;
	private static final int INIT_DATABASE_GETATTRNAMELIST_DOING_MSG = 1232;
	private static final int INIT_DATABASE_GETATTRNAMELIST_DONE_MSG = 1233;
	private static final int INIT_DATABASE_INSERT_DOING_MSG = 1234;
	private static final int INIT_DATABASE_INSERT_DONE_MSG = 1235;

	public static Handler initDBHandler;

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private ContentResolver resolver;
	private  int initDatabase_count;

	private InitDBCallbacks mCallbacks;
	public interface InitDBCallbacks
	{
		public void initDBStartToFragment(int fragmentId);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mCallbacks = (InitDBCallbacks)activity;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getActivity().getSharedPreferences("initdatabasecount",getActivity().MODE_PRIVATE);
		editor = preferences.edit();
		initDBProgressDialog();
		Intent intent = new Intent(getActivity(), huaqin.houlinyuan.a0920_apnsui.database.InitDatabaseIntentService.class);
		getActivity().startService(intent);
		Log.d(TAG , "-----onCreate------");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_initdatabase, container, false);
		Log.d(TAG , "-----onCreateView------");
		return view;
	}
	//初始化数据库
	public void initDBProgressDialog()
	{
		final int MAX_PROGRESS = 100;
		final int MAX_GETATTRPROGRESS = 10;

		final ProgressDialog progressDialog_getAttrNameList = new ProgressDialog(getActivity());
		progressDialog_getAttrNameList.setProgress(0);
		progressDialog_getAttrNameList.setTitle("apns-conf.xml文件字段列表读取中");
		progressDialog_getAttrNameList.setMessage("提取中...");
		progressDialog_getAttrNameList.setIndeterminate(true);
		progressDialog_getAttrNameList.setCancelable(false);
		progressDialog_getAttrNameList.show();
		//
		LinearLayout linearLayout_getAttrNameList = new LinearLayout(getActivity());
		final TextView msg_textView_getAttrNameList = new TextView(getActivity());
		linearLayout_getAttrNameList.addView(msg_textView_getAttrNameList);
		LinearLayout.LayoutParams layoutParams_getAttrNameList = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		progressDialog_getAttrNameList.addContentView(linearLayout_getAttrNameList, layoutParams_getAttrNameList);

//

		final ProgressDialog progressDialog_insert = new ProgressDialog(getActivity());
		progressDialog_insert.setTitle("apns-conf.xml文件解析并插入数据库中");
		progressDialog_insert.setMessage("解析并插入数据库中...");
		progressDialog_insert.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog_insert.setCancelable(false);
		//
		final LinearLayout linearLayout_insert = new LinearLayout(getActivity());
		final TextView msg_textView_insert = new TextView(getActivity());
		linearLayout_insert.addView(msg_textView_insert);
		final LinearLayout.LayoutParams layoutParams_insert = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);


		initDBHandler = new Handler()
		{
			@Override
			public void handleMessage (Message msg)
			{
				if (msg.what == INIT_DATABASE_ERROR)
				{
					Bundle bundle = msg.getData();
					String warning = bundle.getString("warning");
					Toast toast = Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT);
					toast.show();
					if (progressDialog_getAttrNameList.isShowing()) {
						progressDialog_getAttrNameList.cancel();
					}
					if (progressDialog_insert.isShowing()) {
						progressDialog_insert.cancel();
					}
				}
				else if (msg.what == INIT_DATABASE_GETATTRNAMELIST_DOING_MSG)
				{
					Bundle bundle = msg.getData();
					String doing = bundle.getString("doing");
					msg_textView_getAttrNameList.setText(doing);
				}
				else if (msg.what == INIT_DATABASE_GETATTRNAMELIST_DONE_MSG)
				{
					Bundle bundle = msg.getData();
					int APN_COUNT = bundle.getInt("apncount");
					progressDialog_getAttrNameList.setMessage("提取字段列表完成!");
					Toast toast = Toast.makeText(getActivity(), "提取字段列表完成!", Toast.LENGTH_SHORT);
					toast.show();
					progressDialog_getAttrNameList.cancel();
					//初始
					progressDialog_insert.show();
					progressDialog_insert.addContentView(linearLayout_insert, layoutParams_insert);
					Log.d(TAGA,"-------getDone-----");
					progressDialog_insert.setProgress(0);
					progressDialog_insert.setMax(APN_COUNT);
				}
				else if (msg.what == INIT_DATABASE_INSERT_DOING_MSG)
				{
					Bundle bundle = msg.getData();
					String doing = bundle.getString("doing");
					msg_textView_insert.setText(doing);

					int INSERT_COUNT = bundle.getInt("insertcount");

					// int PROGRESS = (int)((INSERT_COUNT / APN_COUNT) * (float)MAX_PROGRESS);
					Log.d(TAGA,"------PROGRESS="+INSERT_COUNT + "--INSERT_COUNT=" + INSERT_COUNT);
					progressDialog_insert.setProgress(INSERT_COUNT);
				}
				else if (msg.what == INIT_DATABASE_INSERT_DONE_MSG)
				{
					Toast toast = Toast.makeText(getActivity(), "解析XML,插入数据库完成!", Toast.LENGTH_SHORT);
					toast.show();
					progressDialog_insert.setProgress(MAX_PROGRESS);
					progressDialog_insert.cancel();
					msg_textView_insert.setText("解析XML,插入数据库完成!");
					mCallbacks.initDBStartToFragment(BasePagerAdapter.HOME_FUNCTION);
				}
			}
		};


	}

	public static InitDataBaseFragment newInstance() {
		return new InitDataBaseFragment();
	}
	//返回给父类，顺变实例化layout
	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_initdatabase;
	}

	@Override
	protected int getNumColumns() {
		return 1;
	}

}
