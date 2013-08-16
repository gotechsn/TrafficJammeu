package sn.gotech.trafficjammeu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataSource {

	// Database fields
	public static SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] columns = new String[] {
			MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_FIRST_LAT,
			MySQLiteHelper.COLUMN_FIRST_LNG,
			MySQLiteHelper.COLUMN_SECOND_LAT,
			MySQLiteHelper.COLUMN_SECOND_LNG,
			MySQLiteHelper.COLUMN_TYPE_ALERT, 
			MySQLiteHelper.COLUMN_DESCRIPTION, 
			MySQLiteHelper.COLUMN_USER, 
			MySQLiteHelper.COLUMN_ROUTE_ID
			};

	public DataSource (Context context) {
		dbHelper = new MySQLiteHelper(context);
		open();
	}

	public void upgradeDatabase(int oldVersion, int newVersion) {
		dbHelper.onUpgrade(database, oldVersion, newVersion);
	}
	
	public void open() throws SQLException {
		try {
			database = dbHelper.getWritableDatabase();
			Log.i("ouverture bdd", "reussi");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("ouverture bdd", "echec");
		}
	}

	public void close() {
		try {
			dbHelper.close();
			database.close();
			Log.i("fermeture bdd", "r�ussie");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("fermeture bdd", "echec");
		}
	}

	public void deleteRoute(Route route) {
		long id = route.getId();
		try {
			database.delete(MySQLiteHelper.TABLE_ROUTE, MySQLiteHelper.COLUMN_ID + " = " + id, null);
			Log.i("suppression route num: " + id ,  "reussie");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("suppression route num: " + id ,  "echec");
		}
	}
	
	public void addRoute(Route route) {
		if(!routeExists(route)) {
			ContentValues cv = new ContentValues();
			cv.put(MySQLiteHelper.COLUMN_FIRST_LAT, route.getFirstLatLng().latitude);
			cv.put(MySQLiteHelper.COLUMN_FIRST_LNG, route.getFirstLatLng().longitude);
			cv.put(MySQLiteHelper.COLUMN_SECOND_LAT, route.getSecondLatLng().latitude);
			cv.put(MySQLiteHelper.COLUMN_SECOND_LNG, route.getSecondLatLng().longitude);
			cv.put(MySQLiteHelper.COLUMN_DESCRIPTION, route.getDesc());
			cv.put(MySQLiteHelper.COLUMN_USER, route.getUser());
			cv.put(MySQLiteHelper.COLUMN_TYPE_ALERT, route.getTypeAlert());
			cv.put(MySQLiteHelper.COLUMN_ROUTE_ID, route.getRouteId());
			database.insert(MySQLiteHelper.TABLE_ROUTE, null, cv);
		}
	}
	
	public boolean  routeExists(Route route){
		Cursor cursor = getRoute(String.valueOf(route.getId()));
		boolean mReturn = false;
		if(cursor.getCount() != 0){
			mReturn = true;
		}
		cursor.close();
		return mReturn;
	}

	private Cursor getRoute(String routeId){
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ROUTE,
				columns, MySQLiteHelper.COLUMN_ROUTE_ID + " = " + routeId , null, null, null, null);
		return cursor;
	}
}