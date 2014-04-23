package cl.teamcore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class VenueActivity extends Activity {
	
	// JSON constants node keys
	private static final String TAG_NAME = "name";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_STATE = "state";
	private static final String TAG_LATITUDE = "lat";
	private static final String TAG_LONGITUDE = "lng";
	private GoogleMap googleMap;
	private double latitude;
	private double longitude;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        
        // Getting intent data
        Intent in = getIntent();
        
        // Get JSON values from previous intent
        String name = in.getStringExtra(TAG_NAME);
        String email = in.getStringExtra(TAG_ADDRESS);
        String mobile = in.getStringExtra(TAG_STATE);
        this.latitude =  Double.parseDouble(in.getStringExtra(TAG_LATITUDE));
        this.longitude = Double.parseDouble(in.getStringExtra(TAG_LONGITUDE));
        
        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.venue_name);
        TextView lblAddress = (TextView) findViewById(R.id.venue_address);
        TextView lblState = (TextView) findViewById(R.id.venue_state);
        
        lblName.setText(name);
        lblAddress.setText(email);
        lblState.setText(mobile);
        
        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
	/**
     * Load map. If map is not created, then create it.
     * */
    private void initilizeMap() {
        if (googleMap == null) {
        	MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        	//Create a marker with the given latitude and longitude
        	MarkerOptions marker = new MarkerOptions().
        			position(new LatLng(this.latitude, this.longitude)).
        			title(TAG_NAME);
        	//Make zoom effect to center marker
        	CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(this.latitude, this.longitude)).zoom(10).build();
        	googleMap = mapFragment.getMap();
            googleMap.addMarker(marker);
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // Check if map is created successfully or not. If not, show a message to the user.
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "We sorry. It's not posible to load a map right now.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

}
