package huaqin.houlinyuan.a0920_apnsui.apn_crud;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.ui.fragments.NotesListFragment;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * All items fragment.
 */
public class HomeFragment extends NotesListFragment {
	private static final String TAG = "hlyHomeFragment";
	private static final String TAGA = "FFAA";


	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private ContentResolver resolver;

	public static HomeFragment newInstance() {
		return new HomeFragment();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getActivity().getSharedPreferences("initdatabasecount",getActivity().MODE_PRIVATE);
		editor = preferences.edit();
		Log.d(TAG , "-----onCreate------");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		TextView home_apn1 = (TextView)view.findViewById(R.id.home_apn1);
		TextPaint tp = home_apn1.getPaint();
		tp.setFakeBoldText(true);
		Log.d(TAG , "-----onCreateView------");
		return view;
	}
	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_home;
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}


}
