package ro.mmp.tic.activities.defaultschedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.ScheduleActivity;
import ro.mmp.tic.activities.streetmap.util.ScheduleAlarm;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.domain.UserPref;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

;

public class DisplayDefaultScheduleActivity extends Activity {

	private String username;
	private DataBaseConnection dbc;
	private ListView listView;
	private List<String> allStringDefaultSchedule;
	static final int DATE_DIALOG_ID = 100;
	// date elements
	private int year;
	private int month;
	private int day;
	// time elements
	private int hour;
	private int minute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_default_schedule);

		setupActionBar();

		setupInterface();

	}

	private void setupInterface() {

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		Intent i = getIntent();
		username = i.getStringExtra("loggedUser");

		dbc = new DataBaseConnection(this);

		ArrayList<UserPref> allUserPref = dbc.getAllUserPreferences(username);
		allStringDefaultSchedule = dbc.getAllDefaultSchedule(allUserPref);

		listView = (ListView) findViewById(R.id.defaultScheduleListView);

		DefaultScheduleArrayAdapter adapter = new DefaultScheduleArrayAdapter(
				this, android.R.layout.simple_list_item_1,
				allStringDefaultSchedule);

		listView.setAdapter(adapter);

	}

	public void onScheduleButtonClick(View view) {
		Intent intent = new Intent(this, ScheduleActivity.class);
		startActivityForResult(intent, 0);
	}

	public void onRefreshScheduleButtonClick(View view) {
		ArrayList<UserPref> allUserPref = dbc.getAllUserPreferences(username);
		allStringDefaultSchedule = dbc.getAllDefaultSchedule(allUserPref);

		listView = (ListView) findViewById(R.id.defaultScheduleListView);

		DefaultScheduleArrayAdapter adapter = new DefaultScheduleArrayAdapter(
				this, android.R.layout.simple_list_item_1,
				allStringDefaultSchedule);

		listView.setAdapter(adapter);

	}

	public void onAcceptScheduleButtonClick(View view) {

		showDialog(DATE_DIALOG_ID);
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

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DATE_DIALOG_ID:

			// set date picker as current date

			return new DatePickerDialog(this, datePickerListener, year, month,
					day);

		}

		return null;

	}

	// the date picker listener
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called

		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			ArrayList<Schedule> lastSchedule = dbc.getLastSchedule();
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			int scheduleHour = 8;
			for (String s : allStringDefaultSchedule) {
				Schedule schedule = new Schedule();
				schedule.setTime(scheduleHour + ":00");
				hour = scheduleHour - 1;
				minute = 0;
				schedule.setDate(day + "/" + month + "/" + year);
				schedule.setPlace(s);

				if (lastSchedule.isEmpty()) {
					schedule.setAlarmnr(1);
				} else {
					schedule.setAlarmnr(lastSchedule.get(0).getAlarmnr() + 1);
				}

				setScheduledAllarm(getApplicationContext(), schedule);

				schedule.setDate(day + "/" + (month + 1) + "/" + year);
				toastMessage("date: " + schedule.getDate() + " time: "
						+ schedule.getTime() + " place " + schedule.getPlace());

				dbc.saveSchedule(schedule);
				scheduleHour = scheduleHour + 4;
			}

		}

	};

	public void setScheduledAllarm(Context context, Schedule schedule) {

		Calendar currentTime = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();

		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.clear();

		calendar.set(year, month, day, hour, minute, 00);
		Log.d("StreetmapUtil", "Set time by the user " + calendar.getTime()
				+ " current time: " + currentTime.getTime());

		if (calendar.compareTo(currentTime) < 0) {
			Log.d("StreetmapUtil", "Incorrect time");
		} else {
			Log.d("DisplayDefaultSchedule",
					"time in milis :"
							+ calendar.getTimeInMillis()
							+ " currentTime in Milis"
							+ currentTime.getTimeInMillis()
							+ " their difference: "
							+ (calendar.getTimeInMillis() - currentTime
									.getTimeInMillis()));

			ScheduleAlarm scheduleAlarm = new ScheduleAlarm(context);
			scheduleAlarm.setScheduleAllarm(calendar.getTimeInMillis(),
					schedule);
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
