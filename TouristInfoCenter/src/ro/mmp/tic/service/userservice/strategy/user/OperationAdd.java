/**
 * @author Matei Mircea
 * 
 * This class is a part of the strategy design pattern, it adds the user to the database if the information provided are correct
 */

package ro.mmp.tic.service.userservice.strategy.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ro.mmp.tic.domain.User;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.util.Log;

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
			String insertLikeBotanical = "INSERT INTO `center`.`like` (iduser, idtopic,likes,unlikes) "
					+ "SELECT u.iduser,1,0,0 from `center`.`user` u where u.username='"
					+ user.getUsername() + "'";
			String insertLikeStatue = "INSERT INTO `center`.`like` (iduser, idtopic,likes,unlikes) "
					+ "SELECT u.iduser,2,0,0 from `center`.`user` u where u.username='"
					+ user.getUsername() + "'";
			String insertLikeCathedral = "INSERT INTO `center`.`like` (iduser, idtopic,likes,unlikes) "
					+ "SELECT u.iduser,3,0,0 from `center`.`user` u where u.username='"
					+ user.getUsername() + "'";
			String insertLikeBastion = "INSERT INTO `center`.`like` (iduser, idtopic,likes,unlikes) "
					+ "SELECT u.iduser,4,0,0 from `center`.`user` u where u.username='"
					+ user.getUsername() + "'";

			addStatement = connection.createStatement();
			addStatement.executeUpdate(insertTableSQL);
			addStatement.executeUpdate(insertLikeBotanical);
			addStatement.executeUpdate(insertLikeStatue);
			addStatement.executeUpdate(insertLikeCathedral);
			addStatement.executeUpdate(insertLikeBastion);

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
