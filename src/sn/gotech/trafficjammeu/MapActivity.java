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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapActivity extends Activity implements OnMapLongClickListener, LocationListener, OnMarkerDragListener{

	private static final String MAP_VIEW_TYPE_SELECTED = "map_type_selected";
	private static final float MIN_ZOOM_LEVEL_FOR_MARKING = 13.0f;
	private GoogleMap map;
	private UiSettings mapSettings;
	private ArrayList<Marker> markers;
	private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment); 
        session = new SessionManager(getApplicationContext());
        
        manageUserInfo();
        manageTuto();
        configureMap();
        manageLocation();
    }
    
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(MAP_VIEW_TYPE_SELECTED)) {
			map.setMapType(savedInstanceState.getInt(MAP_VIEW_TYPE_SELECTED));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(MAP_VIEW_TYPE_SELECTED, map.getMapType());
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		ActionBar actionBar = getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayHomeAsUpEnabled(true);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		case R.id.action_info:
			showTuto();
			break;
			
		case R.id.action_change_view:
			changeMapType();
			break;
		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    public void changeMapType(){
    	
    	int type = map.getMapType(); 
    	Toast toast = new Toast(this);
    	
    	switch (type) {
		case GoogleMap.MAP_TYPE_NORMAL:
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			toast = getCustomToast(R.string.mapTypeSatellite);
			break;
		case GoogleMap.MAP_TYPE_SATELLITE:
			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			toast = getCustomToast(R.string.mapTypeStreet);
			break;
		case GoogleMap.MAP_TYPE_TERRAIN:
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			toast = getCustomToast(R.string.mapTypeHybrid);
			break;
		case GoogleMap.MAP_TYPE_HYBRID:
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			toast = getCustomToast(R.string.mapTypeNormal);
			break;

		default:
			break;
		}
    	toast.show();
    }
    
    public Toast getCustomToast(int stringResourceId){
    
    	LayoutInflater inflater = getLayoutInflater();
   	 
		View layout = inflater.inflate(R.layout.custom_toast,
		  (ViewGroup) findViewById(R.id.custom_toast_layout_id));

		// set a message
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(stringResourceId);

		// Toast...
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		
		return toast;
    }
    
    public Toast getCustomToast(String string){
        
    	LayoutInflater inflater = getLayoutInflater();
   	 
		View layout = inflater.inflate(R.layout.custom_toast,
		  (ViewGroup) findViewById(R.id.custom_toast_layout_id));

		// set a message
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(string);

		// Toast...
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		
		return toast;
    }
    
	public boolean configureMap() {
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		markers = new ArrayList<Marker>();
		boolean mReturn = false;
		if (map != null) {
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			
			mapSettings = map.getUiSettings();
			mapSettings.setCompassEnabled(true);
			mapSettings.setRotateGesturesEnabled(true);
			mapSettings.setZoomControlsEnabled(true);

	        map.animateCamera(CameraUpdateFactory.zoomTo(MIN_ZOOM_LEVEL_FOR_MARKING));
			map.setMyLocationEnabled(true); 
	        map.setOnMapLongClickListener(this);
	        map.setOnMarkerDragListener(this);
	        
			mReturn = true;
		}
		return mReturn;
	}

	private void manageLocation() {
		// TODO Auto-generated method stub
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		// Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
 
        }else { // Google Play Services are available
 
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
 
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
 
            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);
 
            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
	}
	
	public void manageTuto(){
		if(!session.isTutoShown()){
        	showTuto();
        }
	}
	
	public void showTuto(){
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
	        	} else {
	        		session.setTutoShown(false);
	        	}
			}
		});
	}
	public void manageUserInfo(){
		if(!session.hasUID()){
			
		}
	}
	
	public MarkerOptions createMarkerOptions(String title, String snippet, LatLng position, boolean draggable){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions
			.title(title)
			.draggable(draggable)
			.snippet(snippet)
			.position(position);
		
		return markerOptions;
	}
	
	@Override
	public void onMapLongClick(LatLng position) {
		// TODO Auto-generated method stub
		if(map.getCameraPosition().zoom <= MIN_ZOOM_LEVEL_FOR_MARKING){
			Toast toast = getCustomToast("Zoomez encore " + Math.round(MIN_ZOOM_LEVEL_FOR_MARKING - map.getCameraPosition().zoom) + " fois pour pouvoir marquer des routes");
			toast.show();
		} else {

			final LatLng point = position;

			Marker marker = map.addMarker(createMarkerOptions("test", "snippet", point, true));
			markers.add(marker);
			
			if (isOdd(markers.size())) {

				AlertDialog.Builder dialog = new AlertDialog.Builder(this);

				dialog.setTitle("Marquer ce trajet ?");
				CharSequence[] csitems = new CharSequence[3];
				csitems[0] = "Libre";
				csitems[1] = "Normal";
				csitems[2] = "Embouteillé";

				dialog.setSingleChoiceItems(csitems, 0, new OnClickListener() {

					private int alertType;

					public void onClick(DialogInterface arg0, int pos) {
						// TODO Auto-generated method stub
						alertType = pos;
						Log.i("SELECTED", String.valueOf(alertType));

					}
				});

				dialog.setPositiveButton(android.R.string.ok,
						new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								map.addMarker(createMarkerOptions("test",
										"snippet", point, true));
								drawBetween2LastPoints();
							}
						});
				dialog.setNegativeButton(android.R.string.cancel,
						new OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								// on enleve le dernier marker
								int removeIndex = markers.size() - 1;
								Marker removeMarker = markers.get(removeIndex);
								removeMarker.remove();
								markers.remove(removeIndex);
							}
						});
				dialog.create().show();
			}
		}
	}
	
	public void drawBetween2LastPoints(){
		LatLng origin = markers.get(markers.size() - 2).getPosition();
		LatLng dest = markers.get(markers.size() - 1).getPosition();
		String url = getDirectionsUrl(origin, dest);
		DownloadTask downloadTask = new DownloadTask();
		downloadTask.execute(url);
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
					lineOptions.width(7);
					lineOptions.color(Color.argb(125, 255, 0, 0));
					
				}
				
				// Drawing polyline in the Google Map for the i-th route
				if(lineOptions != null){
					map.addPolyline(lineOptions);							
				}
			}
		}			
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		// Getting latitude of the current location
        double latitude = location.getLatitude();
 
        // Getting longitude of the current location
        double longitude = location.getLongitude();
 
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
 
        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
 
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	// OnMarkerDrag methods
	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		ImageView deleteMarker = (ImageView) findViewById(R.id.deleteMarker);
		
//		marker.
		Rect rect = deleteMarker.getDrawable().copyBounds();
		deleteMarker.setVisibility(View.GONE);
		
		// in deleteMarker bouds
		if(true) {
			
		} 
		//else means that marker is moved. checkh if we redraw the route or note
		else {
			
		}
		
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		findViewById(R.id.deleteMarker).setVisibility(View.VISIBLE);
	}   
}
