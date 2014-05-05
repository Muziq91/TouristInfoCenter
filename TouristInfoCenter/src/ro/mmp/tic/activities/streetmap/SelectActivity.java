package ro.mmp.tic.activities.streetmap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.fragment.CategoryFragment;
import ro.mmp.tic.activities.streetmap.fragment.LocationFragment;
import ro.mmp.tic.activities.streetmap.fragment.TypeFragment;
import ro.mmp.tic.activities.streetmap.listeners.LocationSelectListener;
import ro.mmp.tic.activities.streetmap.listeners.TypeSelectListener;
import ro.mmp.tic.adapter.TabAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SelectActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabAdapter tabAdapter;
	private ActionBar actionBar;
	private TypeSelectListener typeSelectListener;
	private LocationSelectListener locationSelectListener;

	// Tab titles
	private String[] tabs = { "Category", "Type", "Location" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		// Show the Up button in the action bar.
		setupActionBar();

		setupTabs();

	}

	//This method sets the tabs and listeners for the fragmentactivity
	private void setupTabs() {
		
		typeSelectListener = new TypeFragment();
		locationSelectListener = new LocationFragment();
		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		tabAdapter = new TabAdapter(getSupportFragmentManager());

		viewPager.setAdapter(tabAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs, and setting the listener for the tabs
		for (String tabName : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tabName)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());

		if (tab.getPosition() == 1
				&& CategoryFragment.getSelectedCategory().isEmpty() == false) {

			typeSelectListener.taskFinished();
		}

		if (tab.getPosition() == 2
				&& TypeFragment.getSeletedType().isEmpty() == false) {
			locationSelectListener.taskFinished();
		}

	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	public void goToMap(View v) {
		if (LocationFragment.getSelectedLocation().isEmpty()) {
			toastMessage("You need to first select a location");
		} else {
			Intent i = getIntent();
			String username = i.getStringExtra("loggedUser");
			Intent intent = new Intent(getApplicationContext(),
					StreetMapActivity.class);
			intent.putExtra("loggedUser", username);

			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);

		this.deleteFile("mapImage.png");
		this.deleteFile("mapImageEnlarge.png");
	}
}
