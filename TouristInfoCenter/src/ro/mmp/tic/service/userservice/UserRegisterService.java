package ro.mmp.tic.service.userservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.userservice.strategy.OperationAdd;
import ro.mmp.tic.service.userservice.strategy.OperationVerif;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class UserRegisterService extends AsyncTask<String, Void, String> {

	private final String TAG = "UserRegisterService";
	private User user;
	private Connection connection;
	private Context context;

	public UserRegisterService(User user, Context context) {
		this.user = user;
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection(
							"jdbc:mysql://ec2-50-19-213-178.compute-1.amazonaws.com:3306/center",
							"Muziq91", "vasilecaine09");

			Strategy verif = new OperationVerif();
			if (verif.execute(user, connection)) {
				Strategy add = new OperationAdd();
				add.execute(user, connection);
				return "registered";
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
		Log.i("TAG", "INTRU AICI  " + result);
		if (result == null) {
			Toast.makeText(context, "UserName or Email are already in user",
					Toast.LENGTH_LONG).show();

		} else {

			Toast.makeText(context, "You have been successfully registered",
					Toast.LENGTH_LONG).show();

		}
	}

}
