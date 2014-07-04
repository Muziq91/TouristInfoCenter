/**
 * @author Matei Mircea
 * 
 * 
 * This activity has the role of controlling the login page. It takes the username and password inserted by the user and
 * passes them to the UserLoginService class to be processed. It also offers the possibility to remember the 
 * username and password. This way the user does not have to input them all the time.
 * 
 * The class implements the UserLoginServiceFinishedListener interface and overrides its method. This way it passes "this"
 * to the AsyncTask and when that task finishes it will call the overriden method 
 */

package ro.mmp.tic.activities.authenticate;

import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.CentralActivity;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLoginServiceFinishedListener;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import ro.mmp.tic.service.userservice.UserLoginService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements
		UserLoginServiceFinishedListener {

	private EditText username;
	private EditText password;
	private CheckBox remember;

	private ProgressDialog loadDialog;

	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	private Boolean saveLoginInfo;
	private boolean canLogin = false;
	private DataBaseConnection dataBaseConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Show the Up button in the action bar.
		setupActionBar();

		setupInterface();

	}

	// Used to prepare the user interface elements
	private void setupInterface() {
		loadDialog = new ProgressDialog(this);

		dataBaseConnection = new DataBaseConnection(this);

		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		remember = (CheckBox) findViewById(R.id.remember);

		// we use the login preferences in order to remember the username and
		// password, if the suer desires so

		loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
		loginPrefsEditor = loginPreferences.edit();

		saveLoginInfo = loginPreferences.getBoolean("saveLoginInfo", false);

		if (saveLoginInfo == true) {
			username.setText(loginPreferences.getString("username", ""));
			password.setText(loginPreferences.getString("password", ""));
			remember.setChecked(true);
		}
	}

	/**
	 * Login into account over here
	 */

	public void onLoginButtonClick(View v) {

		validateInputData();
		if (canLogin) {

			// We try to get the user from the sqlite database
			HashMap<String, String> sqlUser = dataBaseConnection
					.getUserAfterUsername(username.getText().toString());

			// If the user is not inserted in the sqlite database we will check
			// the cloud database
			if (sqlUser.get("username") == null) {

				UserService userLoginService = new UserLoginService(
						createUser(), getApplicationContext(), this);
				userLoginService.execute("");
			}
			// If the user is in the sqlite databse, he has an account and we
			// let him go to the next activity
			else {
				CentralActivity.setCanUpdate(true);
				Intent intent = new Intent(getApplicationContext(),
						CentralActivity.class);
				intent.putExtra("loggedUser", username.getText().toString());

				startActivityForResult(intent, 0);
				loadDialog.dismiss();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		loadDialog.dismiss();
	}

	// When the user click on the remember username and password checkbox and
	// his information will be remembered
	public void onRememberCheckClick(View view) {

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(username.getWindowToken(), 0);

		if (remember.isChecked()) {
			loginPrefsEditor.putBoolean("saveLoginInfo", true);
			loginPrefsEditor.putString("username", username.getText()
					.toString());
			loginPrefsEditor.putString("password", password.getText()
					.toString());
			loginPrefsEditor.commit();
		} else {
			loginPrefsEditor.clear();
			loginPrefsEditor.commit();
		}
	}

	// used to create a basic user with only username and password
	private User createUser() {
		User user = new User();

		user.setUsername(username.getText().toString());
		user.setPassword(password.getText().toString());

		return user;
	}

	// This method is used to validate the information from the text fields
	private void validateInputData() {
		canLogin = false;

		loadDialog.setTitle("Loading Content...");
		loadDialog.setMessage("Please wait.");
		loadDialog.setCancelable(false);
		loadDialog.setIndeterminate(true);

		try {
			loadDialog.show();
		} catch (Exception e) {
			// WindowManager$BadTokenException will be caught and the app
			// would not display
			// the 'Force Close' message
		}
		if (username.getText().toString().matches("")) {
			loadDialog.dismiss();
			toastMessage(getString(R.string.usernameNotEmpty));

		} else if (password.getText().toString().matches("")) {
			loadDialog.dismiss();
			toastMessage(getString(R.string.passwordNotEmpty));

		} else {
			canLogin = true;
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
		getMenuInflater().inflate(R.menu.login, menu);
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
	public void onTaskFinished(boolean canLogin) {
		if (canLogin) {
			CentralActivity.setCanUpdate(true);
			Intent intent = new Intent(getApplicationContext(),
					CentralActivity.class);
			intent.putExtra("loggedUser", username.getText().toString());

			startActivityForResult(intent, 1);
			loadDialog.dismiss();
		} else {
			toastMessage(getString(R.string.incorrectCredentials));

			loadDialog.dismiss();

		}
	}

}
