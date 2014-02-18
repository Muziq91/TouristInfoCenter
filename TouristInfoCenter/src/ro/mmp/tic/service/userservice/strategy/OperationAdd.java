/**
 * @author Matei Mircea
 * 
 * This class is a part of the strategy design pattern, it adds the user to the database if the information provided are correct
 */

package ro.mmp.tic.service.userservice.strategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import android.util.Log;

import ro.mmp.tic.domain.User;

public class OperationAdd extends Strategy {

	private Statement addStatement = null;
	private final String TAG = "UserRegisterService";

	public boolean execute(User user, Connection connection) {

		try {

			String insertTableSQL = "INSERT INTO user"
					+ "(name,username,password,email,country) " + "VALUES"
					+ "('" + user.getName() + "','" + user.getUsername()
					+ "','" + user.getPassword() + "','" + user.getEmail()
					+ "','" + user.getCountry() + "')";

			addStatement = connection.createStatement();
			addStatement.executeUpdate(insertTableSQL);

			return true;

		} catch (Exception exc) {

			Log.d(TAG, exc.toString());

		} finally {
			try {

				addStatement.close();
			} catch (SQLException e) {
			}

		}

		return false;

	}

}
