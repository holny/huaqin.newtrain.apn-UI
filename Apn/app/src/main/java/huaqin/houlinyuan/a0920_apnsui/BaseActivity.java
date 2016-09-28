package huaqin.houlinyuan.a0920_apnsui;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import huaqin.houlinyuan.a0920_apnsui.apn_crud.ApnAddFragment;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.ApnMulitQueryFragment;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.InitDataBaseFragment;
import huaqin.houlinyuan.a0920_apnsui.ui.Fab;
import huaqin.houlinyuan.a0920_apnsui.ui.adapters.BasePagerAdapter;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * Main activity for material sheet fab sample.
 */
public class BaseActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,InitDataBaseFragment.InitDBCallbacks,ApnMulitQueryFragment.MulitQueryCallbacks,ApnAddFragment.ApnAddCallbacks {

	private final String TAG = "hlyMainActivity";
	private static final String TAGA = "hlyDDAABaseActivity";

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private MaterialSheetFab materialSheetFab;
	private int statusBarColor;
	private NavigationView mNavigationView;

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private ContentResolver resolver;
	private  int initDatabase_count;
	public Menu mMenu;

	private ViewPager viewpager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.notes);
		setContentView(R.layout.activity_main);
		mNavigationView = (NavigationView)findViewById(R.id.navigation_view);
		setupActionBar();
		setupDrawer();
		setupFab();

		//看是否第一次打开应用，是第一次就初始化构建数据库，解析xml
		preferences = getSharedPreferences("initdatabasecount",MODE_PRIVATE);

		initDatabase_count = preferences.getInt("initcount", 0);
		if (initDatabase_count < 1)
		{
			Log.d(TAG, "------第一次进入应用------");
			setupTabs(BasePagerAdapter.INITDATABASE_FUNCTION);
		}
		else {
			setupTabs(BasePagerAdapter.HOME_FUNCTION);
		}
		//

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();

	}
	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if (materialSheetFab.isSheetVisible()) {
			materialSheetFab.hideSheet();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Sets up the action bar.
	 */
	private void setupActionBar() {
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Sets up the navigation drawer.
	 */
	private void setupDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.opendrawer,
				R.string.closedrawer);
		drawerLayout.setDrawerListener(drawerToggle);

		//侧滑菜单按钮点击事件
		mNavigationView.setNavigationItemSelectedListener(

				new NavigationView.OnNavigationItemSelectedListener()
				{

					private MenuItem mPreMenuItem;

					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{
						if (mPreMenuItem != null)
						{
							mPreMenuItem.setChecked(false);
						}
						menuItem.setChecked(true);
						drawerLayout.closeDrawers();
						mPreMenuItem = menuItem;

						//根据menu item 的 id 添加跳转事件
						switch (menuItem.getItemId())
						{
							case R.id.drawer_menu_home:
								mMenu.findItem(R.id.menu_main_search).setVisible(false);
								mMenu.findItem(R.id.menu_main_add).setVisible(false);
								mMenu.findItem(R.id.menu_main_mulitquery).setVisible(false);
								mMenu.findItem(R.id.menu_main_siminfotoapnlist).setVisible(false);
								setupTabs(BasePagerAdapter.HOME_FUNCTION);
								break;
							case R.id.drawer_menu_showapnlist:
								mMenu.findItem(R.id.menu_main_search).setVisible(true);
								mMenu.findItem(R.id.menu_main_add).setVisible(false);
								mMenu.findItem(R.id.menu_main_mulitquery).setVisible(false);
								mMenu.findItem(R.id.menu_main_siminfotoapnlist).setVisible(false);
								setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
								break;
							case R.id.drawer_menu_apnadd:
								mMenu.findItem(R.id.menu_main_search).setVisible(false);
								mMenu.findItem(R.id.menu_main_add).setVisible(true);
								mMenu.findItem(R.id.menu_main_mulitquery).setVisible(false);
								mMenu.findItem(R.id.menu_main_siminfotoapnlist).setVisible(false);
								setupTabs(BasePagerAdapter.APNADD_FUNCTION);
								break;
							case R.id.drawer_menu_initdatabase:
								mMenu.findItem(R.id.menu_main_search).setVisible(false);
								mMenu.findItem(R.id.menu_main_add).setVisible(false);
								mMenu.findItem(R.id.menu_main_mulitquery).setVisible(false);
								mMenu.findItem(R.id.menu_main_siminfotoapnlist).setVisible(false);
								setupTabs(BasePagerAdapter.INITDATABASE_FUNCTION);
								break;
							case R.id.drawer_menu_mulitquery:
								mMenu.findItem(R.id.menu_main_search).setVisible(false);
								mMenu.findItem(R.id.menu_main_add).setVisible(false);
								mMenu.findItem(R.id.menu_main_mulitquery).setVisible(true);
								mMenu.findItem(R.id.menu_main_siminfotoapnlist).setVisible(false);
								setupTabs(BasePagerAdapter.APNMULITQUERY_FUNCTION);
								break;
							case R.id.drawer_menu_siminfo:
								mMenu.findItem(R.id.menu_main_search).setVisible(false);
								mMenu.findItem(R.id.menu_main_add).setVisible(false);
								mMenu.findItem(R.id.menu_main_mulitquery).setVisible(false);
								mMenu.findItem(R.id.menu_main_siminfotoapnlist).setVisible(true);
								setupTabs(BasePagerAdapter.SIMINFO_FUNCTION);
								break;

						}


						return true;
					}
				});
	}

	/**
	 * Sets up the tabs.
	 */
	public void setupTabs(int fragmentShowAction) {
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		Log.d(TAG, "------setupTabs------");
		// Setup view pager
		BasePagerAdapter basePagerAdapter = new BasePagerAdapter(this, getSupportFragmentManager(), fragmentShowAction);
		viewpager.setAdapter(basePagerAdapter);
		viewpager.setOffscreenPageLimit(basePagerAdapter.getCount());

		updatePage(fragmentShowAction);
		// Setup tab layout

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewpager);

		viewpager.refreshDrawableState();
		viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
			}

			@Override
			public void onPageSelected(int i) {
				//updatePage(i);
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});

	}

	/**
	 * Sets up the Floating action button.
	 */
	private void setupFab() {

		Fab fab = (Fab) findViewById(R.id.fab);
		View sheetView = findViewById(R.id.fab_sheet);
		View overlay = findViewById(R.id.overlay);
		int sheetColor = getResources().getColor(R.color.background_card);
		int fabColor = getResources().getColor(R.color.theme_accent);

		// Create material sheet FAB
		materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

		// Set material sheet event listener
		materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
			@Override
			public void onShowSheet() {
				// Save current status bar color
				statusBarColor = getStatusBarColor();
				// Set darker status bar color to match the dim overlay
				setStatusBarColor(getResources().getColor(R.color.theme_primary_dark2));
			}

			@Override
			public void onHideSheet() {
				// Restore status bar color
				setStatusBarColor(statusBarColor);
			}
		});

		// Set material sheet item click listeners
		findViewById(R.id.fab_sheet_item_home).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "------fab_sheet_item_home--click----");
				setupTabs(BasePagerAdapter.HOME_FUNCTION);
				mNavigationView.setCheckedItem(R.id.drawer_menu_home);
			}
		});
		findViewById(R.id.fab_sheet_item_reinitdb).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "------fab_sheet_item_reinitdb--click----");
				setupTabs(BasePagerAdapter.INITDATABASE_FUNCTION);
				mNavigationView.setCheckedItem(R.id.drawer_menu_initdatabase);
			}
		});
		findViewById(R.id.fab_sheet_item_siminfo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "------fab_sheet_item_siminfo--click----");
				setupTabs(BasePagerAdapter.SIMINFO_FUNCTION);
				mNavigationView.setCheckedItem(R.id.drawer_menu_siminfo);
				materialSheetFab.hideSheet();
			}
		});
		findViewById(R.id.fab_sheet_item_mulitquery).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "------fab_sheet_item_mulitquery--click----");
				setupTabs(BasePagerAdapter.APNMULITQUERY_FUNCTION);
				mNavigationView.setCheckedItem(R.id.drawer_menu_mulitquery);
				materialSheetFab.hideSheet();
			}
		});
		findViewById(R.id.fab_sheet_item_apnadd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "------fab_sheet_item_apnadd--click----");
				setupTabs(BasePagerAdapter.APNADD_FUNCTION);
				mNavigationView.setCheckedItem(R.id.drawer_menu_apnadd);
				materialSheetFab.hideSheet();
			}
		});
		findViewById(R.id.fab_sheet_item_apnlist).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "------fab_sheet_item_apnlist--click----");
				setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
				mNavigationView.setCheckedItem(R.id.drawer_menu_showapnlist);
				materialSheetFab.hideSheet();
			}
		});
	}
