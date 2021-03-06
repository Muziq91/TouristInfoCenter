/**
 * 
 * @author Matei Mircea
 * This activity has the role of getting the user input in order to register an account. It passes the information to the 
 * UserRegisterService in order to process the information and deduce if the user can or not create an account.
 */

package ro.mmp.tic.activities.authenticate;

import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.CentralActivity;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserRegisterServiceFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.userservice.UserRegisterService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends Activity implements
		UserRegisterServiceFinishedListener {

	private EditText name;
	private EditText username;
	private EditText password;
	private EditText repassword;
	private EditText email;
	private String userCountry;
	// detects if the user has selected a country
	private boolean countrySelected = false;

	private ProgressDialog loadDialog;
	private Spinner countrySpinner;

	// If everything is in order the user will be able to register when
	// canregister is true
	private boolean canRegister = false;
	private DataBaseConnection dataBaseConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		loadDialog = new ProgressDialog(this);
		// Show the Up button in the action bar.
		setupActionBar();
		dataBaseConnection = new DataBaseConnection(this);
	}

	/**
	 * Register account over here, only when canRegister is true
	 */

	public void onRegisterAccountButtonClick(View v) {

		validateInputData();
		if (canRegister) {

			UserService userRegisterService = new UserRegisterService(
					getUserFromEdiTextFields(), getApplicationContext(), this);
			userRegisterService.execute("");

		}

	}

	// this method returns a user object with all the information inputed in the
	// edit text fields
	private User getUserFromEdiTextFields() {
		User user = new User();
		user.setName(name.getText().toString());
		user.setUsername(username.getText().toString());
		user.setPassword(password.getText().toString());
		user.setEmail(email.getText().toString());
		user.setCountry(userCountry);

		return user;
	}

	// This method is used to validate the user input such that the user does
	// not give empty fields, or forget some information
	private void validateInputData() {

		canRegister = false;
		name = (EditText) findViewById(R.id.name);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		repassword = (EditText) findViewById(R.id.repassword);
		email = (EditText) findViewById(R.id.email);
		countrySpinner = (Spinner) findViewById(R.id.countrySpinner);

		countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {

				userCountry = countrySpinner.getItemAtPosition(position)
						.toString();
				countrySelected = true;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				countrySelected = false;

			}
		});

		if (name.getText().toString().matches("")) {
			toastMessage(getString(R.string.nameNotEmpty));
		} else if (username.getText().toString().matches("")) {
			toastMessage(getString(R.string.usernameNotEmpty));
		} else if (password.getText().toString().matches("")) {
			toastMessage(getString(R.string.passwordNotEmpty));
		} else if (repassword.getText().toString().matches("")) {
			toastMessage(getString(R.string.retypepasswordNotEmpty));
		} else if (email.getText().toString().matches("")) {
			toastMessage(getString(R.string.emailNotEmpty));
		} else if (!password.getText().toString()
				.equals(repassword.getText().toString())) {
			toastMessage(getString(R.string.passwordsMatch));
		} else if (countrySelected == false) {
			toastMessage(getString(R.string.countryNotEmpty));

		} else {

			loadDialog.setTitle("Loading Content...");
			loadDialog.setMessage("Please wait.");
			loadDialog.setCancelable(false);
			loadDialog.setIndeterminate(true);

			try {
				loadDialog.show();
			} catch (Exception e) {

			}

			canRegister = true;
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
		getMenuInflater().inflate(R.menu.register, menu);
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

	@Override
	public void onTaskFinished(boolean canRegister) {

		if (canRegister) {

			// Inserts in the sqlite database when the AsynkTask finishes and if
			// everything is in order
			User user = getUserFromEdiTextFields();
			HashMap<String, String> newUser = new HashMap<String, String>(0);
			newUser.put("name", user.getName());
			newUser.put("username", user.getUsername());
			newUser.put("password", user.getPassword());
			newUser.put("email", user.getEmail());
			newUser.put("country", user.getCountry());
			dataBaseConnection.insertUserAtRegister(newUser);
			loadDialog.dismiss();
			CentralActivity.setCanUpdate(true);
			Intent intent = new Intent(getApplicationContext(),
					CentralActivity.class);
			intent.putExtra("loggedUser", username.getText().toString());
			startActivityForResult(intent, 0);
		} else {
			loadDialog.dismiss();
			toastMessage(getString(R.string.incorrectRegister));

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		loadDialog.dismiss();

	}

}
