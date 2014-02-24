/**
 * @author Matei Mircea
 * 
 * This class is an AsyncTask, it creates a connection to the database on the cloud 
 * and verifies if the user can or cannot login into his account
 * 
 */

package ro.mmp.tic.service.userservice;

import java.sql.DriverManager;
import java.sql.SQLException;

import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserLoginServiceFinishedListener;
import ro.mmp.tic.service.userservice.strategy.OperationLoginVerif;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection(
							"jdbc:mysql://ec2-50-19-213-178.compute-1.amazonaws.com:3306/center",
							"Muziq91", "vasilecaine09");

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

		if (result.equals("nologin")) {

			Toast.makeText(context, "There is no user with the specified data",
					Toast.LENGTH_LONG).show();

		} else {

			Toast.makeText(context, "Login successfully", Toast.LENGTH_LONG)
					.show();
			finishedListener.onTaskFinished();
		}

	}

}
