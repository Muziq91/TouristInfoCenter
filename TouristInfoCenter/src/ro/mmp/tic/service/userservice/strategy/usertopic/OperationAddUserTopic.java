package ro.mmp.tic.service.userservice.strategy.usertopic;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

public class OperationAddUserTopic extends Strategy {

	private Statement addStatement = null;

	public void execute(HashMap<String, String> location, Bitmap image,
			String username, Connection connection) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			String imageEncoded = Base64.encodeToString(byteArray,
					Base64.DEFAULT);

			Log.d("Adaugam", "Imagine convertita " + imageEncoded);

			String sqlQuery = "Insert into usertopic (iduser,name,description,image,color,lat,lng) "
					+ "VALUES( (Select u.iduser from user u  where u.username='"
					+ username
					+ "'),"
					+ "'"
					+ location.get("NAME")
					+ "','"
					+ location.get("DESCRIPTION")
					+ "','"
					+ imageEncoded
					+ "','"
					+ location.get("COLOR")
					+ "','"
					+ location.get("LAT") + "','" + location.get("LNG") + "')";

			addStatement = connection.createStatement();
			addStatement.executeUpdate(sqlQuery);

		} catch (Exception exc) {

			Log.d("OperationAddUserTopic", exc.toString());

		} finally {
			try {

				addStatement.close();
			} catch (SQLException e) {
			}

		}

	}

}
