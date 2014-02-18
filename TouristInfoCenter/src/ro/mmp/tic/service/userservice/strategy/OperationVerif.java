/**
 * @author Matei Mircea
 * 
 * This class is a part of the strategy design pattern, it verifies if the user can register an account, or the
 *   provided information are already in user
 * 
 */

package ro.mmp.tic.service.userservice.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ro.mmp.tic.domain.User;

public class OperationVerif extends Strategy {

	@Override
	public boolean execute(User user, Connection connection) {
		PreparedStatement statement = null;
		ResultSet result = null;
		try {

			String sqlQuery = "Select * from user where username = '"
					+ user.getUsername() + "' OR email = '" + user.getEmail()
					+ "'";
			statement = connection.prepareStatement(sqlQuery);
			result = statement.executeQuery();

			if (result.next() == false) {
				return true;
			}

		} catch (Exception e) {

		} finally {

		}

		return false;
	}
}
