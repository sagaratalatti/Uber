package in.gitbox.uber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public void redirectActivity(){
        if(ParseUser.getCurrentUser().get("RiderOrDriver").equals("Rider")){
            Intent intent = new Intent(MainActivity.this, RiderActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, DriverActivity.class);
            startActivity(intent);
        }
    }

    public void getStarted(View view) {
        view.getId();
        String user = "Rider";
        if (userTypeSwitch.isChecked()) {
            user = "Driver";
        }
        ParseUser.getCurrentUser().put("RiderOrDriver", user);
        ParseUser.getCurrentUser().saveInBackground();
        redirectActivity();
    }

    private Switch userTypeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userTypeSwitch = findViewById(R.id.userTypeSwitch);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        getSupportActionBar().hide();
        if (ParseUser.getCurrentUser() == null){
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                }
            });
        } else {
            if (ParseUser.getCurrentUser().get("RiderOrDriver") != null){
                Log.d(TAG, ParseUser.getCurrentUser().get("RiderOrDriver").toString());
                redirectActivity();
            }
        }
    }
}
