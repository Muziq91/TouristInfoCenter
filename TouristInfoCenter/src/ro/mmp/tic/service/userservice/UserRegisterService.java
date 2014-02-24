/**
 * @author Matei Mircea
 * 
 * This class creates a connection to the cloud database and verifies if the user can create an account or not
 * 
 */

package ro.mmp.tic.service.userservice;

import java.sql.DriverManager;
import java.sql.SQLException;

import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.UserService;
import ro.mmp.tic.service.interfaces.UserRegisterServiceFinishedListener;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import ro.mmp.tic.service.userservice.strategy.user.OperationAdd;
import ro.mmp.tic.service.userservice.strategy.user.OperationVerif;
import android.content.Context;
import android.util.Log;

public class UserRegisterService extends UserService {

	private UserRegisterServiceFinishedListener finishedListener;

	public UserRegisterService(User user, Context context,
			UserRegisterServiceFinishedListener finishedListener) {
		this.user = user;
		this.context = context;
		this.finishedListener = finishedListener;
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

		return "noregister";
	}

	@Override
	protected void onPostExecute(String result) {

		boolean canRegister = false;
		if (result.equals("noregister")) {
			canRegister = false;

		} else {

			canRegister = true;

		}

		finishedListener.onTaskFinished(canRegister);
	}

}
