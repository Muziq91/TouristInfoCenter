package ro.mmp.tic.activities.defaultschedule;

import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.domain.UserPref;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class DefaultScheduleActivity extends Activity {

	private static String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_default_schedule);
		// Show the Up button in the action bar.
		setupActionBar();

		if (username == null) {
			Intent i = getIntent();
			username = i.getStringExtra("loggedUser");
		}

	}

	public void onGoToQuestionnaierButtonClick(View view) {

		Intent intent = new Intent(DefaultScheduleActivity.this,
				QuestionnaireActivity.class);
		intent.putExtra("loggedUser", username);
		startActivityForResult(intent, 1);

	}

	public void onDisplayDefaultScheduleButtonClick(View v) {
		DataBaseConnection dbc = new DataBaseConnection(this);
		ArrayList<UserPref> allUserPref = dbc.getAllUserPreferences(username);

		if (allUserPref.get(0).getIduserpref() == 0) {
			toastMessage("You must first answer the questionnaire");
		} else {

			Intent intent = new Intent(DefaultScheduleActivity.this,
					DisplayDefaultScheduleActivity.class);
			intent.putExtra("loggedUser", username);
			startActivityForResult(intent, 2);
		}

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
		getMenuInflater().inflate(R.menu.default_schedule, menu);
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

}
