/**
 * @author Matei Mircea
 * 
 * 
 * This activity has the role of controlling the login page. It takes the username and password inserted by the user and
 * passes them to the UserLoginService class to be processed. It also offers the possibility to remember the 
 * username and password. This way the user does not have to input them all the time.
 */

package ro.mmp.tic.activities.authenticate;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.CentralActivity;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLoginServiceFinishedListener;
import ro.mmp.tic.service.userservice.UserLoginService;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements
		UserLoginServiceFinishedListener {

	private EditText username;
	private EditText password;
	private CheckBox remember;
	private Button loginButton;
	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	private Boolean saveLogin;
	private boolean canLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Show the Up button in the action bar.
		setupActionBar();

		loginButton = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		remember = (CheckBox) findViewById(R.id.remember);

		loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
		loginPrefsEditor = loginPreferences.edit();

		saveLogin = loginPreferences.getBoolean("saveLogin", false);

		if (saveLogin == true) {
			username.setText(loginPreferences.getString("username", ""));
			password.setText(loginPreferences.getString("password", ""));
			remember.setChecked(true);
		}
	}

	/**
	 * Login into account over here
	 */

	public void login(View v) {

		InitializeEditTextFields();
		if (canLogin) {

			UserService userLoginService = new UserLoginService(createUser(),
					getApplicationContext(), this);
			userLoginService.execute("");

			

		}

	}

	public void remember(View view) {

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(username.getWindowToken(), 0);

		if (remember.isChecked()) {
			loginPrefsEditor.putBoolean("saveLogin", true);
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

	private User createUser() {
		User user = new User();

		user.setUsername(username.getText().toString());
		user.setPassword(password.getText().toString());

		return user;
	}

	private void InitializeEditTextFields() {
		canLogin = false;

		if (username.getText().toString().matches("")) {
			toastMessage("Username can not be empty");
		} else if (username.getText().toString().matches("")) {
			toastMessage("Password can not be empty");
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
	public void onTaskFinished() {
		Intent intent = new Intent(getApplicationContext(),
				CentralActivity.class);
		intent.putExtra("loggedUser", username.getText().toString());

		startActivity(intent);

	}

}
