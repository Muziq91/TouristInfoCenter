package ro.mmp.tic.service.userservice.strategy.like;

import java.sql.Connection;
import java.sql.Statement;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.util.Log;

public class OperationSetLike extends Strategy {

	public void execute(String username, String topic, Like like,
			Connection connection) {

		Statement statement = null;

		try {

			Log.d("OperationSetLike", "2 am ajuns aici");

			/*
			 * String sqlQuery = "UPDATE `center`.`like` l SET l.likes='" +
			 * like.getLike() + "',l.unlikes='" + like.getUnlike() +
			 * "' WHERE l.iduser = (SELECT u.iduser from `center`.`user` u where u.username='"
			 * + username +
			 * "') AND l.idtopic=(SELECT t.idtopic from `center`.`topic` t where t.name='"
			 * + topic + "')";
			 */

			String sqlQuery = "UPDATE `center`.`like` l SET l.likes='"
					+ like.getLike() + "',l.unlikes='" + like.getUnlike()
					+ "' WHERE l.iduser ='" + like.getIduser()
					+ "' AND l.idtopic='" + like.getIdtopic() + "'";

			statement = connection.createStatement();
			statement.executeUpdate(sqlQuery);

			Log.d("OperationSetLike",
					"3 am ajuns aici si am facut apelu in baza de date");
		} catch (Exception e) {

		} finally {

		}

	}
}
