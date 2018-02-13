package in.gitbox.uber;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by sagar on 10-02-2018.
 */

class LocationResultHelper {

    final static String KEY_LOCATION_UPDATES_RESULT = "location-updates-result";

    final private static String PRIMARY_CHANNEL = "default";

    final static String KEY_LOCATION_CO_ORDINATED = "location-co-ordinates";


    private Context mContext;
    private List<Location> mLocations;
    private NotificationManager mNotificationManager;

    LocationResultHelper(Context context, List<Location> locations) {
        mContext = context;
        mLocations = locations;
    }

    /**
     * Returns the title for reporting about a list of {@link Location} objects.
     */
    private String getLocationResultTitle() {
        String numLocationsReported = mContext.getResources().getQuantityString(
                R.plurals.num_locations_reported, mLocations.size(), mLocations.size());
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
    }


    private String getLocationResultText() {
        if (mLocations.isEmpty()) {
            return mContext.getString(R.string.unknown_location);
        }
        StringBuilder sb = new StringBuilder();
        for (Location location : mLocations) {
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    private Double getLatitude(){
        if (mLocations.isEmpty()){
            return 22.572646;
        }
        double latitude = 22.572646;
        for (Location location : mLocations){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    private Double getLongitude(){
        if (mLocations.isEmpty()){
            return 88.363895;
        }
        double longitude = 88.363895;
        for (Location location : mLocations){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * Saves location result as a string to {@link android.content.SharedPreferences}.
     */
    void saveResults() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle() + "\n" +
                        getLocationResultText())
                .apply();
    }

    void saveCoordinates(){
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putLong("Latitude", Double.doubleToLongBits(getLatitude()))
                .putLong("Longitude", Double.doubleToLongBits(getLongitude()))
                .apply();
    }

    /**
     * Fetches location results from {@link android.content.SharedPreferences}.
     */
    static String getSavedLocationResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "");
    }

    static double getLatitude(final SharedPreferences prefs) {
        if ( !prefs.contains("Latitude"))
            return 22.572646;
        return Double.longBitsToDouble(prefs.getLong("Latitude", 0));
    }

    static double getLongitude(final SharedPreferences prefs) {
        if ( !prefs.contains("Longitude"))
            return 88.363895;
        return Double.longBitsToDouble(prefs.getLong("Longitude", 0));
    }
    /**
     * Get the notification mNotificationManager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    /**
     * Displays a notification with the location results.
     */
    void showNotification() {
        Intent notificationIntent = new Intent(mContext, MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(mContext)
                .setContentTitle(getLocationResultTitle())
                .setContentText(getLocationResultText())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent);

        getNotificationManager().notify(0, notificationBuilder.build());
    }
}
