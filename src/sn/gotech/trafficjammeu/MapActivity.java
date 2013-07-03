package sn.gotech.trafficjammeu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapActivity extends Activity implements OnMapLongClickListener {

	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap map;
	private UiSettings mapSettings;
	private ArrayList<LatLng> markerPoints;
	private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment); 
        session = new SessionManager(getApplicationContext());
        
        manageUserInfo();
        manageTuto();
        configureMap();
    }
    
	public boolean configureMap() {
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		markerPoints = new ArrayList<LatLng>();
		boolean mReturn = false;
		if (map != null) {
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			mapSettings = map.getUiSettings();
			mapSettings.setCompassEnabled(true);
			mapSettings.setRotateGesturesEnabled(true);
			mapSettings.setZoomControlsEnabled(true);
			map.setMyLocationEnabled(true);
			

	        map.setOnMapLongClickListener(this);
			mReturn = true;
		}
		return mReturn;
	}

	public void manageTuto(){
		if(!session.isTutoShown()){
        	LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        	View view = inflater.inflate(R.layout.tuto_layout, null);
        	final LinearLayout ll = (LinearLayout) findViewById(R.id.mainLayout);
        	ll.addView(view);
        	ll.setVisibility(View.VISIBLE);
        	
        	Button closeButton = (Button) view.findViewById(R.id.closeButton);
        	final CheckBox checkBox = (CheckBox) view.findViewById(R.id.doNotShowAgain);
        	
        	closeButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ll.setVisibility(View.GONE);
					if(checkBox.isChecked()) {
		        		session.setTutoShown(true);
		        	}
				}
			});
        }
	}
	
	public void manageUserInfo(){
		if(!session.hasUID()){
			Toast.makeText(this, "set user info huna", 5000).show();
		}
	}
	
	public MarkerOptions createMarkerOptions(String title, String snippet, LatLng position, boolean draggable){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions
			.title(title)
			.draggable(draggable)
			.snippet(snippet)
			.position(position);
		
		markerPoints.add(position);
		return markerOptions;
	}
	
	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		map.addMarker(createMarkerOptions("test", "snippet", point, true));
		
		if(isOdd(markerPoints.size())) {
			if(markerPoints.size() != 0){
				LatLng origin = markerPoints.get(markerPoints.size()-2);
				LatLng dest = markerPoints.get(markerPoints.size()-1);
				
				String url = getDirectionsUrl(origin, dest);				
				DownloadTask downloadTask = new DownloadTask();
				downloadTask.execute(url);
			}
		}
	}
	
	public boolean isOdd(int size) {
		return size % 2 == 0;
	}
	private String getDirectionsUrl(LatLng origin,LatLng dest){
		
		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		
		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
		
					
		// Sensor enabled
		String sensor = "sensor=false";			
					
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		
		return url;
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

	
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
				
			// For storing data from web service
			String data = "";
					
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = new PolylineOptions();
			
			// Traversing through all the routes
			if(result != null) {
				for(int i=0;i<result.size();i++){
					points = new ArrayList<LatLng>();
					
					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);
					
					// Fetching all the points in i-th route
					for(int j=0;j<path.size();j++){
						HashMap<String,String> point = path.get(j);					
						
						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);	
						
						points.add(position);						
					}
					
					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(session.getDrawWidth());
					lineOptions.color(session.getDrawColor());
					
				}
				
				// Drawing polyline in the Google Map for the i-th route
				if(lineOptions != null){
					map.addPolyline(lineOptions);							
				}
			}
		}			
    }   
}
