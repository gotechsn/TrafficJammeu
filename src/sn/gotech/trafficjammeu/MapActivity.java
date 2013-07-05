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
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapActivity extends Activity implements OnMapLongClickListener, LocationListener, OnMarkerDragListener, OnCameraChangeListener{

	private static final String MAP_VIEW_TYPE_SELECTED = "map_type_selected";
	private static final float MIN_ZOOM_LEVEL_FOR_MARKING = 13.0f;
	private static final int ROUTE_INDEX_FREE = 0;
	private static final int ROUTE_INDEX_NORMAL = 1;
	private static final int ROUTE_INDEX_FULL = 2;
	private static final String ROUTE_FREE_STRING = "Libre";
	private static final String ROUTE_NORMAL_STRING = "Normal";
	private static final String ROUTE_FULL_STRING = "Embouteillé";
	private static final int ROUTE_FREE_COLOR = Color.GREEN;
	private static final int ROUTE_NORMAL_COLOR = Color.YELLOW;
	private static final int ROUTE_FULL_COLOR = Color.RED;
	private GoogleMap map;
	private UiSettings mapSettings;
	private ArrayList<Marker> markers;
	private SessionManager session;
	
	private ArrayList<Polyline> polylines;
	private int alertType;
	private LatLng myPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment); 
        session = new SessionManager(getApplicationContext());
        
        if(!session.hasUID()){
        	registerUser();
        } else {
        	manageTuto();
            configureMap();
            manageLocation();
        }
    }
    
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(MAP_VIEW_TYPE_SELECTED)) {
			map.setMapType(savedInstanceState.getInt(MAP_VIEW_TYPE_SELECTED));
//			map.animateCamera(CameraUpdateFactory.)
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
		//actionBar.setDisplayHomeAsUpEnabled(true);
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
		polylines = new ArrayList<Polyline>();
		
		boolean mReturn = false;
		if (map != null) {
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			mapSettings = map.getUiSettings();
			mapSettings.setCompassEnabled(true);
			mapSettings.setTiltGesturesEnabled(true);
			mapSettings.setRotateGesturesEnabled(true);
			mapSettings.setZoomControlsEnabled(true);
			map.setMyLocationEnabled(true);
			if(myPos != null){
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, session.getZoomSize()));
			} else {
				LatLng latLng = new LatLng(Double.parseDouble(session.getAnimeToLat()), Double.parseDouble(session.getAnimeToLng()));
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, session.getZoomSize()));
			}
			
	        map.setOnMapLongClickListener(this);
	        map.setOnMarkerDragListener(this);
	        map.setOnCameraChangeListener(this);
	        
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
 
        } else { // Google Play Services are available
 
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            Criteria criteria = new Criteria();
 
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
				myPos = new LatLng(location.getLatitude(), location.getLongitude());
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
	
	public void registerUser(){
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    	View view = inflater.inflate(R.layout.registration_layout, null);
    	final LinearLayout ll = (LinearLayout) findViewById(R.id.mainLayout);
    	ll.addView(view);
    	ll.setVisibility(View.VISIBLE);
    	Button saveButton = (Button) view.findViewById(R.id.save);
    	
    	final EditText username = (EditText) view.findViewById(R.id.username);
    	final EditText name = (EditText) view.findViewById(R.id.name);
    	final EditText email = (EditText) view.findViewById(R.id.email);
    	
    	saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String usernameString = username.getText().toString();
				String nameString = name.getText().toString();
				String emailString = email.getText().toString();
				
				if(!usernameString.isEmpty() && !nameString.isEmpty() && !emailString.isEmpty() && isEmailValid(emailString)){
					ll.setVisibility(View.GONE);
					session.createSession(
							usernameString, 
							nameString, 
							emailString
							);
					Intent intent = new Intent(getApplicationContext(), MapActivity.class);
					finish();
					startActivity(intent);
				} else {
					Toast.makeText(MapActivity.this, "Veuillez bien remplir tous les champs !", Toast.LENGTH_LONG).show();
				}
			}
		});
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
		if(map.getCameraPosition().zoom < MIN_ZOOM_LEVEL_FOR_MARKING){
			Toast toast = getCustomToast("Zoomez encore " + Math.round(MIN_ZOOM_LEVEL_FOR_MARKING - map.getCameraPosition().zoom) + " fois pour pouvoir marquer des routes");
			toast.show();
		} else {

			AlertDialog.Builder buildInfosMarker = new AlertDialog.Builder(MapActivity.this);
			buildInfosMarker.setTitle("Infos sur ce point");
			buildInfosMarker.setMessage("ajouter des infos sur ce point");
			final EditText inputInfos = new EditText(this);
			buildInfosMarker.setView(inputInfos);
			buildInfosMarker.create().show();
			final LatLng point = position;
<<<<<<< HEAD

			Marker marker = map.addMarker((new MarkerOptions()).position(point).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
=======
			Marker marker = map.addMarker(createMarkerOptions("test", "snippet", point, true));
>>>>>>> 06299051038e8921cb801d40fa31111f4adb5d4c
			markers.add(marker);
			
			if (isOdd(markers.size())) {
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);

				dialog.setTitle("Marquer ce trajet ?");
				CharSequence[] csitems = new CharSequence[3];
				csitems[ROUTE_INDEX_FREE] = ROUTE_FREE_STRING;
				csitems[ROUTE_INDEX_NORMAL] = ROUTE_NORMAL_STRING;
				csitems[ROUTE_INDEX_FULL] = ROUTE_FULL_STRING;
				alertType = ROUTE_INDEX_FREE;
				dialog.setSingleChoiceItems(csitems, ROUTE_INDEX_FREE, new OnClickListener() {

					public void onClick(DialogInterface arg0, int pos) {
						// TODO Auto-generated method stub
						alertType = pos;
						Log.i("SELECTED", String.valueOf(alertType));

					}
				});

				dialog.setPositiveButton(android.R.string.ok,
						new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								drawBetween2LastPoints(getAlertColor(alertType), "title", "desc");
							}
						});
				dialog.setNegativeButton(android.R.string.cancel,
						new OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								// on enleve le dernier marker
								int removeIndex = markers.size() - 1;
								Marker removeMarker = markers.get(removeIndex); // get the marker
								removeMarker.remove(); // remove the marker
								markers.remove(removeIndex); // remove the index
							}
						});
				dialog.create().show();
			}
		}
	}
	
	public int getAlertColor(int index) {
		switch (index) {
		case ROUTE_INDEX_FREE:
			return ROUTE_FREE_COLOR;
		case ROUTE_INDEX_NORMAL:
			return ROUTE_NORMAL_COLOR;
		case ROUTE_INDEX_FULL:
			return ROUTE_FULL_COLOR;

		default:
			return ROUTE_FREE_COLOR;
		}
	}
	
	public void drawBetween2LastPoints(int color, String title, String snippet){
		
		Marker aMarker = markers.get(markers.size() - 2);
		Marker bMarker = markers.get(markers.size() - 1);
		
		aMarker.setTitle("A: " + title);
		aMarker.setSnippet(snippet);
		aMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
		bMarker.setTitle("B: " + title);
		bMarker.setSnippet(snippet);
		
		switch (color) {
		case Color.RED:
			aMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			bMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			break;
		case Color.GREEN:
			aMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			bMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			break;
		case Color.YELLOW:
			aMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			bMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			break;

		default:
			break;
		}
		
		LatLng origin = aMarker.getPosition();
		LatLng dest = bMarker.getPosition();
		
		String url = getDirectionsUrl(origin, dest);
		DownloadTask downloadTask = new DownloadTask(color);
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

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
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
				
		private int color;

		public DownloadTask(int color) {
			// TODO Auto-generated constructor stub
			this.color = color;
		}
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
			
			ParserTask parserTask = new ParserTask(color);
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	private int color;

		public ParserTask(int color) {
			// TODO Auto-generated constructor stub
    		this.color = color;
		}
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
					lineOptions.color(color);
				}
				
				// Drawing polyline in the Google Map for the i-th route
				if(lineOptions != null){
					Polyline polyline = map.addPolyline(lineOptions);
					polylines.add(polyline);
				}
			}
		}
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
 
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(session.getZoomSize()));
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
		
		deleteMarker.setVisibility(View.GONE);
		
		int index = markers.indexOf(marker);
		if(isOdd(index)){ // mean that its a even number (1, 3, 4, ...)
//			polylines.get(index / 2); // if markers size is odd, thus there is "markers.size / 2" polylines
		} else {
			drawBetween2LastPoints(getAlertColor(alertType), marker.getTitle(), marker.getSnippet());
		}
	} 
	
	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		findViewById(R.id.deleteMarker).setVisibility(View.VISIBLE);
		
		int index = markers.indexOf(marker);
				
//		Toast.makeText(this, "is " + index + " odd "+isOdd(index), 5000).show();
//		// if marker has odd index in the array, that means that marker number in the map is even
		if(isOdd(index)){ // mean that its a even number (1, 3, 4, ...)
			if(polylines.size() > (index+1)/2){
				polylines.get((index+1)/2).remove();
			}
		} else {
			if(polylines.size() > index/2){
				polylines.get(index / 2).remove();
			}
		}
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		// TODO Auto-generated method stub
		session.setAnimeToLat(""+position.target.latitude);
		session.setAnimeToLng(""+position.target.longitude);
	}   
		
	public boolean isEmailValid(CharSequence email) {
	   return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
}
