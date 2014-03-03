/**
 * @author Matei Mircea
 * 
 * this class extends Strategy and has the role of updating the likes and unlikes of a user for a certain landmark
 */
package ro.mmp.tic.service.userservice.strategy.like;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.util.Log;

public class OperationSetLike extends Strategy {

	public void execute(String username, String topic, Like like,
			boolean exists, Connection connection) {

		Statement statement = null;

		try {

			Log.d("OperationSetLike", "2 am ajuns aici " + like.getLike() + " "
					+ like.getUnlike());

			String sqlQuery = "";
			if (exists) {
				Log.d("OperationSetLike",
						"2 update am ajuns aici " + like.getLike() + " "
								+ like.getUnlike());
				
				sqlQuery = "UPDATE `center`.`like` l SET l.likes="
						+ like.getLike()
						+ ",l.unlikes="
						+ like.getUnlike()
						+ " WHERE l.iduser = (Select u.iduser from `center`.`user` u where u.username = '"
						+ username + "')" + " AND l.idtopic='"
						+ like.getIdtopic() + "'";
			} else {

				Log.d("OperationSetLike",
						"2 insert am ajuns aici " + like.getLike() + " "
								+ like.getUnlike());
				sqlQuery = "Insert into `center`.`like` (iduser,idtopic,likes,unlikes) "
						+ "VALUES( (Select u.iduser from `center`.`user` u  where u.username='"
						+ username
						+ "'),"
						+ "(Select t.idtopic from `center`.`topic` t where t.name='"
						+ topic
						+ "'),"
						+ like.getLike()
						+ ","
						+ like.getUnlike() + ")";
			}

			statement = connection.createStatement();
			statement.executeUpdate(sqlQuery);

			Log.d("OperationSetLike",
					"3 am ajuns aici si am facut apelu in baza de date");
		} catch (Exception e) {

			try {
				statement.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} finally {

		}

	}
}
