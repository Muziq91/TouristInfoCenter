package ro.mmp.tic.service.userservice.strategy.usertopic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import ro.mmp.tic.service.userservice.strategy.Strategy;

public class OperationVerifUserTopic extends Strategy {
	// used by OperationVerifUserTopic
	public boolean execute(HashMap<String, String> location, String username,
			Connection connection) {
		PreparedStatement statement = null;
		ResultSet result = null;
		try {

			String sqlQuery = "Select * from usertopic where name = '"
					+ location.get("NAME") + "' OR lat = '"
					+ location.get("LAT") + "' AND lng='" + location.get("LNG")
					+ "'";
			statement = connection.prepareStatement(sqlQuery);
			result = statement.executeQuery();

			if (result.next() == false) {
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
