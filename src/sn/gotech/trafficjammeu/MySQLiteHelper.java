package sn.gotech.trafficjammeu;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	//Noms des tables 
	public static final String TABLE_ROUTE	= "item";
	public static final String COLUMN_ID = "_ID";
	public static final String COLUMN_FIRST_LAT = "first_lat";
	public static final String COLUMN_FIRST_LNG = "first_lng";
	public static final String COLUMN_SECOND_LAT = "second_lat";
	public static final String COLUMN_SECOND_LNG = "second_lng";
	public static final String COLUMN_TYPE_ALERT = "type_alert";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_USER = "user_login";
	public static final String COLUMN_ROUTE_ID = "route_id";
	
	private static final String DATABASE_NAME = "traffic_jammeu_db";
	private static final int DATABASE_VERSION = 4;

	// Database creation sql statement

	private static final String CREATE_TABLE_ROUTE 	= "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTE + " ( " +
			COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
			COLUMN_FIRST_LAT + " VARCHAR(255) NOT NULL, " +
			COLUMN_FIRST_LNG + " VARCHAR(255) NOT NULL, " +
			COLUMN_SECOND_LAT + " VARCHAR(255) NOT NULL, " +
			COLUMN_SECOND_LNG + " VARCHAR(255) NOT NULL, " +
			COLUMN_TYPE_ALERT + " INTEGER NOT NULL, " +
			COLUMN_DESCRIPTION + " VARCHAR(255) NOT NULL, " +
			COLUMN_ROUTE_ID + " VARCHAR(255) NOT NULL, " +
			COLUMN_USER + " VARCHAR(255) NOT NULL" +
			")";
	
	public MySQLiteHelper(Context context) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
		try {
			database.execSQL(CREATE_TABLE_ROUTE);				
			Log.i("Creation bdd", "reussie");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("Creation bdd", "echec");
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("mise a jour bdd", "De la version " + oldVersion + " � la version " + newVersion + ", vous perdrez toutes les donnees");
		try {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE);				
			onCreate(db);
			Log.i("Suppression bdd", "reussie");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Suppression bdd", "echec");
		}	
	}	
} 		