package ro.mmp.tic.adapter;

import ro.mmp.tic.activities.streetmap.fragment.CategoryFragment;
import ro.mmp.tic.activities.streetmap.fragment.LocationFragment;
import ro.mmp.tic.activities.streetmap.fragment.TypeFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

	public TabAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			return new CategoryFragment();

		case 1:
			return new TypeFragment();

		case 2:
			return new LocationFragment();

		}

		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
