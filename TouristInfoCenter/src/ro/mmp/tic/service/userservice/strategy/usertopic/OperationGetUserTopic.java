package ro.mmp.tic.service.userservice.strategy.usertopic;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.activities.streetmap.util.ImageUtil;
import ro.mmp.tic.domain.UserTopic;
import ro.mmp.tic.service.userservice.strategy.Strategy;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class OperationGetUserTopic extends Strategy {

	public ArrayList<HashMap<String, String>> execute(Connection connection,
			Context context) {

		ArrayList<HashMap<String, String>> userTopicList = new ArrayList<HashMap<String, String>>(
				0);
		PreparedStatement statement = null;
		ResultSet result = null;
		try {

			String sqlQuery = "Select ut.idusertopic,ut.iduser,ut.name,ut.description,ut.image,ut.color,ut.lat,ut.lng from `center`.`usertopic` ut ";

			statement = connection.prepareStatement(sqlQuery);

			result = statement.executeQuery();

			while (result.next()) {

				UserTopic userTopic = new UserTopic();

				userTopic.setIdusertopic(result.getInt("idusertopic"));
				userTopic.setIduser(result.getInt("iduser"));
				userTopic.setName(result.getString("name"));
				userTopic.setDescription(result.getString("description"));
				userTopic.setColor(result.getString("color"));
				userTopic.setLat(result.getDouble("lat"));
				userTopic.setLng(result.getDouble("lng"));
				userTopic.setImage(userTopic.getName().toLowerCase()
						+ "download.png");

				Log.d("Crap aici", "1");
				ImageUtil imageUtil = new ImageUtil(context);
				Log.d("Crap aici", "2");
				byte[] byteArr = result.getBytes("image");
				Log.d("Crap aici", "3 " + byteArr.length);
				byte[] decodedByte = Base64.decode(byteArr, 0);
				Log.d("Crap aici", "4 " + decodedByte.length);
				Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0,
						decodedByte.length);
				Log.d("Crap aici", "5");

				if (bitmap == null) {
					Log.d("IMAGINEA E NULL", "IAMGINEA E NULL");
				} else {
					imageUtil.saveImageToInternalStorage(bitmap, userTopic
							.getName().toLowerCase() + "download");
				}

				HashMap<String, String> map = new HashMap<String, String>(0);

				map.put("IDUSERTOPIC",
						String.valueOf(userTopic.getIdusertopic()));
				map.put("IDUSER", String.valueOf(userTopic.getIduser()));
				map.put("NAME", userTopic.getName());
				map.put("DESCRIPTION", userTopic.getDescription());
				map.put("LAT", String.valueOf(userTopic.getLat()));
				map.put("LNG", String.valueOf(userTopic.getLng()));
				map.put("IMAGE", userTopic.getImage());
				map.put("COLOR", userTopic.getColor());

				userTopicList.add(map);
			}

		} catch (Exception e) {
			Log.d("OperationGetUserTopic", "ERROR ERROR ERROR " + e.toString());
		} finally {

			try {
				statement.close();
				result.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return userTopicList;
	}

}
