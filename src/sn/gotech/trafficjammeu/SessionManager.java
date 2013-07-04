package sn.gotech.trafficjammeu;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context context;
     
    private static final String PREF_NAME = "trafficJammeuPreferences";
    private static final int DEFAULT_ZOOM_SIZE = 17;
    private static final float DEFAULT_DRAW_WIDTH = 7;
    private static final int DEFAULT_DRAW_COLOR = Color.BLUE;
    public static final String KEY_UID = "uid";
    public static final String KEY_DRAW_COLOR = "draw_color";
    public static final String KEY_ZOOM_SIZE = "zoom_size";
    public static final String KEY_VIEW_TYPE = "view_type";
	private static final String KEY_ANIME_TO_LNG = "anime_to_lng";
	private static final String KEY_ANIME_TO_LAT = "anime_to_lat";
	private static final double DEFAULT_ANIMATE_TO_LAT = 14;
	private static final double DEFAULT_ANIMATE_TO_LNG = -17;
	private static final String KEY_DRAW_WIDTH = "draw_width";
	private static final int DEFAULT_VIEW_TYPE = GoogleMap.MAP_TYPE_NORMAL;
	private static final String KEY_TUTO_SHOWN = "tuto_shown";
    private static String IS_SESSION_ACTIVED = "is_session_active";
    private static int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createSession(){

        editor.putBoolean(IS_SESSION_ACTIVED, true);
        editor.putString(KEY_UID, generateID());
        editor.commit();
    }
    
    public boolean isTutoShown(){
    	return pref.getBoolean(KEY_TUTO_SHOWN, false);
    }
    
    public void setTutoShown(boolean shown){
    	editor.putBoolean(KEY_TUTO_SHOWN, shown);
    	editor.commit();
    }
    
    public void setDrawColor(int color) {
    	editor.putInt(KEY_DRAW_COLOR, color);
    	editor.commit();
    }
    
    public int getDrawColor() {
    	return pref.getInt(KEY_DRAW_COLOR, DEFAULT_DRAW_COLOR);
    }
    

    public void setZoomSize(int size) {
    	editor.putInt(KEY_ZOOM_SIZE, size);
    	editor.commit();
    }
    
    public int getZoomSize() {
    	return pref.getInt(KEY_ZOOM_SIZE, DEFAULT_ZOOM_SIZE);
    }
    
    public void setDrawWidth(float width) {
    	editor.putFloat(KEY_DRAW_WIDTH, width);
    	editor.commit();
    }

    public float getDrawWidth() {
    	return pref.getFloat(KEY_DRAW_WIDTH, DEFAULT_DRAW_WIDTH);
    }
    
    public void setViewType(int type ) {
    	editor.putInt(KEY_VIEW_TYPE, type);
    	editor.commit();
    }
    
    public int getViewType() {
    	return pref.getInt(KEY_VIEW_TYPE, DEFAULT_VIEW_TYPE);
    }
    
    public String getUID(){
    	return pref.getString(KEY_UID, "vide");
    }
    
    public boolean hasUID(){
    	return pref.getBoolean(IS_SESSION_ACTIVED, false);
    }
    
    public String generateID(){
    	return UUID.randomUUID().toString();
    }
        
    public int getDefaultViewType() {
    	return pref.getInt(KEY_VIEW_TYPE, DEFAULT_VIEW_TYPE);
    }
    
    public void setDefaultViewType(int type) {
    	editor.putInt(KEY_VIEW_TYPE, type);
    	editor.commit();
    }
    
    public String getAnimeToLat() {
    	return pref.getString(KEY_ANIME_TO_LAT, "" + DEFAULT_ANIMATE_TO_LAT);
    }
    
    public String getAnimeToLng() {
    	return pref.getString(KEY_ANIME_TO_LNG, "" + DEFAULT_ANIMATE_TO_LNG);
    }
        
    public void setAnimeToLat(String lat) {
    	editor.putString(KEY_ANIME_TO_LAT, lat);
    	editor.commit();
    }
    
    public void setAnimeToLng(String lng) {
    	editor.putString(KEY_ANIME_TO_LNG, lng);
    	editor.commit();
    }
}