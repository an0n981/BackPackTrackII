package eu.faircode.backpacktrack2;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GoogleElevationApi {
    private static final String TAG = "BPT2.GoogleElevation";
    public static final int cTimeOutMs = 30 * 1000;

    public static boolean getElevation(Location location, boolean waypoint, Context context) {
        try {
            // Check if enabled
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (waypoint) {
                if (!prefs.getBoolean(ActivitySettings.PREF_ALTITUDE_WAYPOINT, ActivitySettings.DEFAULT_ALTITUDE_WAYPOINT))
                    return false;
            } else if (!prefs.getBoolean(ActivitySettings.PREF_ALTITUDE_TRACKPOINT, ActivitySettings.DEFAULT_ALTITUDE_TRACKPOINT))
                return false;

            // Check if connectivity
            if (!ActivitySettings.isConnected(context))
                return false;

            // https://developers.google.com/maps/documentation/elevation/
            URL url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" +
                    String.valueOf(location.getLatitude()) + "," +
                    String.valueOf(location.getLongitude()));
            Log.d(TAG, "url=" + url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(cTimeOutMs);
            urlConnection.setReadTimeout(cTimeOutMs);
            urlConnection.setRequestProperty("Accept", "application/json");

            // Set request type
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setDoInput(true);

            try {
                // Check for errors
                int code = urlConnection.getResponseCode();
                if (code != HttpsURLConnection.HTTP_OK)
                    throw new IOException("HTTP error " + urlConnection.getResponseCode());

                // Get response
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    json.append(line);
                Log.d(TAG, json.toString());

                // Decode result
                JSONObject jroot = new JSONObject(json.toString());
                String status = jroot.getString("status");
                if ("OK".equals(status)) {
                    JSONArray results = jroot.getJSONArray("results");
                    if (results.length() > 0) {
                        double elevation = results.getJSONObject(0).getDouble("elevation");
                        location.setAltitude(elevation);
                        Log.w(TAG, "Elevation " + location);
                        return true;
                    } else
                        throw new IOException("JSON no results");
                } else
                    throw new IOException("JSON status " + status);
            } finally {
                urlConnection.disconnect();
            }
        } catch (Throwable ex) {
            Log.w(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            return false;
        }
    }
}