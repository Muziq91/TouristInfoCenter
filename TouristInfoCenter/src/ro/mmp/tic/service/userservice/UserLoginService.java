/**
w * @author Matei Mircea
 * 
 * This class is an AsyncTask, it creates a connection to the database on the cloud 
 * and verifies if the user can or cannot login into his account
 * 
 */

package ro.mmp.tic.service.userservice;

import java.sql.SQLException;

import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLoginServiceFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.user.OperationLoginVerif;
import android.content.Context;
import android.util.Log;

public class UserLoginService extends UserService {

	private final UserLoginServiceFinishedListener finishedListener;

	public UserLoginService(User user, Context context,
			UserLoginServiceFinishedListener finishedListener) {
		this.user = user;
		this.context = context;
		this.finishedListener = finishedListener;
	}

	@Override
	protected String doInBackground(String... arg0) {

		try {

			connection = super.getConnection();

			Strategy loginVerif = new OperationLoginVerif();
			if (loginVerif.execute(user, connection)) {

				return "login";
			}

		} catch (Exception e) {
			Log.i("TAG", "ERROR " + e.toString());
		} finally {
			try {
				connection.close();

			} catch (SQLException e) {
				Log.i("TAG", "ERROR " + e.toString());
			}

		}

		return "nologin";
	}

	@Override
	protected void onPostExecute(String result) {

		boolean canLogin = false;
		if (result.equals("nologin")) {

			canLogin = false;

		} else {
			canLogin = true;

		}
		finishedListener.onTaskFinished(canLogin);

	}

}
