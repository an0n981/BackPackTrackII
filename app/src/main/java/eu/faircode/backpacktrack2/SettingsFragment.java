package eu.faircode.backpacktrack2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "BPT2.Settings";

    // Preference names
    public static final String PREF_EDIT = "pref_edit";
    public static final String PREF_SHARE_GPX = "pref_share_gpx";
    public static final String PREF_SHARE_KML = "pref_share_kml";
    public static final String PREF_UPLOAD_GPX = "pref_upload_gpx";
    public static final String PREF_LOCATION_HISTORY = "pref_location_history";
    public static final String PREF_ACTIVITY_HISTORY = "pref_activity_history";
    public static final String PREF_STEP_HISTORY = "pref_step_history";
    public static final String PREF_SETTINGS = "pref_settings";

    public static final String PREF_ENABLED = "pref_enabled";
    public static final String PREF_USE_NETWORK = "pref_use_network";
    public static final String PREF_USE_GPS = "pref_use_gps";
    public static final String PREF_INTERVAL = "pref_interval";
    public static final String PREF_ALTITUDE = "pref_altitude";
    public static final String PREF_TP_ACCURACY = "pref_accuracy";
    public static final String PREF_WP_ACCURACY = "pref_wp_accuracy";
    public static final String PREF_TIMEOUT = "pref_timeout";
    public static final String PREF_CHECK_TIME = "pref_check_time";
    public static final String PREF_CHECK_SAT = "pref_check_sat";
    public static final String PREF_INACCURATE = "pref_inaccurate";
    public static final String PREF_NEARBY = "pref_nearby";
    public static final String PREF_MINTIME = "pref_mintime";
    public static final String PREF_MINDIST = "pref_mindist";

    public static final String PREF_PASSIVE_ENABLED = "pref_passive_enabled";
    public static final String PREF_PASSIVE_BEARING = "pref_passive_bearing";
    public static final String PREF_PASSIVE_ALTITUDE = "pref_passive_altitude";
    public static final String PREF_PASSIVE_INACCURATE = "pref_passive_inaccurate";
    public static final String PREF_PASSIVE_NEARBY = "pref_passive_nearby";
    public static final String PREF_PASSIVE_MINTIME = "pref_passive_mintime";
    public static final String PREF_PASSIVE_MINDIST = "pref_passive_mindist";
    public static final String PREF_PASSIVE_DISPLACEMENT = "pref_passive_displacement";

    public static final String PREF_CORRECTION_ENABLED = "pref_correction_enabled";
    public static final String PREF_ALTITUDE_WAYPOINT = "pref_altitude_waypoint";
    public static final String PREF_ALTITUDE_TRACKPOINT = "pref_altitude_trackpoint";
    public static final String PREF_ALTITUDE_AVG = "pref_altitude_avg";

    public static final String PREF_RECOGNITION_ENABLED = "pref_recognition_enabled";
    public static final String PREF_RECOGNITION_INTERVAL_STILL = "pref_recognition_interval_still";
    public static final String PREF_RECOGNITION_INTERVAL_MOVING = "pref_recognition_interval_moving";
    public static final String PREF_RECOGNITION_CONFIDENCE = "pref_recognition_confidence";
    public static final String PREF_RECOGNITION_TILTING = "pref_recognition_filter_tilting";
    public static final String PREF_RECOGNITION_KNOWN = "pref_recognition_replace_unknown";
    public static final String PREF_RECOGNITION_UNKNOWN = "pref_recognition_filter_unknown";
    public static final String PREF_RECOGNITION_STEPS = "pref_recognition_steps";
    public static final String PREF_RECOGNITION_UNKNOWN_STEPS = "pref_recognition_unknown_steps";
    public static final String PREF_RECOGNITION_SIGNIFICANT = "pref_recognition_significant";

    public static final String PREF_RECOGNITION_HISTORY = "pref_recognition_history";

    public static final String PREF_STEP_DELTA = "pref_step_delta";
    public static final String PREF_STEP_SIZE = "pref_step_size";
    public static final String PREF_WEIGHT = "pref_weight";

    public static final String PREF_BLOGURL = "pref_blogurl";
    public static final String PREF_BLOGID = "pref_blogid";
    public static final String PREF_BLOGUSER = "pref_bloguser";
    public static final String PREF_BLOGPWD = "pref_blogpwd";

    public static final String PREF_VERSION = "pref_version";
    public static final String PREF_SUPPORT = "pref_support";
    public static final String PREF_DEBUG = "pref_debug";

    // Preference defaults
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_USE_NETWORK = true;
    public static final boolean DEFAULT_USE_GPS = true;
    public static final String DEFAULT_INTERVAL = "180"; // seconds
    public static final boolean DEFAULT_ALTITUDE = true;
    public static final String DEFAULT_TP_ACCURACY = "20"; // meters
    public static final String DEFAULT_WP_ACCURACY = "10"; // meters
    public static final String DEFAULT_TIMEOUT = "60"; // seconds
    public static final String DEFAULT_CHECK_TIME = "30"; // seconds
    public static final String DEFAULT_CHECK_SAT = "1";
    public static final String DEFAULT_INACCURATE = "100"; // meters
    public static final String DEFAULT_NEARBY = "100"; // meters
    public static final String DEFAULT_MINTIME = "1"; // seconds
    public static final String DEFAULT_MINDIST = "0"; // meters

    public static final boolean DEFAULT_PASSIVE_ENABLED = true;
    public static final String DEFAULT_PASSIVE_BEARING = "30"; // degrees
    public static final String DEFAULT_PASSIVE_ALTITUDE = "20"; // meters
    public static final String DEFAULT_PASSIVE_INACCURATE = "10"; // meters
    public static final String DEFAULT_PASSIVE_NEARBY = "20"; // meters
    public static final String DEFAULT_PASSIVE_MINTIME = "1"; // seconds
    public static final String DEFAULT_PASSIVE_MINDIST = "0"; // meters
    public static final boolean DEFAULT_PASSIVE_DISPLACEMENT = true;

    public static final boolean DEFAULT_CORRECTION_ENABLED = true;
    public static final boolean DEFAULT_ALTITUDE_WAYPOINT = true;
    public static final boolean DEFAULT_ALTITUDE_TRACKPOINT = false;
    public static final String DEFAULT_ALTITUDE_AVG = "5"; // samples

    public static final boolean DEFAULT_RECOGNITION_ENABLED = true;
    public static final String DEFAULT_RECOGNITION_INTERVAL_STILL = "60"; // seconds
    public static final String DEFAULT_RECOGNITION_INTERVAL_MOVING = "60"; // seconds
    public static final String DEFAULT_RECOGNITION_CONFIDENCE = "50"; // percentage
    public static final boolean DEFAULT_RECOGNITION_TILTING = true;
    public static final boolean DEFAULT_RECOGNITION_UNKNOWN = false;
    public static final boolean DEFAULT_RECOGNITION_KNOWN = true;
    public static final boolean DEFAULT_RECOGNITION_STEPS = true;
    public static final boolean DEFAULT_RECOGNITION_UNKNOWN_STEPS = true;
    public static final boolean DEFAULT_RECOGNITION_HISTORY = false;
    public static final boolean DEFAULT_RECOGNITION_SIGNIFICANT = false;

    public static final String DEFAULT_STEP_DELTA = "10"; // steps
    public static final String DEFAULT_STEP_SIZE = "75"; // centimeters
    public static final String DEFAULT_WEIGHT = "75"; // kilograms

    // Transient values
    public static final String PREF_FIRST = "pref_first";
    public static final String PREF_STATE = "pref_state";
    public static final String PREF_LOCATION_TYPE = "pref_location_type";
    public static final String PREF_BEST_LOCATION = "pref_best_location";
    public static final String PREF_SATS_FIXED = "pref_sats_fixed";
    public static final String PREF_SATS_VISIBLE = "pref_sats_visible";

    // Remember last values
    public static final String PREF_LAST_ACTIVITY = "pref_last_activity";
    public static final String PREF_LAST_CONFIDENCE = "pref_last_confidence";
    public static final String PREF_LAST_ACTIVITY_TIME = "pref_last_activity_time";
    public static final String PREF_LAST_LOCATION = "pref_last_location";
    public static final String PREF_LAST_STEP_COUNT = "pref_last_step";
    public static final String PREF_LAST_SHARE_GPX = "pref_last_share_gpx";
    public static final String PREF_LAST_SHARE_KML = "pref_last_share_kml";
    public static final String PREF_LAST_UPLOAD_GPX = "pref_last_gpx_upload";

    public static final String PREF_LAST_TRACK = "pref_last_track";
    public static final String PREF_LAST_EXTENSIONS = "pref_last_extensions";
    public static final String PREF_LAST_FROM = "pref_last_from";
    public static final String PREF_LAST_TO = "pref_last_to";

    private static final int GEOCODER_RESULTS = 5;
    private static final long DAY_MS = 24L * 3600L * 1000L;
    private static final int DAYS_HISTORY = 7;

    private DatabaseHelper db = null;
    private boolean elevationBusy = false;

    private BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(TAG, "Connectivity changed");
            findPreference(PREF_UPLOAD_GPX).setEnabled(blogConfigured() && storageMounted() && isConnected(SettingsFragment.this.getActivity()));
        }
    };

    private BroadcastReceiver mExternalStorageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(TAG, "External storage changed");
            findPreference(PREF_SHARE_GPX).setEnabled(storageMounted());
            findPreference(PREF_SHARE_KML).setEnabled(storageMounted());
            findPreference(PREF_UPLOAD_GPX).setEnabled(blogConfigured() && storageMounted() && isConnected(SettingsFragment.this.getActivity()));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        db = new DatabaseHelper(getActivity());

        // Shared geo point
        Uri data = getActivity().getIntent().getData();
        if (data != null && "geo".equals(data.getScheme())) {
            Intent geopointIntent = new Intent(getActivity(), LocationService.class);
            geopointIntent.setAction(LocationService.ACTION_GEOPOINT);
            geopointIntent.putExtra(LocationService.EXTRA_GEOURI, data);
            getActivity().startService(geopointIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (db != null)
            db.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        getActivity().registerReceiver(mConnectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        IntentFilter storageFilter = new IntentFilter();
        storageFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        storageFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        getActivity().registerReceiver(mExternalStorageReceiver, storageFilter);

        // First run
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        if (prefs.getBoolean(PREF_FIRST, true)) {
            prefs.edit().putBoolean(PREF_FIRST, false).apply();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (getActivity().getApplicationContext()) {
                        firstRun(getActivity());
                    }
                }
            }).start();
        }

        // Get preferences
        Preference pref_edit = findPreference(PREF_EDIT);
        Preference pref_share_gpx = findPreference(PREF_SHARE_GPX);
        Preference pref_share_kml = findPreference(PREF_SHARE_KML);
        Preference pref_upload_gpx = findPreference(PREF_UPLOAD_GPX);
        Preference pref_enabled = findPreference(PREF_ENABLED);
        Preference pref_check = findPreference(PREF_SETTINGS);
        Preference pref_location_history = findPreference(PREF_LOCATION_HISTORY);
        Preference pref_activity_history = findPreference(PREF_ACTIVITY_HISTORY);
        Preference pref_recognize_steps = findPreference(PREF_RECOGNITION_STEPS);
        Preference pref_significant = findPreference(PREF_RECOGNITION_SIGNIFICANT);
        Preference pref_step_history = findPreference(PREF_STEP_HISTORY);
        Preference pref_step_update = findPreference(PREF_STEP_DELTA);
        Preference pref_step_size = findPreference(PREF_STEP_SIZE);
        Preference pref_weight = findPreference(PREF_WEIGHT);
        Preference pref_version = findPreference(PREF_VERSION);

        // Set titles/summaries
        updateTitle(prefs, PREF_SHARE_GPX);
        updateTitle(prefs, PREF_SHARE_KML);
        updateTitle(prefs, PREF_UPLOAD_GPX);

        updateTitle(prefs, PREF_INTERVAL);
        updateTitle(prefs, PREF_ALTITUDE);
        updateTitle(prefs, PREF_TP_ACCURACY);
        updateTitle(prefs, PREF_WP_ACCURACY);
        updateTitle(prefs, PREF_TIMEOUT);
        updateTitle(prefs, PREF_CHECK_TIME);
        updateTitle(prefs, PREF_CHECK_SAT);
        updateTitle(prefs, PREF_INACCURATE);
        updateTitle(prefs, PREF_NEARBY);
        updateTitle(prefs, PREF_MINTIME);
        updateTitle(prefs, PREF_MINDIST);

        updateTitle(prefs, PREF_PASSIVE_BEARING);
        updateTitle(prefs, PREF_PASSIVE_ALTITUDE);
        updateTitle(prefs, PREF_PASSIVE_INACCURATE);
        updateTitle(prefs, PREF_PASSIVE_NEARBY);
        updateTitle(prefs, PREF_PASSIVE_MINTIME);
        updateTitle(prefs, PREF_PASSIVE_MINDIST);

        updateTitle(prefs, PREF_ALTITUDE_AVG);

        updateTitle(prefs, PREF_RECOGNITION_INTERVAL_STILL);
        updateTitle(prefs, PREF_RECOGNITION_INTERVAL_MOVING);
        updateTitle(prefs, PREF_RECOGNITION_CONFIDENCE);

        updateTitle(prefs, PREF_STEP_DELTA);
        updateTitle(prefs, PREF_STEP_SIZE);
        updateTitle(prefs, PREF_WEIGHT);

        updateTitle(prefs, PREF_BLOGURL);
        updateTitle(prefs, PREF_BLOGID);
        updateTitle(prefs, PREF_BLOGUSER);
        updateTitle(prefs, PREF_BLOGPWD);

        updateTitle(prefs, PREF_SUPPORT);

        // Handle waypoint_edit waypoints
        pref_edit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                edit_waypoints();
                return true;
            }
        });

        // Handle share GPX
        pref_share_gpx.setEnabled(storageMounted());
        pref_share_gpx.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), LocationService.class);
                intent.setAction(LocationService.ACTION_SHARE_GPX);
                export(intent, R.string.title_share_gpx);
                return true;
            }
        });

        // Handle share KML
        pref_share_kml.setEnabled(storageMounted());
        pref_share_kml.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), LocationService.class);
                intent.setAction(LocationService.ACTION_SHARE_KML);
                export(intent, R.string.title_share_kml);
                return true;
            }
        });

        // Handle upload GPX
        pref_upload_gpx.setEnabled(blogConfigured() && storageMounted() && isConnected(getActivity()));
        pref_upload_gpx.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), LocationService.class);
                intent.setAction(LocationService.ACTION_UPLOAD_GPX);
                export(intent, R.string.title_upload_gpx);
                return true;
            }
        });

        // Show enabled providers
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        pref_enabled.setEnabled(gps || network);
        String providers;
        if (gps || network) {
            providers = getString(R.string.msg_gps, getString(gps ? R.string.msg_yes : R.string.msg_no)) + "\n" +
                    getString(R.string.msg_network, getString(network ? R.string.msg_yes : R.string.msg_no));
        } else
            providers = getString(R.string.msg_noproviders);
        pref_enabled.setSummary(providers);

        // Handle location history
        pref_location_history.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                location_history();
                return true;
            }
        });

        // Handle activity history
        pref_activity_history.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                activity_history();
                return true;
            }
        });

        // Handle step count history
        pref_step_history.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                step_history();
                return true;
            }
        });

        // Handle location settings
        Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        if (getActivity().getPackageManager().queryIntentActivities(locationSettingsIntent, 0).size() > 0)
            pref_check.setIntent(locationSettingsIntent);
        else
            pref_check.setEnabled(false);

        // Check for Play services
        boolean playServices = LocationService.hasPlayServices(getActivity());
        findPreference(PREF_ACTIVITY_HISTORY).setEnabled(playServices);
        findPreference(PREF_RECOGNITION_ENABLED).setEnabled(playServices);
        findPreference(PREF_RECOGNITION_INTERVAL_STILL).setEnabled(playServices);
        findPreference(PREF_RECOGNITION_INTERVAL_MOVING).setEnabled(playServices);
        findPreference(PREF_RECOGNITION_CONFIDENCE).setEnabled(playServices);

        // Check for significant motion detector
        pref_significant.setEnabled(LocationService.hasSignificantMotion(getActivity()));

        // Handle Play store link
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName()));
        if (getActivity().getPackageManager().queryIntentActivities(playStoreIntent, 0).size() > 0)
            pref_version.setIntent(playStoreIntent);

        // Step counting not available
        if (!LocationService.hasStepCounter(getActivity())) {
            pref_recognize_steps.setEnabled(false);
            pref_step_history.setEnabled(false);
            pref_step_size.setEnabled(false);
            pref_step_update.setEnabled(false);
            pref_weight.setEnabled(false);
        }

        // Handle version info
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            pref_version.setSummary(
                    pInfo.versionName + "/" + pInfo.versionCode + "\n" +
                            getString(R.string.msg_geocoder,
                                    getString(Geocoder.isPresent() ? R.string.msg_yes : R.string.msg_no)) + "\n" +
                            getString(R.string.msg_playservices,
                                    getString(LocationService.hasPlayServices(getActivity()) ? R.string.msg_yes : R.string.msg_no)) + "\n" +
                            getString(R.string.msg_stepcounter, getString(LocationService.hasStepCounter(getActivity()) ? R.string.msg_yes : R.string.msg_no)) + "\n" +
                            getString(R.string.msg_significantmotion, getString(LocationService.hasSignificantMotion(getActivity()) ? R.string.msg_yes : R.string.msg_no)));
        } catch (PackageManager.NameNotFoundException ex) {
            pref_version.setSummary(ex.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        getActivity().unregisterReceiver(mConnectivityChangeReceiver);
        getActivity().unregisterReceiver(mExternalStorageReceiver);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (PREF_LAST_SHARE_GPX.equals(key))
            key = PREF_SHARE_GPX;
        else if (PREF_LAST_SHARE_KML.equals(key))
            key = PREF_SHARE_KML;
        else if (PREF_LAST_UPLOAD_GPX.equals(key))
            key = PREF_UPLOAD_GPX;

        Preference pref = findPreference(key);

        // Remove empty string settings
        if (pref instanceof EditTextPreference)
            if ("".equals(prefs.getString(key, null)))
                prefs.edit().remove(key).apply();

        // Follow extern change (Tasker)
        if (PREF_ENABLED.equals(key))
            ((CheckBoxPreference) pref).setChecked(prefs.getBoolean(PREF_ENABLED, DEFAULT_ENABLED));

        // Reset activity
        if (PREF_RECOGNITION_ENABLED.equals(key))
            prefs.edit().remove(PREF_LAST_ACTIVITY).apply();

        // Update blog URL
        if (PREF_BLOGURL.equals(key)) {
            String blogurl = prefs.getString(key, null);
            if (blogurl != null) {
                if (!blogurl.startsWith("http://") && !blogurl.startsWith("https://"))
                    blogurl = "http://" + blogurl;
                if (!blogurl.endsWith("/"))
                    blogurl += "/";
                prefs.edit().putString(key, blogurl).apply();
                ((EditTextPreference) pref).setText(blogurl);
            }
            findPreference(PREF_UPLOAD_GPX).setEnabled(blogurl != null);
        }

        // Update preference titles
        updateTitle(prefs, key);

        // Restart tracking if needed
        if (PREF_ENABLED.equals(key) ||
                PREF_INTERVAL.equals(key) ||
                PREF_TIMEOUT.equals(key) ||
                PREF_CHECK_TIME.equals(key) ||
                PREF_MINTIME.equals(key) ||
                PREF_MINDIST.equals(key) ||
                PREF_PASSIVE_ENABLED.equals(key) ||
                PREF_PASSIVE_MINTIME.equals(key) ||
                PREF_PASSIVE_MINDIST.equals(key) ||
                PREF_RECOGNITION_ENABLED.equals(key) ||
                PREF_RECOGNITION_INTERVAL_STILL.equals(key) ||
                PREF_RECOGNITION_INTERVAL_MOVING.equals(key) ||
                PREF_RECOGNITION_STEPS.equals(key) ||
                PREF_RECOGNITION_SIGNIFICANT.equals(key))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (getActivity().getApplicationContext()) {
                        LocationService.stopTracking(getActivity());
                        LocationService.startTracking(getActivity());
                    }
                }
            }).start();
    }

    // Helper methods

    public static void firstRun(Context context) {
        Log.w(TAG, "First run");

        long time = new Date().getTime();
        new DatabaseHelper(context).updateSteps(time, 0).close();
        StepCountWidget.updateWidgets(context);

        LocationService.stopTracking(context);
        LocationService.startTracking(context);

        Intent alarmIntent = new Intent(context, LocationService.class);
        alarmIntent.setAction(LocationService.ACTION_DAILY);
        PendingIntent pi = PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    private void edit_waypoints() {
        // Get layout
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewEdit = inflater.inflate(R.layout.waypoint_edit, null);

        // Fill list
        final ListView lv = (ListView) viewEdit.findViewById(R.id.lvEdit);
        Cursor cursor = db.getLocations(0, Long.MAX_VALUE, false, true, false);
        final WaypointAdapter adapter = new WaypointAdapter(getActivity(), cursor, db);
        lv.setAdapter(adapter);

        // Handle waypoint_add
        ImageView ivAdd = (ImageView) viewEdit.findViewById(R.id.ivAdd);
        if (Geocoder.isPresent())
            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View viewEdit = inflater.inflate(R.layout.waypoint_add, null);
                    final EditText address = (EditText) viewEdit.findViewById(R.id.etAdd);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle(R.string.title_geocode);
                    alertDialogBuilder.setView(viewEdit);
                    alertDialogBuilder
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = address.getText().toString();
                                    if (!TextUtils.isEmpty(name))
                                        add_waypoint(name, adapter);
                                }
                            });
                    alertDialogBuilder
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        else
            ivAdd.setVisibility(View.GONE);

        // Show layout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.title_edit);
        alertDialogBuilder.setView(viewEdit);
        alertDialogBuilder
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        // Fix keyboard input
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void add_waypoint(final String name, final WaypointAdapter adapter) {
        // Geocode name
        Toast.makeText(getActivity(), getString(R.string.msg_geocoding, name), Toast.LENGTH_LONG).show();

        new AsyncTask<Object, Object, List<Address>>() {
            protected List<Address> doInBackground(Object... params) {
                try {
                    Geocoder geocoder = new Geocoder(getActivity());
                    return geocoder.getFromLocationName(name, GEOCODER_RESULTS);
                } catch (IOException ex) {
                    Log.w(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                    return null;
                }
            }

            protected void onPostExecute(final List<Address> listAddress) {
                // Get address lines
                if (listAddress != null && listAddress.size() > 0) {
                    final List<CharSequence> listAddressLine = new ArrayList<>();
                    for (Address address : listAddress)
                        if (address.hasLatitude() && address.hasLongitude()) {
                            List<String> listLine = new ArrayList<>();
                            for (int l = 0; l < address.getMaxAddressLineIndex(); l++)
                                listLine.add(address.getAddressLine(l));
                            listAddressLine.add(TextUtils.join(", ", listLine));
                        }

                    // Show address selector
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle(getString(R.string.title_geocode));
                    alertDialogBuilder.setItems(listAddressLine.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Build location
                            final String geocodedName = (String) listAddressLine.get(item);
                            final Location location = new Location("geocoder");
                            location.setLatitude(listAddress.get(item).getLatitude());
                            location.setLongitude(listAddress.get(item).getLongitude());
                            location.setTime(System.currentTimeMillis());

                            new AsyncTask<Object, Object, Object>() {
                                protected Object doInBackground(Object... params) {
                                    // Add elevation data
                                    if (!location.hasAltitude()) {
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                        if (prefs.getBoolean(PREF_ALTITUDE_WAYPOINT, DEFAULT_ALTITUDE_WAYPOINT))
                                            GoogleElevationApi.getElevation(location, getActivity());
                                    }

                                    // Persist location
                                    new DatabaseHelper(getActivity()).insertLocation(location, geocodedName, -1, -1, -1).close();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object result) {
                                    Cursor cursor = db.getLocations(0, Long.MAX_VALUE, false, true, false);
                                    adapter.changeCursor(cursor);
                                    Toast.makeText(getActivity(), getString(R.string.msg_added, geocodedName), Toast.LENGTH_LONG).show();
                                }
                            }.execute();
                        }
                    });
                    alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    });
                    alertDialogBuilder.show();
                } else
                    Toast.makeText(getActivity(), getString(R.string.msg_nolocation, name), Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void export(final Intent intent, int resTitle) {
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

        // Get layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.export, null);

        // Reference controls
        final TextView tvTrackName = (TextView) view.findViewById(R.id.tvTrackName);
        final CheckBox cbExtensions = (CheckBox) view.findViewById(R.id.cbExtensions);
        final CheckBox cbDelete = (CheckBox) view.findViewById(R.id.cbDelete);
        Button btnDateFrom = (Button) view.findViewById(R.id.btnDateFrom);
        Button btnTimeFrom = (Button) view.findViewById(R.id.btnTimeFrom);
        Button btnDateTo = (Button) view.findViewById(R.id.btnDateTo);
        Button btnTimeTo = (Button) view.findViewById(R.id.btnTimeTo);
        final TextView tvDateFrom = (TextView) view.findViewById(R.id.tvDateFrom);
        final TextView tvTimeFrom = (TextView) view.findViewById(R.id.tvTimeFrom);
        final TextView tvDateTo = (TextView) view.findViewById(R.id.tvDateTo);
        final TextView tvTimeTo = (TextView) view.findViewById(R.id.tvTimeTo);

        final boolean ampm = android.text.format.DateFormat.is24HourFormat(getActivity());

        // Set last track name/extensions
        tvTrackName.setText(prefs.getString(PREF_LAST_TRACK, LocationService.DEFAULT_TRACK_NAME));
        cbExtensions.setChecked(prefs.getBoolean(PREF_LAST_EXTENSIONS, false));

        // Get default from
        Calendar defaultFrom = Calendar.getInstance();
        defaultFrom.set(Calendar.YEAR, 1970);
        defaultFrom.set(Calendar.MONTH, Calendar.JANUARY);
        defaultFrom.set(Calendar.DAY_OF_MONTH, 1);
        defaultFrom.set(Calendar.HOUR_OF_DAY, 0);
        defaultFrom.set(Calendar.MINUTE, 0);

        // Get default to
        Calendar defaultTo = Calendar.getInstance();
        defaultTo.set(Calendar.YEAR, 2100);
        defaultTo.set(Calendar.MONTH, Calendar.JANUARY);
        defaultTo.set(Calendar.DAY_OF_MONTH, 1);
        defaultTo.set(Calendar.HOUR_OF_DAY, 0);
        defaultTo.set(Calendar.MINUTE, 0);

        // Get range
        final Calendar from = GregorianCalendar.getInstance();
        final Calendar to = GregorianCalendar.getInstance();

        from.setTime(new Date(prefs.getLong(PREF_LAST_FROM, defaultFrom.getTimeInMillis())));
        to.setTime(new Date(prefs.getLong(PREF_LAST_TO, defaultTo.getTimeInMillis())));

        // Show range
        final DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
        final DateFormat timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

        tvDateFrom.setText(dateFormat.format(from.getTime()));
        tvTimeFrom.setText(timeFormat.format(from.getTime()));
        tvDateTo.setText(dateFormat.format(to.getTime()));
        tvTimeTo.setText(timeFormat.format(to.getTime()));

        // Pick date from
        btnDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                from.set(year, month, day);
                                tvDateFrom.setText(dateFormat.format(from.getTime()));
                            }
                        }, from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH));
                    }
                }.show(getFragmentManager(), "datePicker");
            }
        });

        // Pick time from
        btnTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                from.set(Calendar.HOUR_OF_DAY, hour);
                                from.set(Calendar.MINUTE, minute);
                                tvTimeFrom.setText(timeFormat.format(from.getTime()));
                            }
                        }, from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE), ampm);
                    }
                }.show(getFragmentManager(), "timePicker");
            }
        });

        // Pick date to
        btnDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                to.set(year, month, day);
                                tvDateTo.setText(dateFormat.format(to.getTime()));
                            }
                        }, to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH));
                    }
                }.show(getFragmentManager(), "datePicker");
            }
        });

        // Pick time to
        btnTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                to.set(Calendar.HOUR_OF_DAY, hour);
                                to.set(Calendar.MINUTE, minute);
                                tvTimeTo.setText(timeFormat.format(to.getTime()));
                            }
                        }, to.get(Calendar.HOUR_OF_DAY), to.get(Calendar.MINUTE), ampm);
                    }
                }.show(getFragmentManager(), "timePicker");
            }
        });

        // Show layout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(resTitle);
        alertDialogBuilder.setView(view);
        alertDialogBuilder
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!cbDelete.isChecked()) {
                                    prefs.edit().putString(PREF_LAST_TRACK, tvTrackName.getText().toString()).apply();
                                    prefs.edit().putBoolean(PREF_LAST_EXTENSIONS, cbExtensions.isChecked()).apply();
                                    prefs.edit().putLong(PREF_LAST_FROM, from.getTimeInMillis()).apply();
                                    prefs.edit().putLong(PREF_LAST_TO, to.getTimeInMillis()).apply();
                                }
                                intent.putExtra(LocationService.EXTRA_TRACK_NAME, tvTrackName.getText().toString());
                                intent.putExtra(LocationService.EXTRA_WRITE_EXTENSIONS, cbExtensions.isChecked());
                                intent.putExtra(LocationService.EXTRA_DELETE_DATA, cbDelete.isChecked());
                                intent.putExtra(LocationService.EXTRA_TIME_FROM, from.getTimeInMillis());
                                intent.putExtra(LocationService.EXTRA_TIME_TO, to.getTimeInMillis());
                                getActivity().startService(intent);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void location_history() {
        // Get layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewHistory = inflater.inflate(R.layout.location_history, null);

        // Show altitude graph
        final GraphView graphView = (GraphView) viewHistory.findViewById(R.id.gvLocation);
        showAltitudeGraph(graphView);

        // Fill list
        final ListView lv = (ListView) viewHistory.findViewById(R.id.lvLocationHistory);
        Cursor cursor = db.getLocations(0, Long.MAX_VALUE, true, true, false);
        final LocationAdapter adapter = new LocationAdapter(getActivity(), cursor);
        lv.setAdapter(adapter);

        // Handle list item click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) lv.getItemAtPosition(position);
                if (cursor != null) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    if (name != null)
                        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle list item long click
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) lv.getItemAtPosition(position);
                if (cursor != null) {
                    final long time = cursor.getLong(cursor.getColumnIndex("time"));

                    synchronized (getActivity()) {
                        if (elevationBusy)
                            return false;
                        else
                            elevationBusy = true;
                    }

                    // Get elevation for day
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Get range
                            Calendar from = Calendar.getInstance();
                            from.setTimeInMillis(time);
                            from.set(Calendar.HOUR_OF_DAY, 0);
                            from.set(Calendar.MINUTE, 0);
                            from.set(Calendar.SECOND, 0);
                            from.set(Calendar.MILLISECOND, 0);

                            Calendar to = Calendar.getInstance();
                            to.setTimeInMillis(time);
                            to.set(Calendar.HOUR_OF_DAY, 23);
                            to.set(Calendar.MINUTE, 59);
                            to.set(Calendar.SECOND, 59);
                            to.set(Calendar.MILLISECOND, 999);

                            // Get altitudes for range
                            LocationService.getAltitude(from.getTimeInMillis(), to.getTimeInMillis(), getActivity());

                            synchronized (getActivity()) {
                                elevationBusy = false;
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.msg_updated, getString(R.string.title_altitude_settings)), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();

                    // Feedback
                    return true;
                }

                return false;
            }
        });

        // Live updates
        final DatabaseHelper.LocationChangedListener listener = new DatabaseHelper.LocationChangedListener() {
            @Override
            public void onLocationAdded(Location location) {
                update();
            }

            @Override
            public void onLocationUpdated() {
                update();
            }

            @Override
            public void onLocationDeleted() {
                update();
            }

            private void update() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = db.getLocations(0, Long.MAX_VALUE, true, true, false);
                        adapter.changeCursor(cursor);
                        adapter.init(); // Possible new last location
                        showAltitudeGraph(graphView);
                    }
                });
            }
        };
        DatabaseHelper.addLocationChangedListener(listener);

        // Show layout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.title_location_history);
        alertDialogBuilder.setIcon(R.drawable.location_60);
        alertDialogBuilder.setView(viewHistory);
        alertDialogBuilder
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DatabaseHelper.removeLocationChangedListener(listener);
            }
        });
        alertDialog.show();
        // Fix keyboard input
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void showAltitudeGraph(GraphView graph) {
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

        boolean data = false;
        double minAlt = 10000;
        double maxAlt = 0;
        long minTime = new Date().getTime();
        long maxTime = 0;
        long from = new Date().getTime() - DAYS_HISTORY * DAY_MS;
        double avg = 0;
        int n = 0;

        Cursor cursor = db.getLocations(0, Long.MAX_VALUE, true, true, true);

        int colTime = cursor.getColumnIndex("time");
        int colProvider = cursor.getColumnIndex("provider");
        int colAltitude = cursor.getColumnIndex("altitude");

        int samples = Integer.parseInt(prefs.getString(PREF_ALTITUDE_AVG, DEFAULT_ALTITUDE_AVG));
        LineGraphSeries<DataPoint> seriesAltitudeReal = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> seriesAltitudeAvg = new LineGraphSeries<DataPoint>();

        while (cursor.moveToNext())
            if (!cursor.isNull(colAltitude)) {
                String provider = cursor.getString(colProvider);
                if (LocationManager.NETWORK_PROVIDER.equals(provider) ||
                        LocationManager.GPS_PROVIDER.equals(provider)) {
                    data = true;

                    long time = cursor.getLong(colTime);
                    if (time > from && time < minTime)
                        minTime = time;
                    if (time > maxTime)
                        maxTime = time;

                    double alt = cursor.getDouble(colAltitude);
                    if (alt < minAlt)
                        minAlt = alt;
                    if (alt > maxAlt)
                        maxAlt = alt;

                    avg = (n * avg + alt) / (n + 1);
                    if (n < samples)
                        n++;

                    seriesAltitudeReal.appendData(new DataPoint(new Date(time), alt), true, Integer.MAX_VALUE);
                    seriesAltitudeAvg.appendData(new DataPoint(new Date(time), avg), true, Integer.MAX_VALUE);
                }
            }

        if (data) {
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(minTime);
            graph.getViewport().setMaxX(maxTime);

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(minAlt);
            graph.getViewport().setMaxY(maxAlt);

            seriesAltitudeAvg.setDrawDataPoints(true);
            seriesAltitudeAvg.setDataPointsRadius(3);
            seriesAltitudeReal.setColor(Color.GRAY);

            graph.removeAllSeries();
            graph.addSeries(seriesAltitudeReal);
            graph.addSeries(seriesAltitudeAvg);

            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)));
            graph.getGridLabelRenderer().setNumHorizontalLabels(2);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalable(true);
        } else
            graph.setVisibility(View.GONE);
    }

    private void activity_history() {
        // Get layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewHistory = inflater.inflate(R.layout.activity_history, null);

        // Handle view list
        ImageView ivList = (ImageView) viewHistory.findViewById(R.id.ivList);
        if (LocationService.debugMode(getActivity()))
            ivList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity_list();
                }
            });
        else
            ivList.setVisibility(View.INVISIBLE);

        // Fill list
        final ListView lv = (ListView) viewHistory.findViewById(R.id.lvActivityDuration);
        Cursor cursor = db.getActivityDurations(0, Long.MAX_VALUE, false);
        final ActivityDurationAdapter adapter = new ActivityDurationAdapter(getActivity(), cursor);
        lv.setAdapter(adapter);

        // Handle list item click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) lv.getItemAtPosition(position);
                if (cursor != null) {
                    long time = cursor.getLong(cursor.getColumnIndex("time"));
                    activity_log(time, time + 24 * 3600 * 1000L);
                }
            }
        });

        // Live updates
        final DatabaseHelper.ActivityDurationChangedListener listener = new DatabaseHelper.ActivityDurationChangedListener() {
            @Override
            public void onActivityAdded(long day) {
                update();
            }

            @Override
            public void onActivityUpdated(long day, int activity, long duration) {
                update();
            }

            private void update() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = db.getActivityDurations(0, Long.MAX_VALUE, false);
                        adapter.changeCursor(cursor);
                    }
                });
            }
        };
        DatabaseHelper.addActivityDurationChangedListener(listener);

        // Show layout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.title_activity_history);
        alertDialogBuilder.setIcon(R.drawable.history_60);
        alertDialogBuilder.setView(viewHistory);
        alertDialogBuilder
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DatabaseHelper.removeActivityDurationChangedListener(listener);
            }
        });
        alertDialog.show();
    }

    private void activity_list() {
        // Get layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewHistory = inflater.inflate(R.layout.activity_list, null);

        // Set/handle history enabled
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        boolean enabled = prefs.getBoolean(PREF_RECOGNITION_HISTORY, DEFAULT_RECOGNITION_HISTORY);
        CheckBox cbHistoryEnabled = (CheckBox) viewHistory.findViewById(R.id.cbHistoryEnabled);
        cbHistoryEnabled.setChecked(enabled);
        cbHistoryEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(PREF_RECOGNITION_HISTORY, isChecked).apply();
            }
        });

        // Fill list
        final ListView lv = (ListView) viewHistory.findViewById(R.id.lvActivityHistory);
        Cursor cursor = db.getActivityTypes(0, Long.MAX_VALUE);
        final ActivityTypeAdapter adapter = new ActivityTypeAdapter(getActivity(), cursor);
        lv.setAdapter(adapter);

        // Live updates
        final DatabaseHelper.ActivityTypeChangedListener listener = new DatabaseHelper.ActivityTypeChangedListener() {
            @Override
            public void onActivityAdded(long time, int activity, int confidence) {
                update();
            }

            @Override
            public void onActivityDeleted() {
                update();
            }

            private void update() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = db.getActivityTypes(0, Long.MAX_VALUE);
                        adapter.changeCursor(cursor);
                        lv.setAdapter(adapter);
                    }
                });
            }
        };
        DatabaseHelper.addActivityTypeChangedListener(listener);

        // Handle delete
        ImageView ivDelete = (ImageView) viewHistory.findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(getString(R.string.msg_delete, getString(R.string.title_activity_history)));
                alertDialogBuilder
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncTask<Object, Object, Object>() {
                                    protected Object doInBackground(Object... params) {
                                        new DatabaseHelper(getActivity()).deleteActivityTypes().close();
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result) {
                                        adapter.changeCursor(db.getActivityTypes(0, Long.MAX_VALUE));
                                    }
                                }.execute();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Ignore
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // Show layout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.title_activity_history);
        alertDialogBuilder.setIcon(R.drawable.history_60);
        alertDialogBuilder.setView(viewHistory);
        alertDialogBuilder
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DatabaseHelper.removeActivityTypeChangedListener(listener);
            }
        });
        alertDialog.show();
    }

    private void activity_log(final long from, final long to) {
        // Get layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewHistory = inflater.inflate(R.layout.activity_log, null);

        TextView tvDate = (TextView) viewHistory.findViewById(R.id.tvDate);
        tvDate.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(from));

        // Fill list
        final ListView lv = (ListView) viewHistory.findViewById(R.id.lvActivityLog);
        Cursor cursor = db.getActivityLog(from, to, false);
        final ActivityLogAdapter adapter = new ActivityLogAdapter(getActivity(), cursor);
        lv.setAdapter(adapter);

        // Live updates
        final DatabaseHelper.ActivityLogChangedListener listener = new DatabaseHelper.ActivityLogChangedListener() {
            @Override
            public void onActivityAdded(long start, long finish, int activity) {
                update();
            }

            @Override
            public void onActivityUpdated(long start, long finish, int activity) {
                update();
            }

            private void update() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = db.getActivityLog(from, to, false);
                        adapter.changeCursor(cursor);
                        lv.setAdapter(adapter);
                    }
                });
            }
        };
        DatabaseHelper.addActivityLogChangedListener(listener);

        // Show layout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.title_activity_history);
        alertDialogBuilder.setIcon(R.drawable.history_60);
        alertDialogBuilder.setView(viewHistory);
        alertDialogBuilder
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DatabaseHelper.removeActivityLogChangedListener(listener);
            }
        });
        alertDialog.show();
    }

    private void step_history() {
        // Get layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewHistory = inflater.inflate(R.layout.step_history, null);

        // Show steps bar graph
        final GraphView graph = (GraphView) viewHistory.findViewById(R.id.gvStep);
        showStepGraph(graph);

        // Fill list
        final ListView lv = (ListView) viewHistory.findViewById(R.id.lvStepHistory);
        Cursor cursor = db.getSteps(false);
        final StepCountAdapter adapter = new StepCountAdapter(getActivity(), cursor);
        lv.setAdapter(adapter);

        // Live updates
        final DatabaseHelper.StepCountChangedListener listener = new DatabaseHelper.StepCountChangedListener() {
            @Override
            public void onStepCountAdded(long time, int count) {
                update();
            }

            @Override
            public void onStepCountUpdated(long time, int count) {
                update();
            }

            private void update() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = db.getSteps(false);
                        adapter.changeCursor(cursor);
                        lv.setAdapter(adapter);
                        showStepGraph(graph);
                    }
                });
            }
        };
        DatabaseHelper.addStepCountChangedListener(listener);

        // Show layout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.title_step_history);
        alertDialogBuilder.setIcon(R.drawable.walk_60);
        alertDialogBuilder.setView(viewHistory);
        alertDialogBuilder
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DatabaseHelper.removeStepCountChangedListener(listener);
            }
        });
        alertDialog.show();
    }

    private void showStepGraph(GraphView graph) {
        boolean data = false;
        int max = 10000;

        Cursor cursor = db.getSteps(true);
        int colTime = cursor.getColumnIndex("time");
        int colCount = cursor.getColumnIndex("count");

        BarGraphSeries<DataPoint> seriesStep = new BarGraphSeries<DataPoint>();

        while (cursor.moveToNext()) {
            data = true;

            long time = cursor.getLong(colTime);

            int count = cursor.getInt(colCount);
            if (count > max)
                max = count;

            seriesStep.appendData(new DataPoint(new Date(time), count), true, Integer.MAX_VALUE);
        }

        if (data) {
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(new Date().getTime() - DAYS_HISTORY * DAY_MS);
            graph.getViewport().setMaxX(new Date().getTime());

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(max);

            seriesStep.setSpacing(10);

            graph.removeAllSeries();
            graph.addSeries(seriesStep);

            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setScrollable(true);
        } else
            graph.setVisibility(View.GONE);
    }

    private void updateTitle(SharedPreferences prefs, String key) {
        Preference pref = findPreference(key);

        if (PREF_SHARE_GPX.equals(key)) {
            long time = prefs.getLong(PREF_LAST_SHARE_GPX, -1);
            String ftime = (time > 0 ? SimpleDateFormat.getDateTimeInstance().format(time) : getString(R.string.msg_never));
            pref.setSummary(getString(R.string.summary_share_gpx, ftime));
        } else if (PREF_SHARE_KML.equals(key)) {
            long time = prefs.getLong(PREF_LAST_SHARE_KML, -1);
            String ftime = (time > 0 ? SimpleDateFormat.getDateTimeInstance().format(time) : getString(R.string.msg_never));
            pref.setSummary(getString(R.string.summary_share_kml, ftime));

        } else if (PREF_UPLOAD_GPX.equals(key)) {
            long time = prefs.getLong(PREF_LAST_UPLOAD_GPX, -1);
            String ftime = (time > 0 ? SimpleDateFormat.getDateTimeInstance().format(time) : getString(R.string.msg_never));
            pref.setSummary(getString(R.string.summary_upload_gpx, ftime));

        } else if (PREF_INTERVAL.equals(key))
            pref.setTitle(getString(R.string.title_interval, prefs.getString(key, DEFAULT_INTERVAL)));
        else if (PREF_TP_ACCURACY.equals(key))
            pref.setTitle(getString(R.string.title_tp_accuracy, prefs.getString(key, DEFAULT_TP_ACCURACY)));
        else if (PREF_WP_ACCURACY.equals(key))
            pref.setTitle(getString(R.string.title_wp_accuracy, prefs.getString(key, DEFAULT_WP_ACCURACY)));
        else if (PREF_TIMEOUT.equals(key))
            pref.setTitle(getString(R.string.title_timeout, prefs.getString(key, DEFAULT_TIMEOUT)));
        else if (PREF_CHECK_TIME.equals(key))
            pref.setTitle(getString(R.string.title_check_time, prefs.getString(key, DEFAULT_CHECK_TIME)));
        else if (PREF_CHECK_SAT.equals(key))
            pref.setTitle(getString(R.string.title_check_sat, prefs.getString(key, DEFAULT_CHECK_SAT)));
        else if (PREF_INACCURATE.equals(key))
            pref.setTitle(getString(R.string.title_inaccurate, prefs.getString(key, DEFAULT_INACCURATE)));
        else if (PREF_NEARBY.equals(key))
            pref.setTitle(getString(R.string.title_nearby, prefs.getString(key, DEFAULT_NEARBY)));
        else if (PREF_MINTIME.equals(key))
            pref.setTitle(getString(R.string.title_mintime, prefs.getString(key, DEFAULT_MINTIME)));
        else if (PREF_MINDIST.equals(key))
            pref.setTitle(getString(R.string.title_mindist, prefs.getString(key, DEFAULT_MINDIST)));

        else if (PREF_PASSIVE_BEARING.equals(key))
            pref.setTitle(getString(R.string.title_passive_bearing, prefs.getString(key, DEFAULT_PASSIVE_BEARING)));
        else if (PREF_PASSIVE_ALTITUDE.equals(key))
            pref.setTitle(getString(R.string.title_passive_altitude, prefs.getString(key, DEFAULT_PASSIVE_ALTITUDE)));
        else if (PREF_PASSIVE_INACCURATE.equals(key))
            pref.setTitle(getString(R.string.title_inaccurate, prefs.getString(key, DEFAULT_PASSIVE_INACCURATE)));
        else if (PREF_PASSIVE_NEARBY.equals(key))
            pref.setTitle(getString(R.string.title_nearby, prefs.getString(key, DEFAULT_PASSIVE_NEARBY)));
        else if (PREF_PASSIVE_MINTIME.equals(key))
            pref.setTitle(getString(R.string.title_mintime, prefs.getString(key, DEFAULT_PASSIVE_MINTIME)));
        else if (PREF_PASSIVE_MINDIST.equals(key))
            pref.setTitle(getString(R.string.title_mindist, prefs.getString(key, DEFAULT_PASSIVE_MINDIST)));

        else if (PREF_ALTITUDE_AVG.equals(key))
            pref.setTitle(getString(R.string.title_altitude_avg, prefs.getString(key, DEFAULT_ALTITUDE_AVG)));

        else if (PREF_RECOGNITION_INTERVAL_STILL.equals(key))
            pref.setTitle(getString(R.string.title_recognition_interval_still, prefs.getString(key, DEFAULT_RECOGNITION_INTERVAL_STILL)));
        else if (PREF_RECOGNITION_INTERVAL_MOVING.equals(key))
            pref.setTitle(getString(R.string.title_recognition_interval_moving, prefs.getString(key, DEFAULT_RECOGNITION_INTERVAL_MOVING)));
        else if (PREF_RECOGNITION_CONFIDENCE.equals(key))
            pref.setTitle(getString(R.string.title_recognition_confidence, prefs.getString(key, DEFAULT_RECOGNITION_CONFIDENCE)));

        else if (PREF_STEP_DELTA.equals(key))
            pref.setTitle(getString(R.string.title_step_delta, prefs.getString(key, DEFAULT_STEP_DELTA)));
        else if (PREF_STEP_SIZE.equals(key))
            pref.setTitle(getString(R.string.title_step_size, prefs.getString(key, DEFAULT_STEP_SIZE)));
        else if (PREF_WEIGHT.equals(key))
            pref.setTitle(getString(R.string.title_weight, prefs.getString(key, DEFAULT_WEIGHT)));

        else if (PREF_BLOGURL.equals(key))
            pref.setTitle(getString(R.string.title_blogurl, prefs.getString(key, getString(R.string.msg_notset))));
        else if (PREF_BLOGID.equals(key))
            pref.setTitle(getString(R.string.title_blogid, prefs.getString(key, "1")));
        else if (PREF_BLOGUSER.equals(key))
            pref.setTitle(getString(R.string.title_bloguser, prefs.getString(key, getString(R.string.msg_notset))));
        else if (PREF_BLOGPWD.equals(key))
            if (prefs.getString(key, null) == null)
                pref.setTitle(getString(R.string.title_blogpwd, getString(R.string.msg_notset)));
            else
                pref.setTitle(getString(R.string.title_blogpwd, "********"));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    private boolean storageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private boolean blogConfigured() {
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        return (prefs.getString(PREF_BLOGURL, null) != null);
    }
}
