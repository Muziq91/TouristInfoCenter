package ro.mmp.tic.service.sqlite.sqliteservice;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Like;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UpdateLikeService extends UpdateDataBaseService {

	public UpdateLikeService(SQLiteDatabase db) {

		this.db = db;
	}

	@Override
	public void insertLikes(ArrayList<Like> likes, String username) {

		Log.d("UpdateLikeService", "Username " + username);

		for (Like l : likes) {

			if (getTopicLike(l).isEmpty()) {
				String sqlQuery = "Insert into like (iduser,idtopic,idusertopic,likes,unlikes) "
						+ "VALUES((Select u.iduser from user u where u.username='"
						+ username
						+ "')"
						+ ","
						+ l.getIdtopic()
						+ ","
						+ l.getIdusertopic()
						+ ","
						+ l.getLike()
						+ ","
						+ l.getUnlike() + ")";
				Log.d("UpdateLikeService", sqlQuery);
				db.execSQL(sqlQuery);

			}

			if (getUserTopicLike(l).isEmpty()) {
				String sqlQuery = "Insert into like (iduser,idtopic,idusertopic,likes,unlikes) "
						+ "VALUES((Select u.iduser from user u where u.username='"
						+ username
						+ "')"
						+ ","
						+ l.getIdtopic()
						+ ","
						+ l.getIdusertopic()
						+ ","
						+ l.getLike()
						+ ","
						+ l.getUnlike() + ")";
				Log.d("UpdateLikeService", sqlQuery);
				db.execSQL(sqlQuery);

			}
		}

	}

	public HashMap<String, Integer> getTopicLike(Like l) {

		HashMap<String, Integer> likes = new HashMap<String, Integer>(0);

		String query = "Select * from like where idtopic=" + l.getIdtopic()
				+ " and idusertopic=1";

		Log.d("UpdateLikeService", query);

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {

				likes.put("idlike", cursor.getInt(0));
				likes.put("iduser", cursor.getInt(1));
				likes.put("idtopic", cursor.getInt(2));
				likes.put("idusertopic", cursor.getInt(3));
				likes.put("likes", cursor.getInt(4));
				likes.put("unlikes", cursor.getInt(5));

			} while (cursor.moveToNext());
		}

		cursor.close();
		return likes;
	}

	public HashMap<String, Integer> getUserTopicLike(Like l) {
		HashMap<String, Integer> likes = new HashMap<String, Integer>(0);

		String query = "Select * from like where idusertopic="
				+ l.getIdusertopic() + " and idtopic=1";
		Cursor cursor = db.rawQuery(query, null);
		Log.d("UpdateLikeService", query);
		if (cursor.moveToFirst()) {
			do {
				likes.put("idlike", cursor.getInt(0));
				likes.put("iduser", cursor.getInt(1));
				likes.put("idtopic", cursor.getInt(2));
				likes.put("idusertopic", cursor.getInt(3));
				likes.put("likes", cursor.getInt(4));
				likes.put("unlikes", cursor.getInt(5));

			} while (cursor.moveToNext());
		}

		cursor.close();
		return likes;
	}

}
