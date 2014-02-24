/**
 * 
 * @author Matei Mircea
 * This activity has the role of getting the user input in order to register an account. It passes the information to the 
 * UserRegisterService in order to process the information and deduce if the user can or not create an account.
 */

package ro.mmp.tic.activities.authenticate;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.CentralActivity;
import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserRegisterServiceFinishedListener;
import ro.mmp.tic.service.userservice.UserRegisterService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements
		UserRegisterServiceFinishedListener {

	private EditText name;
	private EditText username;
	private EditText password;
	private EditText repassword;
	private EditText email;
	private EditText country;
	private Button registerButton;
	private boolean canRegister = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Register account over here
	 */

	public void registerAccount(View v) {

		intializeEditTextFields();
		if (canRegister) {

			registerButton.setVisibility(View.GONE);
			UserService userRegisterService = new UserRegisterService(
					getUserFromEdiTextFields(), getApplicationContext(), this);
			userRegisterService.execute("");

		}

	}

	private User getUserFromEdiTextFields() {
		User user = new User();
		user.setName(name.getText().toString());
		user.setUsername(username.getText().toString());
		user.setPassword(password.getText().toString());
		user.setEmail(email.getText().toString());
		user.setCountry(country.getText().toString());

		return user;
	}

	private void intializeEditTextFields() {
		canRegister = false;
		name = (EditText) findViewById(R.id.name);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		repassword = (EditText) findViewById(R.id.repassword);
		email = (EditText) findViewById(R.id.email);
		country = (EditText) findViewById(R.id.country);
		registerButton = (Button) findViewById(R.id.register);

		if (name.getText().toString().matches("")) {
			toastMessage("Name can not be empty");
		} else if (username.getText().toString().matches("")) {
			toastMessage("UserName can not be empty");
		} else if (password.getText().toString().matches("")) {
			toastMessage("Password can not be empty");
		} else if (repassword.getText().toString().matches("")) {
			toastMessage("Please re type the password");
		} else if (email.getText().toString().matches("")) {
			toastMessage("Email can not be empty");
		} else if (country.getText().toString().matches("")) {
			toastMessage("Country can not be empty");
		} else if (!password.getText().toString()
				.equals(repassword.getText().toString())) {
			toastMessage("Passwords must match");
		} else {

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
			Intent intent = new Intent(getApplicationContext(),
					CentralActivity.class);
			intent.putExtra("loggedUser", username.getText().toString());
			startActivity(intent);
		} else {
			toastMessage("Username or email are already in use");
			registerButton.setVisibility(View.VISIBLE);
		}
	}

}
