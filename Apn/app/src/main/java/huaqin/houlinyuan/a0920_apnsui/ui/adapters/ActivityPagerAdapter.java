package huaqin.houlinyuan.a0920_apnsui.ui.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by ubuntu on 16-9-21.
 */
public class ActivityPagerAdapter extends PagerAdapter {
    private Context context;
    private FragmentManager fm;
    private View view;
    public ActivityPagerAdapter(View view,Context context, FragmentManager fm) {
        this.fm = fm;
        this.context = context;
        this.view = view;
    }
    @Override
    public int getCount() {
        return 1;
    }

    @Override

    public void destroyItem(ViewGroup container, int position,

                            Object object) {

        // TODO Auto-generated method stub

        container.removeView(view);

    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(view);
        return view;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
