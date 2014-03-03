/**
 * @author Matei Mircea
 * 
 * This is an abstract class used by the service part of the program
 */

package ro.mmp.tic.service;

import java.sql.Connection;
import java.sql.DriverManager;

import ro.mmp.tic.domain.User;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class UserService extends AsyncTask<String, Void, String> {

	protected String TAG = "";
	protected User user;
	protected Connection connection;
	protected Context context;

	protected synchronized Connection getConnection() {

		if (connection == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager
						.getConnection(
								"jdbc:mysql://ec2-50-19-213-178.compute-1.amazonaws.com:3306/center",
								"Muziq91", "vasilecaine09");

			} catch (Exception e) {
				Log.i("TAG", "ERROR " + e.toString());
			}
		}

		return connection;
	}
}
