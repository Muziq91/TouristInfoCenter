package ro.mmp.tic.activities.defaultschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ro.mmp.tic.R;
import ro.mmp.tic.domain.UserPref;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

;

public class DisplayDefaultScheduleActivity extends Activity {

	private String username;
	private DataBaseConnection dbc;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_default_schedule);

		setupActionBar();

		Intent i = getIntent();
		username = i.getStringExtra("loggedUser");

		dbc = new DataBaseConnection(this);

		ArrayList<UserPref> allUserPref = dbc.getAllUserPreferences(username);
		List<String> allStringDefaultSchedule = dbc
				.getAllDefaultSchedule(allUserPref);

		listView = (ListView) findViewById(R.id.defaultScheduleListView);

		DefaultScheduleArrayAdapter adapter = new DefaultScheduleArrayAdapter(this,
				android.R.layout.simple_list_item_1, allStringDefaultSchedule);

		listView.setAdapter(adapter);

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
		getMenuInflater().inflate(R.menu.display_default_schedule, menu);
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

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

	}

	private class DefaultScheduleArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public DefaultScheduleArrayAdapter(Context context,
				int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
}
