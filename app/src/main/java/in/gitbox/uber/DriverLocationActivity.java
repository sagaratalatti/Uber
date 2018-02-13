package in.gitbox.uber;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        intent = getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button acceptButton = findViewById(R.id.accept_request);
        RelativeLayout mapLayout = findViewById(R.id.map_layout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LatLng driverLocation = new LatLng(intent.getDoubleExtra("driverLatitude", 0),
                        intent.getDoubleExtra("driverLongitude", 0));
                LatLng riderLocation = new LatLng(intent.getDoubleExtra("riderLatitude", 0),
                        intent.getDoubleExtra("riderLongitude", 0));
                ArrayList<Marker> markers = new ArrayList<>();
                markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Driver's Location")));
                markers.add(mMap.addMarker(new MarkerOptions().position(riderLocation).title("Rider's Location")));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers){
                    builder.include(marker.getPosition());
                }

                LatLngBounds bounds = builder.build();
                int padding = 60;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cameraUpdate);
            }
        });
        acceptButton.setOnClickListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onClick(View view) {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Request");
        parseQuery.whereEqualTo("username", intent.getStringExtra("username"));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject parseObject : objects){
                            parseObject.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Intent directionsIntent = new Intent(Intent.ACTION_VIEW,
                                         Uri.parse("http://maps.google.com/maps?saddr="
                                         + intent.getDoubleExtra("driverLatitude", 0)
                                         +","
                                         + intent.getDoubleExtra("driverLongitude", 0)
                                         + "&daddr="
                                         + intent.getDoubleExtra("riderLatitude", 0)
                                                 + ","
                                         + intent.getDoubleExtra("riderLongitude", 0)));
                                    startActivity(directionsIntent);
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
