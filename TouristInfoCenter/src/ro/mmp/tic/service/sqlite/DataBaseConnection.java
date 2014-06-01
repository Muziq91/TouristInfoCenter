/**
 * @author Matei Mircea
 * 
 * 
 * This class helps with the creation and maintenaince of the sqlite database
 */

package ro.mmp.tic.service.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ro.mmp.tic.adapter.model.CustomMapModel;
import ro.mmp.tic.adapter.model.MapModel;
import ro.mmp.tic.domain.Category;
import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Presentation;
import ro.mmp.tic.domain.Schedule;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.Type;
import ro.mmp.tic.domain.UserPref;
import ro.mmp.tic.domain.UserTopic;
import ro.mmp.tic.service.sqlite.sqliteservice.UpdateCategoryService;
import ro.mmp.tic.service.sqlite.sqliteservice.UpdateLikeService;
import ro.mmp.tic.service.sqlite.sqliteservice.UpdatePresentationService;
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
		String categorytable = "create table category(idcategory integer primary key AUTOINCREMENT, category text, color text)";
		String typeTable = "create table type(idtype integer primary key AUTOINCREMENT, type text)";
		String likeTable = "create table like(idlike integer primary key AUTOINCREMENT, iduser integer, idtopic integer, idusertopic integer, likes integer,unlikes integer, FOREIGN KEY (iduser) references user(iduser), FOREIGN KEY (idtopic) references topic(idtopic), FOREIGN KEY (idusertopic) references usertopic(idusertopic))";
		String topicTable = "create table topic(idtopic integer primary key AUTOINCREMENT, idcategory integer, idtype integer, name text, address text, lat real, lng,real, FOREIGN KEY (idcategory) references category(idcategory), FOREIGN KEY (idtype) references type(idtype))";
		String scheduleTable = "create table schedule(idschedule integer primary key AUTOINCREMENT, date text,time text, place text, alarmnr integer)";
		String presentationTable = "create table presentation(idpresentation integer primary key AUTOINCREMENT, idtopic integer, image text,description text,  FOREIGN KEY (idtopic) references topic(idtopic))";
		String userTopicTable = "create table usertopic(idusertopic integer primary key AUTOINCREMENT, iduser integer, name text, description text, image text, color text, lat real, lng real,  FOREIGN KEY (iduser) references user(iduser))";
		String userPrefTable = "create table userpref(iduserpref integer primary key AUTOINCREMENT, iduser integer, favfood text, favactivity text, likehistory text,  FOREIGN KEY (iduser) references user(iduser))";

		db.execSQL(userTable);
		db.execSQL(categorytable);
		db.execSQL(typeTable);
		db.execSQL(likeTable);
		db.execSQL(topicTable);
		db.execSQL(scheduleTable);
		db.execSQL(presentationTable);
		db.execSQL(userTopicTable);
		db.execSQL(userPrefTable);

		closeDB();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		String userTable = "DROP TABLE IF EXISTS user";
		String topicTable = "DROP TABLE IF EXISTS topic";
		String typeTable = "DROP TABLE IF EXISTS type";
		String categoryTable = "DROP TABLE IF EXISTS category";
		String likeTable = "DROP TABLE IF EXISTS like";
		String scheduleTable = "DROP TABLE IF EXISTS schedule";
		String presentationTable = "DROP TABLE IF EXISTS presentation";
		String userTopicTable = "DROP TABLE IF EXISTS usertopic";
		String userPrefTable = "DROP TABLE IF EXISTS userpref";

		db.execSQL(userTable);
		db.execSQL(typeTable);
		db.execSQL(categoryTable);
		db.execSQL(topicTable);
		db.execSQL(likeTable);
		db.execSQL(scheduleTable);
		db.execSQL(presentationTable);
		db.execSQL(userTopicTable);
		db.execSQL(userPrefTable);

		onCreate(db);
		closeDB();
	}

	public void upgrade() {
		db = this.getWritableDatabase();
		onUpgrade(db, 0, 1);
	}

	/**************************************************************************************/
	/**
	 * REGISTER OPERATIONS
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

	/**
	 * close the databse connection
	 */
	private void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	/**************************************************************************************/

	public boolean insertLikes(ArrayList<Like> likes, String username) {
		db = this.getWritableDatabase();
		UpdateDataBaseService uds = new UpdateLikeService(db);
		uds.insertLikes(likes, username);
		closeDB();

		return true;

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

	public HashMap<String, String> getUserTopicLike(String username,
			String topicName) {

		HashMap<String, String> likes = new HashMap<String, String>(0);
		db = this.getWritableDatabase();

		String sqlQuery = "SELECT l.idlike,l.iduser,l.idtopic,l.idusertopic ,l.likes,l.unlikes FROM like l "
				+ "join user u ON l.iduser = u.iduser "
				+ "join usertopic ut on l.idusertopic = ut.idusertopic "
				+ "where u.username='"
				+ username
				+ "' "
				+ "AND ut.name='"
				+ topicName + "'";
		Cursor cursor = db.rawQuery(sqlQuery, null);
		if (cursor.moveToFirst()) {
			do {
				likes.put("idlike", cursor.getString(0));
				likes.put("iduser", cursor.getString(1));
				likes.put("idtopic", cursor.getString(2));
				likes.put("idusertopic", cursor.getString(3));
				likes.put("likes", cursor.getString(4));
				likes.put("unlikes", cursor.getString(5));

			} while (cursor.moveToNext());
		}

		closeDB();

		return likes;
	}

	public void insertUserTopicLike(String username, String topicName, Like like) {
		db = this.getWritableDatabase();
		String sqlQuery = "Insert into like (iduser,idtopic, idusertopic,likes,unlikes) "
				+ "VALUES( (Select u.iduser from user u  where u.username='"
				+ username
				+ "'),'"
				+ 1
				+ "',"
				+ "(Select t.idusertopic from usertopic t where t.name='"
				+ topicName
				+ "'),'"
				+ like.getLike()
				+ "','"
				+ like.getUnlike() + "')";
		db.execSQL(sqlQuery);
		closeDB();
	}

	public void insertLike(String username, String topicName, Like like) {
		db = this.getWritableDatabase();
		String sqlQuery = "Insert into like (iduser,idtopic, idusertopic,likes,unlikes) "
				+ "VALUES( (Select u.iduser from user u  where u.username='"
				+ username
				+ "'),"
				+ "(Select t.idtopic from topic t where t.name='"
				+ topicName
				+ "'),'"
				+ 1
				+ "','"
				+ like.getLike()
				+ "','"
				+ like.getUnlike() + "')";
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

	/**************************************************************************************/
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
				category.put("color", cursor.getString(2));

			} while (cursor.moveToNext());
		}

		closeDB();

		return category;
	}

	/**************************************************************************************/
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

	/**************************************************************************************/
	/**
	 * Topic methods
	 * 
	 * @param topics
	 */

	public boolean insertTopic(ArrayList<Topic> topics) {
		Log.d("DatabaeConnection", "5 incercam din nou sa inseram");
		db = this.getWritableDatabase();
		UpdateDataBaseService uds = new UpdateTopicService(db);
		uds.insertTopic(topics);
		closeDB();

		return true;

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

	public ArrayList<HashMap<String, String>> getTopicAfterCategory(
			String categoryName, boolean likeHistory) {
		ArrayList<HashMap<String, String>> topics = new ArrayList<HashMap<String, String>>(
				0);

		db = this.getWritableDatabase();
		String query;
		if (likeHistory) {
			query = "SELECT * FROM topic t "
					+ "join type ct on t.idtype=ct.idtype "
					+ "join category c on t.idcategory = c.idcategory where c.category='"
					+ categoryName + "'";
		} else {
			query = "SELECT * FROM topic t "
					+ "join type ct on t.idtype=ct.idtype "
					+ "join category c on t.idcategory = c.idcategory where c.category='"
					+ categoryName + "' and ct.type<>'History'";
		}

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> topic = new HashMap<String, String>(0);
				topic.put("idtopic", cursor.getString(0));
				topic.put("idcategory", cursor.getString(1));
				topic.put("idtype", cursor.getString(2));
				topic.put("name", cursor.getString(3));
				topic.put("address", cursor.getString(4));
				topic.put("lat", cursor.getString(5));
				topic.put("lng", cursor.getString(6));
				topics.add(topic);

			} while (cursor.moveToNext());
		}

		closeDB();

		return topics;
	}

	public ArrayList<HashMap<String, String>> getTopicAfterType(String typeName) {

		ArrayList<HashMap<String, String>> topics = new ArrayList<HashMap<String, String>>(
				0);

		db = this.getWritableDatabase();

		String query = "SELECT * FROM topic t "
				+ "join type ct on t.idtype=ct.idtype "
				+ "join category c on t.idcategory = c.idcategory "
				+ "where ct.type='" + typeName + "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> topic = new HashMap<String, String>(0);
				topic.put("idtopic", cursor.getString(0));
				topic.put("idcategory", cursor.getString(1));
				topic.put("idtype", cursor.getString(2));
				topic.put("name", cursor.getString(3));
				topic.put("address", cursor.getString(4));
				topic.put("lat", cursor.getString(5));
				topic.put("lng", cursor.getString(6));
				topics.add(topic);

			} while (cursor.moveToNext());
		}

		closeDB();

		return topics;

	}

	/**************************************************************************************/
	/**
	 * Presentation methods
	 * 
	 * @param presentations
	 */

	public boolean insertPresentation(ArrayList<Presentation> presentations) {
		db = this.getWritableDatabase();
		UpdateDataBaseService uds = new UpdatePresentationService(db);
		uds.insertPresentation(presentations);
		closeDB();
		return true;

	}

	public Presentation getPresentaion(String presentationName) {
		Presentation presentation = new Presentation();

		Log.d("DatabaseConnection", "GETING");
		db = this.getWritableDatabase();

		String query = "SELECT p.idpresentation,p.idtopic,p.image,p.description FROM presentation p "
				+ "join topic t on  p.idtopic = t.idtopic "
				+ "where t.name = '" + presentationName + "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {

				presentation.setIdpresentation(cursor.getInt(0));
				presentation.setIdtopic(cursor.getInt(1));
				presentation.setImage(cursor.getString(2));
				presentation.setDescription(cursor.getString(3));
				Log.d("DatabaseConnection",
						"WE HAVE SOMETHING " + presentation.getIdpresentation());

			} while (cursor.moveToNext());
		}

		closeDB();

		return presentation;
	}

	public Presentation getUserTopicPresentaion(String topicName) {
		Presentation presentation = new Presentation();
		CustomMapModel customMapModel = getCustomMapModel(topicName);

		presentation.setImage(customMapModel.getUserTopic().getImage());
		presentation.setDescription(customMapModel.getUserTopic()
				.getDescription());

		return presentation;
	}

	/**************************************************************************************/

	/* METHODS FOR SELECT ACTIVITY */

	public ArrayList<HashMap<String, String>> getAllCategory() {
		ArrayList<HashMap<String, String>> allCategories = new ArrayList<HashMap<String, String>>(
				0);

		db = this.getWritableDatabase();

		String query = "Select * from category";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> category = new HashMap<String, String>(
						0);

				category.put("idcategory", cursor.getString(0));
				category.put("category", cursor.getString(1));
				category.put("color", cursor.getString(2));
				allCategories.add(category);

			} while (cursor.moveToNext());
		}

		closeDB();
		return allCategories;
	}

	public ArrayList<HashMap<String, String>> getAllTypes(
			ArrayList<String> selectedCategory) {
		db = this.getWritableDatabase();

		ArrayList<HashMap<String, String>> allTypes = new ArrayList<HashMap<String, String>>(
				0);

		for (String s : selectedCategory) {

			String query = "SELECT distinct t.idtype,t.type FROM type t "
					+ "join topic tp on t.idtype=tp.idtype "
					+ "join category c on c.idcategory=tp.idcategory "
					+ "where c.category='" + s + "'";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					HashMap<String, String> type = new HashMap<String, String>(
							0);

					type.put("idtype", cursor.getString(0));
					type.put("type", cursor.getString(1));
					allTypes.add(type);

				} while (cursor.moveToNext());
			}

		}

		closeDB();
		return allTypes;
	}

	public ArrayList<HashMap<String, String>> getAllLocations(
			ArrayList<String> seletedType) {
		db = this.getWritableDatabase();

		ArrayList<HashMap<String, String>> allLocations = new ArrayList<HashMap<String, String>>(
				0);

		for (String s : seletedType) {

			String query = "SELECT tp.name,tp.address,tp.lat,tp.lng FROM topic tp "
					+ "join type t  on tp.idtype=t.idtype "
					+ "join category c  on tp.idcategory=c.idcategory "
					+ "where t.type='" + s + "'";

			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					HashMap<String, String> location = new HashMap<String, String>(
							0);

					location.put("name", cursor.getString(0));
					location.put("address", cursor.getString(1));
					location.put("lat", cursor.getString(2));
					location.put("lng", cursor.getString(3));

					allLocations.add(location);

				} while (cursor.moveToNext());
			}

		}

		closeDB();
		return allLocations;
	}

	/**************************************************************************************/

	/**
	 * CREATE THE MAP MODEL
	 * 
	 * @param selectedLocation
	 * @return
	 */
	public ArrayList<MapModel> getMapModel(ArrayList<String> selectedLocation) {

		db = this.getWritableDatabase();
		ArrayList<MapModel> mapModel = new ArrayList<MapModel>(0);
		for (String s : selectedLocation) {

			String topicQuery = "SELECT tp.name,tp.address,tp.lat,tp.lng FROM topic tp "
					+ "where tp.name='" + s + "'";
			String colorQuery = "Select c.color from category c "
					+ "join topic tp  on c.idcategory=tp.idcategory "
					+ "where tp.name='" + s + "'";

			Cursor topiCursor = db.rawQuery(topicQuery, null);
			Cursor colorCursor = db.rawQuery(colorQuery, null);

			if (topiCursor.moveToFirst()) {
				do {
					Topic t = new Topic();
					String color = "default";

					if (colorCursor.moveToFirst()) {
						color = colorCursor.getString(0);
					}

					t.setName(topiCursor.getString(0));
					t.setAddress(topiCursor.getString(1));
					t.setLat(topiCursor.getDouble(2));
					t.setLng(topiCursor.getDouble(3));

					MapModel mm = new MapModel(t, color);
					mapModel.add(mm);

				} while (topiCursor.moveToNext());
			}

		}
		closeDB();
		return mapModel;
	}

	/**
	 * CREATE THE CUSTOM MAP MODEL
	 * 
	 * @param selectedLocation
	 * @return
	 */
	public ArrayList<CustomMapModel> getCustomMapModel() {

		db = this.getWritableDatabase();
		ArrayList<CustomMapModel> customMapModel = new ArrayList<CustomMapModel>(
				0);

		String customMapQuery = "SELECT ut.idusertopic, ut.iduser, ut.name, ut.description, ut.image,ut.color, ut.lat, ut.lng FROM usertopic ut";

		Cursor customMapCursor = db.rawQuery(customMapQuery, null);

		if (customMapCursor.moveToFirst()) {
			do {
				UserTopic t = new UserTopic();

				t.setIdusertopic(customMapCursor.getInt(0));
				t.setIduser(customMapCursor.getInt(1));
				t.setName(customMapCursor.getString(2));
				t.setDescription(customMapCursor.getString(3));
				t.setImage(customMapCursor.getString(4));
				t.setColor(customMapCursor.getString(5));
				t.setLat(customMapCursor.getDouble(6));
				t.setLng(customMapCursor.getDouble(7));

				CustomMapModel cm = new CustomMapModel(t);
				customMapModel.add(cm);
				Log.d("CustomMapModel", "Getting " + t.getName());
			} while (customMapCursor.moveToNext());
		}

		closeDB();

		Log.d("CustomMapModel", "Finished");
		return customMapModel;
	}

	public ArrayList<CustomMapModel> getUserCustomMapModel(String username) {
		db = this.getWritableDatabase();
		ArrayList<CustomMapModel> customMapModel = new ArrayList<CustomMapModel>(
				0);

		String customMapQuery = "SELECT ut.idusertopic, ut.iduser, ut.name, ut.description, ut.image,ut.color, ut.lat, ut.lng FROM usertopic ut join user u ON u.iduser = ut.iduser where u.username='"
				+ username + "'";

		Cursor customMapCursor = db.rawQuery(customMapQuery, null);

		if (customMapCursor.moveToFirst()) {
			do {
				UserTopic t = new UserTopic();

				t.setIdusertopic(customMapCursor.getInt(0));
				t.setIduser(customMapCursor.getInt(1));
				t.setName(customMapCursor.getString(2));
				t.setDescription(customMapCursor.getString(3));
				t.setImage(customMapCursor.getString(4));
				t.setColor(customMapCursor.getString(5));
				t.setLat(customMapCursor.getDouble(6));
				t.setLng(customMapCursor.getDouble(7));

				CustomMapModel cm = new CustomMapModel(t);
				customMapModel.add(cm);
				Log.d("CustomMapModel", "Getting " + t.getName());
			} while (customMapCursor.moveToNext());
		}

		closeDB();

		Log.d("CustomMapModel", "Finished");
		return customMapModel;
	}

	public ArrayList<HashMap<String, String>> getUserTopicModel() {
		db = this.getWritableDatabase();
		ArrayList<HashMap<String, String>> downloadMapModel = new ArrayList<HashMap<String, String>>(
				0);

		String customMapQuery = "SELECT ut.idusertopic, ut.iduser, ut.name, ut.description, ut.image,ut.color, ut.lat, ut.lng FROM usertopic ut where ut.image LIKE'%download%'";

		Cursor customMapCursor = db.rawQuery(customMapQuery, null);

		if (customMapCursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>(0);

				map.put("IDUSERTOPIC",
						String.valueOf(customMapCursor.getInt(0)));
				map.put("IDUSER", String.valueOf(customMapCursor.getInt(1)));
				map.put("NAME", customMapCursor.getString(2));
				map.put("DESCRIPTION", customMapCursor.getString(3));
				map.put("IMAGE", customMapCursor.getString(4));
				map.put("COLOR", customMapCursor.getString(5));
				map.put("LAT", String.valueOf(customMapCursor.getDouble(6)));
				map.put("LNG", String.valueOf(customMapCursor.getDouble(7)));

				downloadMapModel.add(map);
			} while (customMapCursor.moveToNext());
		}

		closeDB();

		Log.d("CustomMapModel", "Finished");
		return downloadMapModel;
	}

	public CustomMapModel getCustomMapModel(String name) {

		db = this.getWritableDatabase();
		CustomMapModel cm = null;

		String customMapQuery = "SELECT ut.idusertopic, ut.iduser, ut.name, ut.description, ut.image,ut.color, ut.lat, ut.lng FROM usertopic ut where ut.name='"
				+ name + "'";

		Cursor customMapCursor = db.rawQuery(customMapQuery, null);

		if (customMapCursor.moveToFirst()) {
			do {
				UserTopic t = new UserTopic();

				t.setIdusertopic(customMapCursor.getInt(0));
				t.setIduser(customMapCursor.getInt(1));
				t.setName(customMapCursor.getString(2));
				t.setDescription(customMapCursor.getString(3));
				t.setImage(customMapCursor.getString(4));
				t.setColor(customMapCursor.getString(5));
				t.setLat(customMapCursor.getDouble(6));
				t.setLng(customMapCursor.getDouble(7));

				cm = new CustomMapModel(t);

				Log.d("CustomMapModel", "Getting " + t.getName());
			} while (customMapCursor.moveToNext());
		}

		closeDB();

		Log.d("CustomMapModel", "Finished");
		return cm;
	}

	public void insertUserTopic(UserTopic ut, String username) {
		db = this.getWritableDatabase();

		Log.d("Inserting", "here isnerting");
		String sqlQuery = "Insert into usertopic (iduser,name,description,image,color,lat,lng) "
				+ "VALUES( (Select u.iduser from user u  where u.username='"
				+ username
				+ "'),"
				+ "'"
				+ ut.getName()
				+ "','"
				+ ut.getDescription()
				+ "','"
				+ ut.getImage()
				+ "','"
				+ ut.getColor()
				+ "','"
				+ ut.getLat()
				+ "','"
				+ ut.getLng()
				+ "')";

		Log.d("Inserting", sqlQuery);

		db.execSQL(sqlQuery);
		Log.d("CustomMapModel", "Inserting " + ut.getName());

		closeDB();

	}

	public void insertUserTopic(UserTopic ut) {
		db = this.getWritableDatabase();
		Log.d("INSERTED USER TOPIC INSIDE DATABSE",
				"INSERTED USER TOPIC INSIDE DATABSE");

		Log.d("Inserting", "here isnerting");
		String sqlQuery = "Insert into usertopic (idusertopic,iduser,name,description,image,color,lat,lng) "
				+ "VALUES('"
				+ ut.getIdusertopic()
				+ "','"
				+ ut.getIduser()
				+ "','"
				+ ut.getName()
				+ "','"
				+ ut.getDescription()
				+ "','"
				+ ut.getImage()
				+ "','"
				+ ut.getColor()
				+ "','"
				+ ut.getLat()
				+ "','" + ut.getLng() + "')";

		Log.d("Inserting", sqlQuery);

		db.execSQL(sqlQuery);
		Log.d("CustomMapModel", "Inserting " + ut.getName());

		closeDB();

	}

	public void updateCustomMap(HashMap<String, String> clickPosition) {
		db = this.getWritableDatabase();
		String sqlQuery = "UPDATE usertopic SET name='"
				+ clickPosition.get("NAME") + "',description='"
				+ clickPosition.get("DESCRIPTION") + "',lat='"
				+ clickPosition.get("LAT") + "',lng='"
				+ clickPosition.get("LNG") + "', color='"
				+ clickPosition.get("COLOR") + "' WHERE idusertopic ='"
				+ clickPosition.get("IDUSERTOPIC") + "'";
		db.execSQL(sqlQuery);
		closeDB();

	}

	public void deleteCustomMap(HashMap<String, String> clickPosition) {
		db = this.getWritableDatabase();
		String sqlQuery = "DELETE FROM usertopic  WHERE idusertopic ='"
				+ clickPosition.get("IDUSERTOPIC") + "'";
		db.execSQL(sqlQuery);
		closeDB();

	}

	/**************************************************************************************/
	/**
	 * Schedule methods
	 * 
	 * @param schedule
	 */

	public void saveSchedule(Schedule schedule) {
		db = this.getWritableDatabase();
		String sqlQuery = "Insert into schedule (date,time,place,alarmnr) "
				+ "VALUES('" + schedule.getDate() + "','" + schedule.getTime()
				+ "','" + schedule.getPlace() + "','" + schedule.getAlarmnr()
				+ "')";
		db.execSQL(sqlQuery);

		closeDB();

	}

	public ArrayList<Schedule> getAllSchedule() {

		ArrayList<Schedule> allSchedule = new ArrayList<Schedule>(0);
		allSchedule.clear();
		db = this.getWritableDatabase();

		String scheduleQuery = "SELECT s.idschedule,s.date,s.time,s.place,s.alarmnr FROM schedule s";

		Cursor scheduleCursor = db.rawQuery(scheduleQuery, null);

		if (scheduleCursor.moveToFirst()) {
			do {
				Schedule s = new Schedule();

				s.setIdschedule(scheduleCursor.getInt(0));
				s.setDate(scheduleCursor.getString(1));
				s.setTime(scheduleCursor.getString(2));
				s.setPlace(scheduleCursor.getString(3));
				s.setAlarmnr(scheduleCursor.getInt(4));

				allSchedule.add(s);

			} while (scheduleCursor.moveToNext());
		}

		closeDB();
		return allSchedule;
	}

	public ArrayList<Schedule> getLastSchedule() {

		ArrayList<Schedule> allSchedule = new ArrayList<Schedule>(0);
		allSchedule.clear();
		db = this.getWritableDatabase();

		String scheduleQuery = "SELECT s.idschedule,s.date,s.time,s.place,Max(s.alarmnr) FROM schedule s";

		Cursor scheduleCursor = db.rawQuery(scheduleQuery, null);

		if (scheduleCursor.moveToFirst()) {
			do {
				Schedule s = new Schedule();

				s.setIdschedule(scheduleCursor.getInt(0));
				s.setDate(scheduleCursor.getString(1));
				s.setTime(scheduleCursor.getString(2));
				s.setPlace(scheduleCursor.getString(3));
				s.setAlarmnr(scheduleCursor.getInt(4));

				allSchedule.add(s);

			} while (scheduleCursor.moveToNext());
		}

		closeDB();
		return allSchedule;
	}

	public void updateSchedule(Schedule updateSchedule) {
		db = this.getWritableDatabase();
		String sqlQuery = "UPDATE schedule SET date='"
				+ updateSchedule.getDate() + "',time='"
				+ updateSchedule.getTime() + "',place='"
				+ updateSchedule.getPlace() + "',alarmnr='"
				+ updateSchedule.getAlarmnr() + "' WHERE idschedule ='"
				+ updateSchedule.getIdschedule() + "'";

		Log.d("DataabseCon", sqlQuery);
		db.execSQL(sqlQuery);
		closeDB();

	}

	public void deleteSchedule(Schedule schedule) {
		db = this.getWritableDatabase();
		String sqlQuery = "DELETE FROM schedule  WHERE idschedule ='"
				+ schedule.getIdschedule() + "'";
		db.execSQL(sqlQuery);
		closeDB();

	}

	/**************************************************************************************/
	/**
	 * UserPreferecnes methods
	 * 
	 * @param up
	 * @param username
	 */

	public void saveUserPreferences(UserPref up, String username) {

		db = this.getWritableDatabase();
		String sqlQuery = "Insert into userpref (iduser,favfood,favactivity,likehistory)"
				+ "VALUES( (Select u.iduser from user u  where u.username='"
				+ username
				+ "'),"
				+ "'"
				+ up.getFavFood()
				+ "','"
				+ up.getFavActivity() + "','" + up.getLikeHistory() + "')";

		Log.d("DataBaseConenctio", sqlQuery);

		db.execSQL(sqlQuery);

		Log.d("DataBaseConenctio", "Prefrence Inserted");
		closeDB();

	}

	public ArrayList<UserPref> getAllUserPreferences(String username) {

		ArrayList<UserPref> alluserPref = new ArrayList<UserPref>(0);
		alluserPref.clear();
		db = this.getWritableDatabase();

		String scheduleQuery = "SELECT Max(up.iduserpref), up.iduser, up.favfood, up.favactivity, up.likehistory FROM userpref up join user u on up.iduser=u.iduser where u.username='"
				+ username + "'";

		Cursor userPrefCursor = db.rawQuery(scheduleQuery, null);

		if (userPrefCursor.moveToFirst()) {
			do {
				UserPref up = new UserPref();

				up.setIduserpref(userPrefCursor.getInt(0));
				up.setIduser(userPrefCursor.getInt(1));
				up.setFavFood(userPrefCursor.getString(2));
				up.setFavActivity(userPrefCursor.getString(3));
				up.setLikeHistory(userPrefCursor.getString(4));

				alluserPref.add(up);

			} while (userPrefCursor.moveToNext());
		}

		closeDB();
		return alluserPref;
	}

	public ArrayList<String> getAllDefaultSchedule(
			ArrayList<UserPref> allUserPref) {
		boolean likeHistory = false;
		ArrayList<String> allDefaultSchedule = new ArrayList<String>(0);

		UserPref up = allUserPref.get(0);
		ArrayList<HashMap<String, String>> allTopicsAfterCategory;

		if (up.getLikeHistory().equals("Yes")) {
			likeHistory = true;
		}

		if (up.getFavActivity().equals("Indoor")) {
			allTopicsAfterCategory = getTopicAfterCategory("Museum",
					likeHistory);

		} else {
			allTopicsAfterCategory = getTopicAfterCategory("Landmark",
					likeHistory);
		}

		ArrayList<HashMap<String, String>> allTopicsAfterType = getTopicAfterType(up
				.getFavFood());

		int poz = 0, ok = 0;
		boolean isSaved = true;
		Random random = new Random();
		Log.d("DataBaseConnection", "Incepem inserarea de topicuri in schedule");
		allDefaultSchedule.clear();

		if (allTopicsAfterCategory.size() <= 3) {

			Log.d("DataBaseConnection", "Dimensiunea e mai mica ca 3");
			for (HashMap<String, String> p : allTopicsAfterCategory) {
				String text = p.get("name") + "\n" + p.get("address");
				Log.d("DataBaseConnection", text);
				allDefaultSchedule.add(text);
			}
		} else {
			Log.d("DataBaseConnection", "Dimensiunea e mai mare ca 3");
			for (int i = 0; i < 3; i++) {
				Log.i("DataBaseConnection", "Inseram cate 3 " + i);
				ok = 0;
				while (ok != 1) {
					isSaved = true;

					Log.e("DataBaseConnection", "Intram in while");
					poz = random.nextInt(allTopicsAfterCategory.size());
					Log.d("DataBaseConnection", "poz=" + poz);
					for (String s : allDefaultSchedule) {

						Log.d("DataBaseConnection", "already inserted \n" + s);
						Log.d("DataBaseConnection",
								"what we want to insert \n"
										+ allTopicsAfterCategory.get(poz).get(
												"name")
										+ "\n"
										+ allTopicsAfterCategory.get(poz).get(
												"address"));
						if (s.equals(allTopicsAfterCategory.get(poz)
								.get("name")
								+ "\n"
								+ allTopicsAfterCategory.get(poz)
										.get("address"))) {

							Log.d("DataBaseConnection",
									"They are already inserted");
							isSaved = false;

						}
					}

					if (isSaved) {
						Log.e("DataBaseConnection",
								"They are not already isnerted");
						ok = 1;
					} else {
						ok = 0;
					}

				}

				String text = allTopicsAfterCategory.get(poz).get("name")
						+ "\n" + allTopicsAfterCategory.get(poz).get("address");
				allDefaultSchedule.add(text);

				Log.d("DataBaseConnection", "Am adaugat " + text);
			}

		}

		if (allTopicsAfterType.size() == 1) {
			String text = allTopicsAfterType.get(0).get("name") + "\n"
					+ allTopicsAfterType.get(0).get("address");

			allDefaultSchedule.add(text);
		} else {

			Log.d("DataBaseConnection", "Dimensiunea e mai mare ca 1");

			ok = 0;
			while (ok != 1) {
				isSaved = true;

				Log.e("DataBaseConnection", "Intram in while");
				poz = random.nextInt(allTopicsAfterType.size());
				Log.d("DataBaseConnection", "poz=" + poz);
				for (String s : allDefaultSchedule) {

					Log.d("DataBaseConnection", "already inserted \n" + s);
					Log.d("DataBaseConnection", "what we want to insert \n"
							+ allTopicsAfterType.get(poz).get("name") + "\n"
							+ allTopicsAfterType.get(poz).get("address"));
					if (s.equals(allTopicsAfterType.get(poz).get("name") + "\n"
							+ allTopicsAfterType.get(poz).get("address"))) {

						Log.d("DataBaseConnection", "They are already inserted");
						isSaved = false;

					}
				}

				if (isSaved) {
					Log.e("DataBaseConnection", "They are not already isnerted");
					ok = 1;
				} else {
					ok = 0;
				}

			}

			String text = allTopicsAfterType.get(poz).get("name") + "\n"
					+ allTopicsAfterType.get(poz).get("address");
			allDefaultSchedule.add(text);

			Log.d("DataBaseConnection", "Am adaugat " + text);

		}

		return allDefaultSchedule;
	}
}