//	//悬浮fab 菜单item点击事件
//	@Override
//	public void onClick(View v) {
//		Toast.makeText(this, R.string.sheet_item_pressed, Toast.LENGTH_SHORT).show();
//		Log.d(TAG, "------Fabmenu click------");
//		if (v != null)
//		{
//			switch (v.getId())
//			{
//				case R.id.fab_sheet_item_home:
//					Log.d(TAG, "------fab_sheet_item_home--click----");
//					setupTabs(BasePagerAdapter.HOME_FUNCTION);
//					mNavigationView.setCheckedItem(R.id.drawer_menu_home);
//					break;
//				case R.id.fab_sheet_item_reinitdb:
//					Log.d(TAG, "------fab_sheet_item_reinitdb--click----");
//					setupTabs(BasePagerAdapter.INITDATABASE_FUNCTION);
//					mNavigationView.setCheckedItem(R.id.drawer_menu_initdatabase);
//					break;
//				case R.id.fab_sheet_item_siminfo:
//					Log.d(TAG, "------fab_sheet_item_siminfo--click----");
//					setupTabs(BasePagerAdapter.SIMINFO_FUNCTION);
//					mNavigationView.setCheckedItem(R.id.drawer_menu_siminfo);
//					break;
//				case R.id.fab_sheet_item_mulitquery:
//					Log.d(TAG, "------fab_sheet_item_mulitquery--click----");
//					setupTabs(BasePagerAdapter.APNMULITQUERY_FUNCTION);
//					mNavigationView.setCheckedItem(R.id.drawer_menu_mulitquery);
//					break;
//				case R.id.fab_sheet_item_apnadd:
//					Log.d(TAG, "------fab_sheet_item_apnadd--click----");
//					setupTabs(BasePagerAdapter.APNADD_FUNCTION);
//					mNavigationView.setCheckedItem(R.id.drawer_menu_apnadd);
//					break;
//				case R.id.fab_sheet_item_apnlist:
//					Log.d(TAG, "------fab_sheet_item_apnlist--click----");
//					setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
//					mNavigationView.setCheckedItem(R.id.drawer_menu_showapnlist);
//					break;
//			}
//		}
//		materialSheetFab.hideSheet();
//	}
	/**
	 * Updates the FAB based on the selected page
	 *
	 * @param selectedPage selected page
	 */
	private void updateFab(int selectedPage) {
		TextView fab_home = (TextView)findViewById(R.id.fab_sheet_item_home);
		TextView fab_reinitdb = (TextView)findViewById(R.id.fab_sheet_item_reinitdb);
		TextView fab_siminfo = (TextView)findViewById(R.id.fab_sheet_item_siminfo);
		TextView fab_mulitquery = (TextView)findViewById(R.id.fab_sheet_item_mulitquery);
		TextView fab_apnadd = (TextView)findViewById(R.id.fab_sheet_item_apnadd);
		TextView fab_apnlist = (TextView)findViewById(R.id.fab_sheet_item_apnlist);
		switch (selectedPage) {
			case BasePagerAdapter.INITDATABASE_FUNCTION:
				materialSheetFab.hideSheetThenFab();
				break;
			case BasePagerAdapter.HOME_FUNCTION:
				materialSheetFab.showFab();
				fab_home.setVisibility(View.GONE);
				fab_reinitdb.setVisibility(View.VISIBLE);
				fab_siminfo.setVisibility(View.VISIBLE);
				fab_mulitquery.setVisibility(View.VISIBLE);
				fab_apnadd.setVisibility(View.VISIBLE);
				fab_apnlist.setVisibility(View.VISIBLE);
				break;
			case BasePagerAdapter.APNLISTSHOW_FUNCTION:
				materialSheetFab.showFab();
				fab_home.setVisibility(View.VISIBLE);
				fab_reinitdb.setVisibility(View.VISIBLE);
				fab_siminfo.setVisibility(View.VISIBLE);
				fab_mulitquery.setVisibility(View.VISIBLE);
				fab_apnadd.setVisibility(View.VISIBLE);
				fab_apnlist.setVisibility(View.GONE);
				break;
			case BasePagerAdapter.APNADD_FUNCTION:
				materialSheetFab.showFab();
				fab_home.setVisibility(View.VISIBLE);
				fab_reinitdb.setVisibility(View.VISIBLE);
				fab_siminfo.setVisibility(View.VISIBLE);
				fab_mulitquery.setVisibility(View.VISIBLE);
				fab_apnadd.setVisibility(View.GONE);
				fab_apnlist.setVisibility(View.VISIBLE);
				break;
			case BasePagerAdapter.APNMULITQUERY_FUNCTION:
				materialSheetFab.showFab();
				fab_home.setVisibility(View.VISIBLE);
				fab_reinitdb.setVisibility(View.VISIBLE);
				fab_siminfo.setVisibility(View.VISIBLE);
				fab_mulitquery.setVisibility(View.GONE);
				fab_apnadd.setVisibility(View.VISIBLE);
				fab_apnlist.setVisibility(View.VISIBLE);
				break;
			case BasePagerAdapter.SIMINFO_FUNCTION:
				materialSheetFab.showFab();
				fab_home.setVisibility(View.VISIBLE);
				fab_reinitdb.setVisibility(View.VISIBLE);
				fab_siminfo.setVisibility(View.GONE);
				fab_mulitquery.setVisibility(View.VISIBLE);
				fab_apnadd.setVisibility(View.VISIBLE);
				fab_apnlist.setVisibility(View.VISIBLE);
				break;
			default:
				materialSheetFab.hideSheetThenFab();
				break;
		}
	}
	private void updateTabView(int selectedPage) {
		TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
		switch (selectedPage) {
			case BasePagerAdapter.INITDATABASE_FUNCTION:
				setTitle(R.string.fab_reinitdb);
				if (tabLayout != null) {
					tabLayout.setVisibility(View.INVISIBLE);
				}
				break;
			case BasePagerAdapter.HOME_FUNCTION:
				setTitle(R.string.fab_home);
				if (tabLayout != null) {
					tabLayout.setVisibility(View.INVISIBLE);
				}
				break;
			case BasePagerAdapter.APNLISTSHOW_FUNCTION:
				setTitle(R.string.fab_apnlist);
				if (tabLayout != null) {
					tabLayout.setVisibility(View.INVISIBLE);
				}
				break;
			case BasePagerAdapter.APNADD_FUNCTION:
				setTitle(R.string.fab_add);
				if (tabLayout != null) {
					tabLayout.setVisibility(View.INVISIBLE);
				}
				break;
			case BasePagerAdapter.APNMULITQUERY_FUNCTION:
				setTitle(R.string.fab_mulitquery);
				if (tabLayout != null) {
					tabLayout.setVisibility(View.INVISIBLE);
				}
				break;
			case BasePagerAdapter.SIMINFO_FUNCTION:
				setTitle(R.string.fab_siminfo);
				if (tabLayout != null) {
					tabLayout.setVisibility(View.VISIBLE);
				}
				break;
			default:
				setTitle(R.string.notes);
				break;
		}
	}
	/**
	 * Called when the selected page changes.
	 *
	 * @param selectedPage selected page
	 */
	private void updatePage(int selectedPage) {
		updateFab(selectedPage);
		updateSnackbar(selectedPage);
		//updateTabView(selectedPage);
	}

	/**
	 * Updates the snackbar based on the selected page
	 *
	 * @param selectedPage selected page
	 */
	private void updateSnackbar(int selectedPage) {
		View snackbar = findViewById(R.id.snackbar);
		switch (selectedPage) {
//		case InitDataBasePagerAdapter.SHARED_POS:
//			snackbar.setVisibility(View.VISIBLE);
//			break;
		case BasePagerAdapter.INITDATABASE_POS:
//		case InitDataBasePagerAdapter.FAVORITES_POS:
		default:
			snackbar.setVisibility(View.GONE);
			break;
		}
	}




	/**
	 * Toggles opening/closing the drawer.
	 */
	private void toggleDrawer() {
		if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
		} else {
			drawerLayout.openDrawer(GravityCompat.START);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		mMenu = menu;
		//控制toolbar上菜单初始状态是隐藏
		mMenu.findItem(R.id.menu_main_search).setVisible(false);
		mMenu.findItem(R.id.menu_main_add).setVisible(false);
		mMenu.findItem(R.id.menu_main_mulitquery).setVisible(false);
		mMenu.findItem(R.id.menu_main_siminfotoapnlist).setVisible(false);


		//给toolbar 上的searchview加属性
		MenuItem search=mMenu.findItem(R.id.menu_main_search);
		search.collapseActionView();
		SearchView searchview=(SearchView) search.getActionView();
		SearchManager mSearchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
		SearchableInfo info=mSearchManager.getSearchableInfo(getComponentName());
		searchview.setSearchableInfo(info); //需要在Xml文件加下建立searchable.xml,搜索框配置文件
		searchview.setOnQueryTextListener(this);
		searchview.setSubmitButtonEnabled(true);
		searchview.setQueryHint("请输入Carrier匹配查询");

		//给sim卡信息fragment上toolbar的图标添加点击事件
		mMenu.findItem(R.id.menu_main_siminfotoapnlist).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId())
				{
					case R.id.menu_main_siminfotoapnlist:
						switch (viewpager.getCurrentItem())
						{
							case BasePagerAdapter.SIMAINFO_POS:
								//利用反射获取mtk代码里的getSubId方法。获取subId
								int SOLTAID = 0;
								try {
									Log.d(TAG, "---------SOLTAID = 0--------");
									Class<?> MtkSubscriptionManager = Class.forName("android.telephony.SubscriptionManager");
									Constructor MtkSubscriptionManagerConstructor = MtkSubscriptionManager.getConstructor(Context.class);
									Object mtkSubscriptionManagerObject = MtkSubscriptionManagerConstructor.newInstance(BaseActivity.this);
									Method mtkSubscriptionManagergetSubId = MtkSubscriptionManager.getMethod("getSubId", int.class);
									int[] result = (int[])mtkSubscriptionManagergetSubId.invoke(mtkSubscriptionManagerObject,SOLTAID);
									int SUBID = result[0];
									//根据subId获取subscriptionInfo
									SubscriptionManager subscriptionManager = SubscriptionManager.from(BaseActivity.this);
									SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(SUBID);
									if (subscriptionInfo != null) {
										String mnc = String.valueOf(subscriptionInfo.getMnc());
										String mcc = String.valueOf(subscriptionInfo.getMcc());
										StringBuffer whereClause = new StringBuffer();
										if (mcc.length() == 1)
										{
											mcc = "0" + mcc.trim();
										}
										whereClause.append(" mcc like '%" + mcc + "%' and mnc like '%" + mnc + "%' ");
										Toast toast1 = Toast.makeText(BaseActivity.this, "whereClause=" + whereClause.toString(), Toast.LENGTH_SHORT);
										toast1.show();

										//新添加，往sp存储查询条件，apnlistshowfragment提取
										editor = preferences.edit();
										editor.putString("whereCalues", whereClause.toString());
										editor.commit();

										//新添加，跳转到apnlistfragment并调整nvmenu光标
										setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
										mNavigationView.setCheckedItem(R.id.drawer_menu_showapnlist);
									}
									else
									{
										Toast toast = Toast.makeText(BaseActivity. this,"SIM卡1错误!", Toast.LENGTH_SHORT);
										toast.show();
									}
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
								} catch (InstantiationException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}

								break;
							case BasePagerAdapter.SIMBINFO_POS:
								//利用反射获取mtk代码里的getSubId方法。获取subId
								int SOLTBID = 1;
								try {
									Log.d(TAG, "---------SOLTBID = 1--------");
									Class<?> MtkSubscriptionManager = Class.forName("android.telephony.SubscriptionManager");
									Constructor MtkSubscriptionManagerConstructor = MtkSubscriptionManager.getConstructor(Context.class);
									Object mtkSubscriptionManagerObject = MtkSubscriptionManagerConstructor.newInstance(BaseActivity.this);
									Method mtkSubscriptionManagergetSubId = MtkSubscriptionManager.getMethod("getSubId", int.class);
									int[] result = (int[])mtkSubscriptionManagergetSubId.invoke(mtkSubscriptionManagerObject,SOLTBID);
									int SUBID = result[0];
									//根据subId获取subscriptionInfo
									SubscriptionManager subscriptionManager = SubscriptionManager.from(BaseActivity.this);
									SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(SUBID);
									if (subscriptionInfo != null) {
										String mnc = String.valueOf(subscriptionInfo.getMnc());
										String mcc = String.valueOf(subscriptionInfo.getMcc());
										if (mnc.trim().length() == 1)
										{

											mnc = "0" + mnc.trim();
										}
										StringBuffer whereClause = new StringBuffer();
										whereClause.append(" mcc like '%" + mcc + "%' and mnc like '%" + mnc + "%' ");
										Toast toast1 = Toast.makeText(BaseActivity.this, "whereClause=" + whereClause.toString(), Toast.LENGTH_SHORT);
										toast1.show();

										//新添加，往sp存储查询条件，apnlistshowfragment提取
										editor = preferences.edit();
										editor.putString("whereCalues", whereClause.toString());
										editor.commit();

										//新添加，跳转到apnlistfragment并调整nvmenu光标
										setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
										mNavigationView.setCheckedItem(R.id.drawer_menu_showapnlist);
									}
									else
									{
										Toast toast = Toast.makeText(BaseActivity. this,"SIM卡2错误!", Toast.LENGTH_SHORT);
										toast.show();
									}
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
								} catch (InstantiationException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								break;
						}
						break;
				}

				return false;
			}
		});



		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggleDrawer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private int getStatusBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return getWindow().getStatusBarColor();
		}
		return 0;
	}

	private void setStatusBarColor(int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(color);
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		String whereCalues = "carrier like '%" + query.trim() + "%'";
		Log.d(TAGA, "------onQueryTextSubmit------query="+whereCalues);
		Toast toast = Toast.makeText(this, "onQueryTextSubmit!", Toast.LENGTH_SHORT);
		toast.show();
		//往sp存储whereCaluse，apnlistFragment再取出
		editor = preferences.edit();
		editor.putString("whereCalues", whereCalues);
		editor.commit();

		BaseActivity.this.setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		//        Toast toast = Toast.makeText(ApnListShowActivity.this, "onQueryTextChange!", Toast.LENGTH_SHORT);
//        toast.show();
		return false;
	}

	//与Fragment通信
	@Override
	public void initDBStartToFragment(int fragmentId) {
		Log.d(TAG, "------initDBStartToFragment------");
		mNavigationView.setCheckedItem(R.id.drawer_menu_home);
		setupTabs(BasePagerAdapter.HOME_FUNCTION);
	}
	@Override
	public void mulitQeruyStartToFragment(int fragmentId) {
		Log.d(TAG, "------mulitQeruyStartToFragment------");
		mNavigationView.setCheckedItem(R.id.drawer_menu_showapnlist);
		setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
	}

	@Override
	public void apnAddStartToFragment(int fragmentId) {
		Log.d(TAG, "------apnAddStartToFragment------");
		mNavigationView.setCheckedItem(R.id.drawer_menu_showapnlist);
		setupTabs(BasePagerAdapter.APNLISTSHOW_FUNCTION);
	}
}
