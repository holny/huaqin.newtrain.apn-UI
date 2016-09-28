package huaqin.houlinyuan.a0920_apnsui.apn_crud;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yalantis.taurus.PullToRefreshView;

import java.util.ArrayList;
import java.util.Map;

import huaqin.houlinyuan.a0920_apnsui.BaseActivity;
import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.helper.OnStartDragListener;
import huaqin.houlinyuan.a0920_apnsui.helper.SimpleItemTouchHelperCallback;
import huaqin.houlinyuan.a0920_apnsui.ui.adapters.BasePagerAdapter;
import huaqin.houlinyuan.a0920_apnsui.ui.adapters.NotesAdapter;
import huaqin.houlinyuan.a0920_apnsui.ui.animator.RecylerItemAnimator;
import huaqin.houlinyuan.a0920_apnsui.ui.fragments.NotesListFragment;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * All items fragment.
 */
public class ApnListShowFragment extends NotesListFragment implements OnStartDragListener {
	private static final String TAG = "hlyApnListShowFragment";
	private static final String TAGA = "DDAApnListShowFragment";

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private ContentResolver resolver;
	private ArrayList<Map<String,String>> apnlist;
	private String whereCalues;
	private Resources resources;
	private View view;
	//下拉刷新时间
	private PullToRefreshView mPullToRefreshView;
	public static final int REFRESH_DELAY = 2000;

	private ItemTouchHelper mItemTouchHelper;

	private StaggeredGridLayoutManager sm;
	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
		Log.d(TAGA,"------onStartDrag---------");
		mItemTouchHelper.startDrag(viewHolder);
	}



	public static ApnListShowFragment newInstance() {
		return new ApnListShowFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAGA,"------onCreate---------");
		resolver = getActivity().getContentResolver();
		preferences = getActivity().getSharedPreferences("initdatabasecount", Context.MODE_PRIVATE);
		editor = preferences.edit();
		resources = getResources();

		whereCalues = preferences.getString("whereCalues", "");
		editor.remove("whereCalues");
		Log.d(TAGA , "-----onCreate------whereCalues= " + whereCalues);
		if ((whereCalues != null) && ("".equals(whereCalues.trim())))
		{
			whereCalues = null;
		}

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.d(TAGA,"------onCreateView---------");

		view = inflater.inflate(R.layout.fragment_apnlistshow, container, false);
		//initListView(whereCalues);

		// Setup list
		final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.apnlistshow_recylerview);
		sm = new StaggeredGridLayoutManager(getNumColumns(),
				StaggeredGridLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(sm);
		final NotesAdapter adapter = new NotesAdapter(getActivity(),whereCalues, newInstance());
		recyclerView.setAdapter(adapter);
		recyclerView.setItemAnimator(new RecylerItemAnimator());
		//下拉刷新
		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.apnlistshow_refresh_layout);
		mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mPullToRefreshView.postDelayed(new Runnable() {
					@Override
					public void run() {
						mPullToRefreshView.setRefreshing(false);
						Log.d(TAGA,"------mPullToRefreshView---------");
						//adapter.notify();
						BaseActivity baseActivity = (BaseActivity)getActivity();
						baseActivity.setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);

					}
				}, REFRESH_DELAY);
			}
		});
		Log.d(TAG , "-----onCreateView------");

		ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
		mItemTouchHelper = new ItemTouchHelper(callback);
		mItemTouchHelper.attachToRecyclerView(recyclerView);

		return view;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_home;
	}

	@Override
	protected int getNumColumns() {
		return 1;
	}

}
