package cl.teamcore;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.res.Resources;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Menu;
import android.view.View;

import android.util.Log;

public class MainActivity extends ListActivity {

	private ProgressDialog pDialog;
	 
    // URL to get JSON
    private static String url;
    private static String unavailableAddress;
    private static String loadingText;
    
    // JSON Node names
    private static final String TAG_VENUES = "venues";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_LOCATION_ADDRESS = "address";
    private static final String TAG_LOCATION_CITY = "city";
    private static final String TAG_LOCATION_STATE = "state";
    private static final String TAG_LOCATION_COUNTRY = "country";
    private static final String TAG_LOCATION_LATITUDE = "lat";
    private static final String TAG_LOCATION_LONGITUDE = "lng";
    
    // Contacts JSONArray
    JSONArray venues = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> venuesList;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources appResources = this.getResources();
        this.setConstantsFromAppResources(appResources);
        setContentView(R.layout.activity_main);
 
        //A list of HashMap with Venues info
        venuesList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
 
        // Listview on item click listener
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// Getting values from selected ListItem
				HashMap<String, String> singleVenue = venuesList.get(position);
				String name 	= (String)singleVenue.get(TAG_NAME);
				String address 	= (String)singleVenue.get(TAG_LOCATION_ADDRESS);
				String state  	= (String)singleVenue.get(TAG_LOCATION_CITY);
				String latitude = (String)singleVenue.get(TAG_LOCATION_LATITUDE);
				String longitude= (String)singleVenue.get(TAG_LOCATION_LONGITUDE);

				// Starting single contact activity
				Intent intent = new Intent(getApplicationContext(), VenueActivity.class);
				intent.putExtra(TAG_NAME, name);
				intent.putExtra(TAG_LOCATION_ADDRESS, address);
				intent.putExtra(TAG_LOCATION_STATE, state);
				intent.putExtra(TAG_LOCATION_LONGITUDE, longitude);
				intent.putExtra(TAG_LOCATION_LATITUDE, latitude);
				
				startActivity(intent);
			}
        });
 
        // Calling async task to get json
        new GetContacts().execute();
    }

    //Set values from strings.xml
    private void setConstantsFromAppResources(Resources appResources){
    	url = appResources.getString(R.string.fourSquareURL);
    	unavailableAddress = appResources.getString(R.string.unavailableAddressText);
    	loadingText = appResources.getString(R.string.loadingText);
    }
    
    
    

	//AsyncTask class to get a json asynchronously by making an HTTP request
	private class GetContacts extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(loadingText);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHelper sh = new ServiceHelper();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHelper.GET);
			
			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					jsonObj = jsonObj.getJSONObject("response");
					venues = jsonObj.getJSONArray(TAG_VENUES);
					
					// Looping through venues
					for (int i = 0; i < venues.length(); i++) {
						JSONObject venue = venues.getJSONObject(i);
						
						String id = venue.getString(TAG_ID);
						String name = venue.getString(TAG_NAME);

						// location is a JSON Object in venues
						JSONObject location = venue.getJSONObject(TAG_LOCATION);
						String locationAddress;
						if(location.has(TAG_LOCATION_ADDRESS)){
							locationAddress = location.getString(TAG_LOCATION_ADDRESS);
						}else{
							locationAddress = unavailableAddress;
						}
						String locationState = location.getString(TAG_LOCATION_CITY);
						String locationCity = location.getString(TAG_LOCATION_STATE);
						String locationCountry = location.getString(TAG_LOCATION_COUNTRY);
						String locationLong = location.getString(TAG_LOCATION_LONGITUDE);
						String locationLat = location.getString(TAG_LOCATION_LATITUDE);
				
						// Build a hashmap for every single venue
						HashMap<String, String> venueHash = new HashMap<String, String>();
						venueHash.put(TAG_ID, id);
						venueHash.put(TAG_NAME, name);
						venueHash.put(TAG_LOCATION_ADDRESS, locationAddress);
						venueHash.put(TAG_LOCATION_CITY, locationState + ", " + locationCity + " - " + locationCountry);
						venueHash.put(TAG_LOCATION_LONGITUDE, locationLong);
						venueHash.put(TAG_LOCATION_LATITUDE, locationLat);

						// Adding all venues to venues list
						venuesList.add(venueHash);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHelper", "Couldn't get any data from the given url");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */
			ListAdapter adapter = new SimpleAdapter(
					MainActivity.this, venuesList,
					R.layout.list_row, new String[] { TAG_NAME, TAG_LOCATION_ADDRESS,
							TAG_LOCATION_CITY }, new int[] { R.id.name,
							R.id.address, R.id.state});
			
			setListAdapter(adapter);
		}
 
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}

}
