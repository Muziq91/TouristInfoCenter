package ro.mmp.tic.service.sqlite.sqliteservice;

import java.util.ArrayList;

import ro.mmp.tic.domain.Presentation;
import ro.mmp.tic.service.sqlite.UpdateDataBaseService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UpdatePresentationService extends UpdateDataBaseService {

	public UpdatePresentationService(SQLiteDatabase db) {

		this.db = db;
	}

	public void insertPresentation(ArrayList<Presentation> presentations) {

		Log.d("UpdatePresentationService", "1 Inserting presentation");
		for (Presentation p : presentations) {

			if (getPresentaion(p.getIdpresentation(), db).getDescription() == null) {
				Log.d("UpdatePresentationService", "2 Inserting presentation");

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
				Log.d("UpdatePresentationService", "3 Finished inerting");

			}
		}

	}

	public Presentation getPresentaion(int idPresentaion, SQLiteDatabase db) {
		Log.d("UpdatePresentationService", "Verifying presentation");
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
