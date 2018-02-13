package in.gitbox.uber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends AppCompatActivity {

    private ListView requestListView;
    private ArrayList<String> requestList = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private ArrayList<Double> requestLatitudes = new ArrayList<>();
    private ArrayList<Double> requestLongitudes = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        requestListView = findViewById(R.id.requests_list);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final double latitude = LocationResultHelper.getLatitude(prefs);
        final double longitude = LocationResultHelper.getLongitude(prefs);
        if (latitude != 0 && longitude != 0){
            updateRequest(latitude, longitude);
            ParseUser.getCurrentUser().put("geo_points", new ParseGeoPoint(latitude, longitude));
            ParseUser.getCurrentUser().saveInBackground();
        }
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, requestList);
        requestListView.setAdapter(arrayAdapter);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (requestLatitudes.size() > i && requestLongitudes.size() > i){
                    Intent intent = new Intent(DriverActivity.this, DriverLocationActivity.class);
                    intent.putExtra("riderLatitude", requestLatitudes.get(i));
                    intent.putExtra("riderLongitude", requestLongitudes.get(i));
                    intent.putExtra("driverLatitude", latitude);
                    intent.putExtra("driverLongitude", longitude);
                    intent.putExtra("username", usernames.get(i));
                    startActivity(intent);
                }
            }
        });
    }

    private void updateRequest(double latitude, double longitude){
        requestList.add("Getting nearby Requests...");
        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
        final ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latitude, longitude);
        query.whereNear("geo_points", parseGeoPoint);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    requestList.clear();
                    requestLongitudes.clear();
                    requestLatitudes.clear();
                    if (objects.size() > 0){

                        for (ParseObject parseObject : objects){
                            ParseGeoPoint requestLocation = (ParseGeoPoint) parseObject.get("geo_points");
                            if (requestLocation != null){

                                Double distanceInKilometers = parseGeoPoint.distanceInKilometersTo(requestLocation);
                                Double distanceOneDecimal = (double) Math.round(distanceInKilometers * 10) / 10;
                                requestList.add(distanceOneDecimal.toString() + " Kms away");
                                requestLatitudes.add(requestLocation.getLatitude());
                                requestLongitudes.add(requestLocation.getLongitude());
                                usernames.add(parseObject.get("username").toString());
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        requestList.add("No Nearby Requests Found!");
                    }
                }
            }
        });

    }
}
