/**
 * @author Matei Mircea
 * 
 * This class is an abstract class  that helps with the insert of categories, types,topics and presentation
 * when entering the central activity
 */

package ro.mmp.tic.service.sqlite;

import java.util.ArrayList;

import ro.mmp.tic.domain.Category;
import ro.mmp.tic.domain.Like;
import ro.mmp.tic.domain.Presentation;
import ro.mmp.tic.domain.Topic;
import ro.mmp.tic.domain.Type;
import android.database.sqlite.SQLiteDatabase;

public abstract class UpdateDataBaseService {

	protected SQLiteDatabase db;

	public void insertCategory(ArrayList<Category> categories) {
	}

	public void insertType(ArrayList<Type> types) {
	}

	public void insertTopic(ArrayList<Topic> topics) {
	}

	public void insertPresentation(ArrayList<Presentation> presentations) {

	}
	
	
	public void insertLikes(ArrayList<Like> likes, String username) {

	}
}
