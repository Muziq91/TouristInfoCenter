/**
 * @author Matei Mircea
 * 
 * This class extends UpdateDataBaseService and overrides the insertTopic method
 */
package ro.mmp.tic.service.sqlite.sqliteservice;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UpdateTopicService extends UpdateDataBaseService {

	public UpdateTopicService(SQLiteDatabase db) {

		this.db = db;
	}

	@Override
	public void insertTopic(ArrayList<Topic> topics) {

		for (Topic t : topics) {

			if (getTopic(t.getName()).isEmpty()) {
				String sqlQuery = "Insert into topic (idcategory,idtype,name,address,lat,lng) "
						+ "VALUES("
						+ t.getIdcategory()
						+ ","
						+ t.getIdtype()
						+ ",'"
						+ t.getName()
						+ "','"
						+ t.getAddress()
						+ "',"
						+ t.getLat() + "," + t.getLng() + ")";
				db.execSQL(sqlQuery);

			}
		}

	}

	public HashMap<String, String> getTopic(String topicName) {
		HashMap<String, String> topic = new HashMap<String, String>(0);

		String query = "Select * from topic where name='" + topicName + "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				topic.put("idtopic", cursor.getString(0));
				topic.put("idcategory", cursor.getString(1));
				topic.put("idtype", cursor.getString(2));
				topic.put("name", cursor.getString(3));
				topic.put("address", cursor.getString(4));
				topic.put("lat", cursor.getString(5));
				topic.put("lng", cursor.getString(6));

			} while (cursor.moveToNext());
		}

		cursor.close();
		return topic;
	}
}
