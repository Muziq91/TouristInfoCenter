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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class QuestionnaireActivity extends Activity {

	private String username;

	private Spinner favFoodSpinner;
	private String favFoodValue;
	private boolean favFoodSelected;

	private Spinner favActivitySpinner;
	private String favActivityValue;
	private boolean favActivitySelected;

	private RadioGroup radioHistory;
	private String likeHistoryValue;
	private RadioButton radioButton;
	private boolean isChecked;

	private DataBaseConnection dbc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionnaire);
		// Show the Up button in the action bar.
		setupActionBar();
		dbc = new DataBaseConnection(this);
		Intent i = getIntent();
		username = i.getStringExtra("loggedUser");

		setUpInterfaceElements();

	}

	private void setUpInterfaceElements() {
		favFoodSpinner = (Spinner) findViewById(R.id.favFoodSpinner);
		favActivitySpinner = (Spinner) findViewById(R.id.favActivitySpinner);
		radioHistory = (RadioGroup) findViewById(R.id.radioHistory);

		favFoodSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {

				favFoodValue = favFoodSpinner.getItemAtPosition(position)
						.toString();

				favFoodSelected = true;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				favFoodSelected = false;

			}
		});

		favActivitySpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {

						favActivityValue = favActivitySpinner
								.getItemAtPosition(position).toString();

						favActivitySelected = true;

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						favActivitySelected = false;

					}
				});

		radioHistory.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				radioButton = (RadioButton) radioHistory
						.findViewById(checkedId);
				isChecked = radioButton.isChecked();

				if (isChecked) {
					likeHistoryValue = (String) radioButton.getText();

				}
			}
		});

		ArrayList<UserPref> allUserPref = dbc.getAllUserPreferences(username);

		if (allUserPref.get(0).getIduserpref() != 0) {
			if (allUserPref.get(0).getFavFood().equals("Pizza")) {
				favFoodSpinner.setSelection(0);
			} else if (allUserPref.get(0).getFavFood().equals("Chinesse")) {
				favFoodSpinner.setSelection(1);
			} else if (allUserPref.get(0).getFavFood().equals("Indian")) {
				favFoodSpinner.setSelection(2);
			} else if (allUserPref.get(0).getFavFood().equals("Traditional")) {
				favFoodSpinner.setSelection(3);
			}

			if (allUserPref.get(0).getFavActivity().equals("Indoor")) {
				favActivitySpinner.setSelection(0);
			} else {
				favActivitySpinner.setSelection(1);

			}

			if (allUserPref.get(0).getLikeHistory().equals("Yes")) {
				radioButton = (RadioButton) radioHistory
						.findViewById(R.id.radioLikeHistory);
				radioButton.setChecked(true);
			} else {
				radioButton = (RadioButton) radioHistory
						.findViewById(R.id.radioUnlikeHistory);
				radioButton.setChecked(true);
			}

		}

	}

	public void onSubmitQuestionnaierButtonClick(View view) {
		
		if (favFoodSelected == false) {
			toastMessage(getString(R.string.favFoodNotSelected));
		} else if (favActivitySelected == false) {
			toastMessage(getString(R.string.favActivityNotSelected));
		} else if (isChecked == false) {
			toastMessage(getString(R.string.likeHistoryNotSelected));
		} else {

			toastMessage("You selected, favourite food: " + favFoodValue
					+ " favourite activity: " + favActivityValue
					+ " and history is:" + likeHistoryValue);
			UserPref up = new UserPref();
			up.setFavFood(favFoodValue);
			up.setFavActivity(favActivityValue);
			up.setLikeHistory(likeHistoryValue);

			dbc.saveUserPreferences(up, username);

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
		getMenuInflater().inflate(R.menu.questionnaire, menu);
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
