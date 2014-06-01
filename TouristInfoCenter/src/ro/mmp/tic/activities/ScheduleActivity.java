/**
 * @author Matei Mircea
 * 
 * this is the schedule activity which allows the user to see his schedule items, update or
 * delete them
 */

package ro.mmp.tic.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.util.ScheduleAlarm;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleActivity extends Activity {

	private ListView listView;
	private ArrayList<Schedule> allSchedule;
	private TextView placeUpdateText;
	private TextView timeUpdateText;
	private TextView dateUpdateText;
	private int clickPosition;
	private AlertDialog scheduleAlertDialog;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	private ScheduleAlarm scheduleAlarm;
	private Schedule oldSchedule;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		setupInterface();

	}

	private void setupInterface() {

		allSchedule = new ArrayList<Schedule>(0);
		ArrayList<String> allStringSchedule = new ArrayList<String>(0);
		allSchedule.clear();
		allSchedule = getAllSchedule();

		for (Schedule s : allSchedule) {
			String text = s.getPlace() + "\n" + s.getDate() + "\n"
					+ s.getTime();
			allStringSchedule.add(text);
		}

		listView = (ListView) findViewById(R.id.scheduleListView);
		final ScheduleArrayAdapter adapter = new ScheduleArrayAdapter(this,
				android.R.layout.simple_list_item_1, allStringSchedule);

		listView.setAdapter(adapter);

		// when the user click on one schedule item a dialog comes up
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				oldSchedule = new Schedule();
				oldSchedule.setIdschedule(allSchedule.get(position)
						.getIdschedule());
				oldSchedule.setDate(allSchedule.get(position).getDate());
				oldSchedule.setTime(allSchedule.get(position).getTime());
				oldSchedule.setPlace(allSchedule.get(position).getPlace());
				oldSchedule.setAlarmnr(allSchedule.get(position).getAlarmnr());

				createDialogView(position);

			}

		});

		scheduleAlarm = new ScheduleAlarm(getApplicationContext());

	}

	/**
	 * Creates the dialog box that the user sees when clicking on a schedule
	 * item
	 * 
	 * @param position
	 */

	private void createDialogView(int position) {
		this.clickPosition = position;
		// We create the view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialogView = layoutInflater.inflate(
				R.layout.schedule_props_prompt_layout, null);
		// set up the dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(dialogView);
		alertDialogBuilder.setTitle("Update Schedule");
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		// setUpAlert(geometry.getName());
		// say what the buttons do
		alertDialogBuilder.setCancelable(false)

		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});

		initiateViewElements(dialogView);

		// create the alert dialog
		scheduleAlertDialog = alertDialogBuilder.create();
		// display the dialog
		scheduleAlertDialog.show();

	}

	/**
	 * Initializes the view elements
	 * 
	 * @param dialogView
	 */
	private void initiateViewElements(View dialogView) {

		dateUpdateText = (TextView) dialogView
				.findViewById(R.id.dateUpdateText);
		timeUpdateText = (TextView) dialogView
				.findViewById(R.id.timeUpdateText);
		placeUpdateText = (TextView) dialogView
				.findViewById(R.id.placeUpdateText);

		dateUpdateText.setText(allSchedule.get(clickPosition).getDate());
		timeUpdateText.setText(allSchedule.get(clickPosition).getTime());
		placeUpdateText.setText(allSchedule.get(clickPosition).getPlace());

	}

	public void onUpdateButtonClick(View view) {

		Log.d("ScheduleActivity", "Old alarm " + oldSchedule.getPlace()
				+ " old nr" + oldSchedule.getAlarmnr());
		// cancel old schedule alarm
		scheduleAlarm.cancelScheduleAllarm(oldSchedule);

		DataBaseConnection dbc = new DataBaseConnection(this);
		Schedule updateSchedule = new Schedule();
		ArrayList<Schedule> lastSchedule = dbc.getLastSchedule();

		// create new updated schedule
		updateSchedule.setIdschedule(allSchedule.get(clickPosition)
				.getIdschedule());
		updateSchedule.setDate(dateUpdateText.getText().toString());
		updateSchedule.setTime(timeUpdateText.getText().toString());
		updateSchedule.setPlace(placeUpdateText.getText().toString());
		updateSchedule.setAlarmnr(allSchedule.get(clickPosition).getAlarmnr());

		// assign it a new alarmnr
		if (lastSchedule.isEmpty()) {
			updateSchedule.setAlarmnr(1);
		} else {
			updateSchedule.setAlarmnr(lastSchedule.get(0).getAlarmnr() + 1);
		}

		Log.d("ScheduleActivity", "New alarm " + updateSchedule.getPlace()
				+ " new nr" + updateSchedule.getAlarmnr());

		// update the schedule
		dbc.updateSchedule(updateSchedule);

		// get the new date and time
		String newDate[] = updateSchedule.getDate().split("/");
		String newTime[] = updateSchedule.getTime().split(":");

		// set day/month/year hour:mionute elements in order to set a new alarm
		day = Integer.parseInt(newDate[0]);
		month = Integer.parseInt(newDate[1]);
		month = month - 1;
		year = Integer.parseInt(newDate[2]);

		hour = Integer.parseInt(newTime[0]);
		minute = Integer.parseInt(newTime[1]);

		// set the new alarm
		setScheduledAllarm(updateSchedule);

		// gets all schedules including the one updated
		ArrayList<String> allStringSchedule = new ArrayList<String>(0);
		allSchedule = new ArrayList<Schedule>(0);
		allSchedule = getAllSchedule();

		// transforms it into String
		for (Schedule s : allSchedule) {
			String text = s.getPlace() + "\n" + s.getDate() + "\n"
					+ s.getTime();
			allStringSchedule.add(text);
		}

		// set the list view with the new information
		listView = (ListView) findViewById(R.id.scheduleListView);
		final ScheduleArrayAdapter adapter = new ScheduleArrayAdapter(this,
				android.R.layout.simple_list_item_1, allStringSchedule);

		listView.setAdapter(adapter);

		// closes the dialog
		scheduleAlertDialog.cancel();

	}

	/**
	 * determines what happens when the user deletes a schedule item
	 * 
	 * @param view
	 */
	public void onDeleteButtonClick(View view) {

		// cancel old alarm schedule
		scheduleAlarm.cancelScheduleAllarm(oldSchedule);

		DataBaseConnection dbc = new DataBaseConnection(this);
		dbc.deleteSchedule(allSchedule.get(clickPosition));

		ArrayList<String> allStringSchedule = new ArrayList<String>(0);
		allSchedule = new ArrayList<Schedule>(0);
		allSchedule = getAllSchedule();

		for (Schedule s : allSchedule) {
			String text = s.getPlace() + "\n" + s.getDate() + "\n"
					+ s.getTime();
			allStringSchedule.add(text);
		}

		listView = (ListView) findViewById(R.id.scheduleListView);
		final ScheduleArrayAdapter adapter = new ScheduleArrayAdapter(this,
				android.R.layout.simple_list_item_1, allStringSchedule);

		listView.setAdapter(adapter);

		scheduleAlertDialog.cancel();

	}

	private ArrayList<Schedule> getAllSchedule() {
		DataBaseConnection dbc = new DataBaseConnection(this);

		return dbc.getAllSchedule();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
		return true;
	}

	private void setScheduledAllarm(Schedule schedule) {

		Calendar currentTime = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();

		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.clear();

		calendar.set(year, month, day, hour - 1, minute, 00);

		if (calendar.compareTo(currentTime) < 0) {
			toastMessage("That time is no longer valid, please select another time");
		} else {

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

		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();

	}

	private class ScheduleArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public ScheduleArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
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
