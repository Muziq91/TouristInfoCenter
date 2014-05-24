package ro.mmp.tic.service.sqlite.sqliteservice;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UpdateLikeService extends UpdateDataBaseService {
	
	public UpdateLikeService(SQLiteDatabase db) {

		this.db = db;
	}

	@Override
	public void insertLikes(ArrayList<Like> likes,String  username) {
		
		for (Like l : likes) {

			if (getTopicLike(l).isEmpty() && getUserTopicLike(l).isEmpty()) {
				String sqlQuery = "Insert into like (iduser,idtopic,idusertopic,likes,unlikes) "
						+ "VALUES((Select u.iduser from user u where u.username='"+username+"')"
						+ ","
						+ l.getIdtopic()
						+ ","
						+ l.getIdusertopic()
						+ ","
						+ l.getLike() + "," + l.getUnlike() + ")";
				db.execSQL(sqlQuery);

			}
		}

	}

	public HashMap<String, String> getTopicLike(Like l) {
		HashMap<String, String> topic = new HashMap<String, String>(0);

		String query = "Select * from like where idtopic='" + l.getIdtopic()+ "' and idusertopic=1";

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

		return topic;
	}
	
	
	public HashMap<String, String> getUserTopicLike(Like l) {
		HashMap<String, String> topic = new HashMap<String, String>(0);

		String query = "Select * from like where idusertopic='" + l.getIdusertopic()+ "' and idtopic=1";

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

		return topic;
	}
	
}
