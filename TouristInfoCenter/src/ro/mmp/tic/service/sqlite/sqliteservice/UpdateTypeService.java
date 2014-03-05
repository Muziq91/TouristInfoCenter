package ro.mmp.tic.service.sqlite.sqliteservice;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Type;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UpdateTypeService extends UpdateDataBaseService {

	public UpdateTypeService(SQLiteDatabase db) {
		this.db = db;
	}

	public void insertType(ArrayList<Type> types) {

		for (Type t : types) {

			if (getType(t.getType(), db).isEmpty()) {
				String sqlQuery = "Insert into type (type) " + "VALUES('"
						+ t.getType() + "')";
				db.execSQL(sqlQuery);

			}
		}

	}

	public HashMap<String, String> getType(String typeName, SQLiteDatabase db) {
		HashMap<String, String> type = new HashMap<String, String>(0);

		String query = "Select * from type where type='" + typeName + "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				type.put("idtype", cursor.getString(0));
				type.put("type", cursor.getString(1));

			} while (cursor.moveToNext());
		}

		return type;
	}
}
