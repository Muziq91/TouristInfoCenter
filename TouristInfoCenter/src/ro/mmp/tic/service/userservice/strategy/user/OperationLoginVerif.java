/**
 * @author Matei Mircea
 * 
 * 
 * This class is a part of the strategy design pattern, it verifies if the user can login. 
 */

package ro.mmp.tic.service.userservice.strategy.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.userservice.strategy.Strategy;

public class OperationLoginVerif extends Strategy {

	@Override
	public boolean execute(User user, Connection connection) {

		PreparedStatement statement = null;
		ResultSet result = null;
		try {

			String sqlQuery = "Select * from user where username = '"
					+ user.getUsername() + "' AND password = '"
					+ user.getPassword() + "'";

			statement = connection.prepareStatement(sqlQuery);
			result = statement.executeQuery();

			if (result.next()) {
				return true;
			}

		} catch (Exception e) {

		} finally {

			try {
				statement.close();
				result.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return false;

	}

}
