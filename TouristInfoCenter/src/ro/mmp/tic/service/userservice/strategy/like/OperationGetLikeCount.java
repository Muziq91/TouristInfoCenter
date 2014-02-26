/**
 * @author Matei Mircea
 * 
 * This class extends strategy and has the role of retrieving the count of likes and unlikes of a certaninlandmark
 */
package ro.mmp.tic.service.userservice.strategy.like;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ro.mmp.tic.service.userservice.strategy.Strategy;

public class OperationGetLikeCount extends Strategy {

	private int likeCount;
	private int unlikeCount;

	public int execute(String topicName, Connection connection, String type) {

		PreparedStatement statement = null;
		ResultSet result = null;

		try {

			if (type.equals("like")) {

				String sqlQuery = " SELECT COUNT(l.likes) FROM `center`.`like` l "
						+ "join `center`.`topic` t on l.idtopic=t.idtopic "
						+ "where t.name='"
						+ topicName
						+ "' AND l.likes=1 AND l.unlikes=0;";

				statement = connection.prepareStatement(sqlQuery);
				result = statement.executeQuery();

				if (result.next()) {

					likeCount = result.getInt("COUNT(l.likes)");

				}
				return likeCount;
			}

			else {

				String sqlQuery = " SELECT COUNT(l.likes) FROM `center`.`like` l "
						+ "join `center`.`topic` t on l.idtopic=t.idtopic "
						+ "where t.name='"
						+ topicName
						+ "' AND l.likes=0 AND l.unlikes=1;";

				statement = connection.prepareStatement(sqlQuery);
				result = statement.executeQuery();

				if (result.next()) {

					unlikeCount = result.getInt("COUNT(l.likes)");

				}

				return unlikeCount;
			}

		} catch (Exception e) {

		} finally {

			try {
				result.close();
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return 0;
	}

}
