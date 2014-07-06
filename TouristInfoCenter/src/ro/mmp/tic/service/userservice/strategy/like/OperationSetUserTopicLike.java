/**
 * @author Matei Mircea
 * 
 * this class extends Strategy and has the role of updating the likes and 
 * unlikes of a user for a certain landmark
 */
package ro.mmp.tic.service.userservice.strategy.like;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.util.Log;

public class OperationSetUserTopicLike extends Strategy {

	public void execute(String username, String topic, Like like,
			boolean exists, Connection connection) {

		Statement statement = null;

		try {

			Log.d("OperationSetUserTopicLike",
					"2 am ajuns aici " + like.getLike() + " "
							+ like.getUnlike());

			String sqlQuery = "";
			// if the like column already exists we only udpate it
			if (exists) {

				sqlQuery = "UPDATE `center`.`like` l SET l.likes="
						+ like.getLike()
						+ ",l.unlikes="
						+ like.getUnlike()
						+ " WHERE l.iduser = (Select u.iduser from `center`.`user` u where u.username = '"
						+ username
						+ "')"
						+ " AND l.idusertopic=(Select ut.idusertopic from `center`.`usertopic` ut where ut.name='"
						+ topic + "')";
				Log.d("OperationSetUserTopicLike", sqlQuery);
			}
			// if the like comment does not exist we insert it
			else {

				sqlQuery = "Insert into `center`.`like` (iduser,idtopic,idusertopic,likes,unlikes) "
						+ "VALUES( (Select u.iduser from `center`.`user` u  where u.username='"
						+ username
						+ "'), 1 , "
						+ "(Select t.idusertopic from `center`.`usertopic` t where t.name='"
						+ topic
						+ "'), "
						+ like.getLike()
						+ ","
						+ like.getUnlike() + ")";
				Log.d("OperationSetUserTopicLike", sqlQuery);
			}

			statement = connection.createStatement();
			statement.executeUpdate(sqlQuery);

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
