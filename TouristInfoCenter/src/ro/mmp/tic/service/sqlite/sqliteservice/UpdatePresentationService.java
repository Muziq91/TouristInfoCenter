/**
 * @author Matei Mircea
 * 
 * This class extends the UpdateDataBaseService and overrides the inserPresentation method
 */
package ro.mmp.tic.service.sqlite.sqliteservice;

import java.util.ArrayList;

import ro.mmp.tic.domain.Presentation;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UpdatePresentationService extends UpdateDataBaseService {

	public UpdatePresentationService(SQLiteDatabase db) {

		this.db = db;
	}

	public void insertPresentation(ArrayList<Presentation> presentations) {

		for (Presentation p : presentations) {

			if (getPresentaion(p.getIdpresentation()).getDescription() == null) {

				String sqlQuery = "Insert into presentation (idpresentation,idtopic,image,description) "
						+ "VALUES("
						+ p.getIdpresentation()
						+ ","
						+ p.getIdtopic()
						+ ",'"
						+ p.getImage()
						+ "','"
						+ p.getDescription() + "')";
				db.execSQL(sqlQuery);

			}
		}

	}

	public Presentation getPresentaion(int idPresentaion) {
		Presentation presentation = new Presentation();

		String query = "Select idpresentation,idtopic,description from presentation where idpresentation="
				+ idPresentaion;

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				presentation.setIdpresentation(cursor.getInt(0));
				presentation.setIdtopic(cursor.getInt(1));
				presentation.setDescription(cursor.getString(2));

			} while (cursor.moveToNext());
		}

		return presentation;
	}
}
