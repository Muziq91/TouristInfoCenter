package ro.mmp.tic.service.sqlite;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.domain.Category;
import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.Type;
import ro.mmp.tic.service.sqlite.sqliteservice.UpdateCategoryService;
import ro.mmp.tic.service.sqlite.sqliteservice.UpdateTopicService;
import ro.mmp.tic.service.sqlite.sqliteservice.UpdateTypeService;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseConnection extends SQLiteOpenHelper {

	private SQLiteDatabase db;

	public DataBaseConnection(Context context) {

		super(context, "center.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String userTable = "create table user(iduser integer primary key AUTOINCREMENT, name text, username text, password text,email text, country text)";
		String categorytable = "create table category(idcategory integer primary key AUTOINCREMENT, category text)";
		String typeTable = "create table type(idtype integer primary key AUTOINCREMENT, type text)";
		String commentTable = "create table comment(idcomment integer primary key AUTOINCREMENT, idu integer,idt integer, comment text,FOREIGN KEY(idu) references user(iduser),FOREIGN KEY(idt) references topic(idtopic))";
		String likeTable = "create table like(idlike integer primary key AUTOINCREMENT, iduser integer, idtopic integer, likes integer,unlikes integer, FOREIGN KEY (iduser) references user(iduser), FOREIGN KEY (idtopic) references topic(idtopic))";
		String topicTable = "create table topic(idtopic integer primary key AUTOINCREMENT, idcategory integer, idtype integer, name text, address text, lat real, lng,real, FOREIGN KEY (idcategory) references category(idcategory), FOREIGN KEY (idtype) references type(idtype))";

		db.execSQL(userTable);
		db.execSQL(categorytable);
		db.execSQL(typeTable);
		db.execSQL(commentTable);
		db.execSQL(likeTable);
		db.execSQL(topicTable);

		closeDB();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		String userTable = "DROP TABLE IF EXISTS user";
		String topicTable = "DROP TABLE IF EXISTS topic";
		String typeTable = "DROP TABLE IF EXISTS type";
		String categoryTable = "DROP TABLE IF EXISTS category";
		String commentTable = "DROP TABLE IF EXISTS comment";
		String likeTable = "DROP TABLE IF EXISTS like";

		db.execSQL(userTable);
		db.execSQL(typeTable);
		db.execSQL(categoryTable);
		db.execSQL(topicTable);
		db.execSQL(commentTable);
		db.execSQL(likeTable);

		onCreate(db);
		closeDB();
	}

	public void upgrade() {
		db = this.getWritableDatabase();
		onUpgrade(db, 0, 1);
	}

	/**
	 * 
	 * @param user
	 */

	public void insertUserAtRegister(HashMap<String, String> user) {
		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("name", user.get("name"));
		values.put("username", user.get("username"));
		values.put("password", user.get("password"));
		values.put("email", user.get("email"));
		values.put("country", user.get("country"));
		db.insert("user", null, values);

		closeDB();
	}

	public HashMap<String, String> getUserAfterUsername(String username) {
		HashMap<String, String> user = new HashMap<String, String>(0);
		db = this.getWritableDatabase();
		String query = "Select * from user where username='" + username + "'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				user.put("iduser", cursor.getString(0));
				user.put("name", cursor.getString(1));
				user.put("username", cursor.getString(2));
				user.put("password", cursor.getString(3));
				user.put("email", cursor.getString(4));
				user.put("country", cursor.getString(5));

			} while (cursor.moveToNext());
		}

		closeDB();

		return user;
	}

	private void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	/**
	 * Operation on Like table
	 * 
	 * @param username
	 * @param topicName
	 * @return
	 */
	public HashMap<String, String> getLike(String username, String topicName) {

		HashMap<String, String> likes = new HashMap<String, String>(0);
		db = this.getWritableDatabase();

		String sqlQuery = "SELECT l.idlike,l.iduser,l.idtopic,l.likes,l.unlikes FROM like l "
				+ "join user u ON u.iduser = l.iduser "
				+ "join topic t on l.idtopic = t.idtopic "
				+ "where u.username='"
				+ username
				+ "' "
				+ "AND t.name='"
				+ topicName + "'";
		Cursor cursor = db.rawQuery(sqlQuery, null);
		if (cursor.moveToFirst()) {
			do {
				likes.put("idlike", cursor.getString(0));
				likes.put("iduser", cursor.getString(1));
				likes.put("idtopic", cursor.getString(2));
				likes.put("likes", cursor.getString(3));
				likes.put("unlikes", cursor.getString(4));

			} while (cursor.moveToNext());
		}

		closeDB();

		return likes;
	}

	public void insertLike(String username, String topicName, Like like) {
		db = this.getWritableDatabase();
		String sqlQuery = "Insert into like (iduser,idtopic,likes,unlikes) "
				+ "VALUES( (Select u.iduser from user u  where u.username='"
				+ username + "'),"
				+ "(Select t.idtopic from topic t where t.name='" + topicName
				+ "')," + "'" + like.getLike() + "','" + like.getUnlike()
				+ "')";
		db.execSQL(sqlQuery);
		closeDB();
	}

	public void updateLike(String username, String topicName, Like like) {
		db = this.getWritableDatabase();
		String sqlQuery = "UPDATE like SET likes='" + like.getLike()
				+ "',unlikes='" + like.getUnlike() + "' WHERE iduser ='"
				+ like.getIduser() + "' AND idtopic='" + like.getIdtopic()
				+ "'";
		db.execSQL(sqlQuery);
		closeDB();

	}

	/**
	 * category tables operations
	 * 
	 * @param categories
	 */
	public boolean insertCategory(ArrayList<Category> categories) {

		Log.i("DatabaseConnection", "1 am intrat in database conenction");
		db = this.getWritableDatabase();
		UpdateDataBaseService uds = new UpdateCategoryService(db);
		uds.insertCategory(categories);
		closeDB();

		return true;
	}

	public HashMap<String, String> getCategory(String categoryName) {
		HashMap<String, String> category = new HashMap<String, String>(0);

		db = this.getWritableDatabase();

		String query = "Select * from category where category='" + categoryName
				+ "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {

				category.put("idcategory", cursor.getString(0));
				category.put("category", cursor.getString(1));

			} while (cursor.moveToNext());
		}

		closeDB();

		return category;
	}

	/**
	 * Type methods
	 * 
	 * @param types
	 */

	public boolean insertType(ArrayList<Type> types) {

		db = this.getWritableDatabase();

		UpdateDataBaseService uds = new UpdateTypeService(db);
		uds.insertType(types);
		closeDB();

		return true;
	}

	public HashMap<String, String> getType(String typeName) {
		HashMap<String, String> type = new HashMap<String, String>(0);

		db = this.getWritableDatabase();

		String query = "Select * from type where type='" + typeName + "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				type.put("idtype", cursor.getString(0));
				type.put("type", cursor.getString(1));

			} while (cursor.moveToNext());
		}

		closeDB();

		return type;
	}

	/**
	 * Topic methods
	 * 
	 * @param topics
	 */

	public void insertTopic(ArrayList<Topic> topics) {
		Log.d("DatabaeConnection", "5 incercam din nou sa inseram");
		db = this.getWritableDatabase();
		UpdateDataBaseService uds = new UpdateTopicService(db);
		uds.insertTopic(topics);
		closeDB();

	}

	public HashMap<String, String> getTopic(String topicName) {
		HashMap<String, String> topic = new HashMap<String, String>(0);

		db = this.getWritableDatabase();

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

		closeDB();

		return topic;
	}

}
