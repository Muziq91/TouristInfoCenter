package ro.mmp.tic.service.sqlite.sqliteservice;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Category;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UpdateCategoryService extends UpdateDataBaseService {

	public UpdateCategoryService(SQLiteDatabase db) {
		this.db = db;
	}

	public void insertCategory(ArrayList<Category> categories) {

		for (Category c : categories) {

			if (getCategory(c.getCategory(), db).isEmpty()) {
				String sqlQuery = "Insert into category (category,color) "
						+ "VALUES('" + c.getCategory() + "','" + c.getColor()
						+ "')";

				db.execSQL(sqlQuery);

			}
		}

	}

	public HashMap<String, String> getCategory(String categoryName,
			SQLiteDatabase db) {
		HashMap<String, String> category = new HashMap<String, String>(0);

		String query = "Select * from category where category='" + categoryName
				+ "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {

				category.put("idcategory", cursor.getString(0));
				category.put("category", cursor.getString(1));
				category.put("color", cursor.getString(2));

			} while (cursor.moveToNext());
		}

		return category;
	}

}
