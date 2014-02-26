/**
 * @author Matei Mircea
 * 
 * 
 * This class extends Strategy and has the rol of retrieveing the likes and unlikes of a scpecific user in order
 * to set the visibility of the buttons in the presentaion activity
 */

package ro.mmp.tic.service.userservice.strategy.like;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.service.userservice.strategy.Strategy;

public class OperationGetLike extends Strategy {

	public Like execute(String username, String topic, Connection connection) {
		PreparedStatement statement = null;
		ResultSet result = null;
		Like like = new Like();
		try {

			String sqlQuery = "SELECT l.idlike,l.iduser,l.idtopic,l.likes,l.unlikes FROM `center`.`like` l "
					+ "join `center`.`user` u ON u.iduser = l.iduser "
					+ "join `center`.`topic` t on l.idtopic = t.idtopic "
					+ "where u.username='"
					+ username
					+ "' "
					+ "AND t.name='"
					+ topic + "'";

			statement = connection.prepareStatement(sqlQuery);
			result = statement.executeQuery();

			if (result.next()) {

				like.setIdlike(result.getInt("idlike"));
				like.setIduser(result.getInt("iduser"));
				like.setIdtopic(result.getInt("idtopic"));
				like.setLike(result.getInt("likes"));
				like.setUnlike(result.getInt("unlikes"));
				return like;
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

		return like;
	}
}
