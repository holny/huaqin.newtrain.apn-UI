package huaqin.houlinyuan.a0920_apnsui.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.ApnAddFragment;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.ApnListShowFragment;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.ApnMulitQueryFragment;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.HomeFragment;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.InitDataBaseFragment;
import huaqin.houlinyuan.a0920_apnsui.sim.SimAFragment;
import huaqin.houlinyuan.a0920_apnsui.sim.SimBFragment;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * Pager adapter for main activity.
 */
public class BasePagerAdapter extends FragmentStatePagerAdapter {
	private final String TAG = "hlyBasePagerAdapter";
	private int NUM_ITEMS = 0;

	//表示各个功能的ID
	private int mfounctionid;
	public static final int INITDATABASE_FUNCTION = 0;
	public static final int HOME_FUNCTION = 1;
	public static final int APNLISTSHOW_FUNCTION = 2;
	public static final int APNADD_FUNCTION = 3;
	public static final int APNMULITQUERY_FUNCTION = 4;
	public static final int SIMINFO_FUNCTION = 5;


	public static final int INITDATABASE_POS = 0;
	public static final int HOME_POS = 0;
	public static final int APNLISTSHOW_POS = 0;
	public static final int APNADD_POS = 0;
	public static final int APNMULITQUERY_POS = 0;

	public static final int SIMAINFO_POS = 0;
	public static final int SIMBINFO_POS = 1;
	private FragmentManager fm;
	private Context context;

	public BasePagerAdapter(Context context, FragmentManager fm, int founctionId) {
		super(fm);
		this.fm = fm;
		FragmentTransaction transaction = fm.beginTransaction();
		//把所有缓存碎片都删了。

//		if (fm.getFragments() != null) {
//			for (Fragment childFragment : fm.getFragments()) {
//				transaction.detach(childFragment);
//				transaction.commit();
//				Log.d(TAG, "------transaction.detach------ " );
//			}
//		}
		mfounctionid = 0;
		mfounctionid = founctionId;
		switch (mfounctionid)
		{
			case INITDATABASE_FUNCTION:
				NUM_ITEMS = 1;
				break;
			case HOME_FUNCTION:
				NUM_ITEMS = 1;
				break;
			case APNLISTSHOW_FUNCTION:
				NUM_ITEMS = 1;
				break;
			case APNADD_FUNCTION:
				NUM_ITEMS = 1;
				break;
			case APNMULITQUERY_FUNCTION:
				NUM_ITEMS = 1;
				break;
			case SIMINFO_FUNCTION:
				NUM_ITEMS = 2;
				break;
			default:
				NUM_ITEMS = 0;
				break;
		}

		Log.d(TAG, "------InitPagerAdapter------mfounctionid= " +mfounctionid);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		Log.d(TAG, "------getItem------mfounctionid= " +mfounctionid + "----position=" + position);

		switch (mfounctionid)
		{
			case INITDATABASE_FUNCTION:
				switch (position) {
					case INITDATABASE_POS:
						return InitDataBaseFragment.newInstance();
					default:
						return null;
				}
			case HOME_FUNCTION:
				switch (position) {
					case  HOME_POS:
						return HomeFragment.newInstance();
					default:
						return null;
				}
			case APNLISTSHOW_FUNCTION:
				switch (position) {
					case  APNLISTSHOW_POS:
						return ApnListShowFragment.newInstance();
					default:
						return null;
				}
			case APNADD_FUNCTION:
				switch (position) {
					case  APNADD_POS:
						return ApnAddFragment.newInstance();
					default:
						return null;
				}
			case APNMULITQUERY_FUNCTION:
				switch (position) {
					case  APNLISTSHOW_POS:
						return ApnMulitQueryFragment.newInstance();
					default:
						return null;
				}
			case SIMINFO_FUNCTION:
				switch (position) {
					case  SIMAINFO_POS:
						return SimAFragment.newInstance();
					case  SIMBINFO_POS:
						return SimBFragment.newInstance();
					default:
						return null;
				}
			default:
				return null;

		}

	}

	@Override
	public CharSequence getPageTitle(int position) {
		Log.d(TAG, "------getPageTitle------mfounctionid= " +mfounctionid + "----position=" + position);
		switch (mfounctionid)
		{
			case INITDATABASE_FUNCTION:
				switch (position) {
					case INITDATABASE_POS:
						return context.getString(R.string.reinitdb);
					default:
						return "";
				}
			case HOME_FUNCTION:
				switch (position) {
					case  HOME_POS:
					//
						return context.getString(R.string.home);
					default:
						return "";
				}
			case APNLISTSHOW_FUNCTION:
				switch (position) {
					case  APNLISTSHOW_POS:
						return context.getString(R.string.apnlist_show);
					default:
						return "";
				}
			case APNADD_FUNCTION:
				switch (position) {
					case  APNADD_POS:
						return context.getString(R.string.apn_add);
					default:
						return "";
				}
			case APNMULITQUERY_FUNCTION:
				switch (position) {
					case  APNMULITQUERY_POS:
						return context.getString(R.string.mulitquery);
					default:
						return "";

				}
			case SIMINFO_FUNCTION:
				switch (position) {
					case  SIMAINFO_POS:
						return context.getString(R.string.simatitle);
					case  SIMBINFO_POS:
						return context.getString(R.string.simbtitle);
					default:
						return "";
				}
			default:
				return "";

		}

	}

	@Override
	public int getCount() {
		Log.d(TAG, "------getCount------NUM_ITEMS= " +NUM_ITEMS);
		return NUM_ITEMS;
	}
}
