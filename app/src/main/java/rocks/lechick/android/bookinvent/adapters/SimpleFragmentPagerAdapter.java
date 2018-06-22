package rocks.lechick.android.bookinvent.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import rocks.lechick.android.bookinvent.R;
import rocks.lechick.android.bookinvent.fragments.BusinessFragment;
import rocks.lechick.android.bookinvent.fragments.FictionFragment;
import rocks.lechick.android.bookinvent.fragments.OtherFragment;
import rocks.lechick.android.bookinvent.fragments.PopScienceFragment;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private String tabTitles[];




    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        tabTitles = new String[] {
                mContext.getResources().getString(R.string.fiction),
                mContext.getResources().getString(R.string.business),
                mContext.getResources().getString(R.string.pop_sci),
                mContext.getResources().getString(R.string.other)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FictionFragment();
        } else if (position == 1) {
            return new BusinessFragment();
        } else if (position == 2) {
            return new PopScienceFragment();
        } else {
            return new OtherFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
