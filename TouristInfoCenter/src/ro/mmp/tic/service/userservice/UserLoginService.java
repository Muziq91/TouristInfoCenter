package ro.mmp.tic.service.userservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.userservice.strategy.OperationAdd;
import ro.mmp.tic.service.userservice.strategy.OperationLoginVerif;
import ro.mmp.tic.service.userservice.strategy.OperationVerif;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class UserLoginService extends AsyncTask<String, Void, String> {

	private final String TAG = "UserRegisterService";
	private User user;
	private Connection connection;
	private Context context;

	public UserLoginService(User user, Context context) {
		this.user = user;
		this.context = context;
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

		return null;
	}

	@Override
	protected void onPostExecute(String result) {

		if (result == null) {

			Toast.makeText(context, "There is no user with the specified data",
					Toast.LENGTH_LONG).show();

		} else {

			Toast.makeText(context, "Login successfully", Toast.LENGTH_LONG)
					.show();

		}
	}

}
