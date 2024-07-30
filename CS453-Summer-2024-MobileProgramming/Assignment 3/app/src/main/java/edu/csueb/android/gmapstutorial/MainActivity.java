package edu.csueb.android.gmapstutorial;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.981860);
    private GoogleMap map;
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(LOCATION_CS).title("Find Me Here!"));

        map.setOnMapClickListener(point -> {
            Log.d(TAG, "Map clicked at: " + point.toString());
            float zoom = map.getCameraPosition().zoom; // Get zoom level on the main thread
            new LocationInsertTask().execute(new LatLngWithZoom(point, zoom));
            map.addMarker(new MarkerOptions().position(point));
            Toast.makeText(MainActivity.this, "Marker is added to the map", Toast.LENGTH_SHORT).show();
        });

        map.setOnMapLongClickListener(point -> {
            Log.d(TAG, "Map long clicked at: " + point.toString());
            new LocationDeleteTask().execute();
            Toast.makeText(MainActivity.this, "All markers are removed", Toast.LENGTH_SHORT).show();
        });
    }

    public void onClick_CS(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, 18);
        map.animateCamera(update);
    }

    public void onClick_Univ(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 14);
        map.animateCamera(update);
    }

    public void onClick_City(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10);
        map.animateCamera(update);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, LocationsContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;

        if (data != null) {
            locationCount = data.getCount();
            data.moveToFirst();
        }

        for (int i = 0; i < locationCount; i++) {
            int latIndex = data.getColumnIndex(LocationsDB.COLUMN_LATITUDE);
            int lngIndex = data.getColumnIndex(LocationsDB.COLUMN_LONGITUDE);
            int zoomIndex = data.getColumnIndex(LocationsDB.COLUMN_ZOOM);

            if (latIndex >= 0 && lngIndex >= 0 && zoomIndex >= 0) {
                lat = data.getDouble(latIndex);
                lng = data.getDouble(lngIndex);
                zoom = data.getFloat(zoomIndex);

                LatLng location = new LatLng(lat, lng);
                map.addMarker(new MarkerOptions().position(location));
            } else {
                Log.e(TAG, "Invalid column index");
            }

            data.moveToNext();
        }

        if (locationCount > 0) {
            LatLng lastLocation = new LatLng(lat, lng);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, zoom));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        map.clear();
    }

    private static class LatLngWithZoom {
        LatLng point;
        float zoom;

        LatLngWithZoom(LatLng point, float zoom) {
            this.point = point;
            this.zoom = zoom;
        }
    }

    private class LocationInsertTask extends AsyncTask<LatLngWithZoom, Void, Void> {
        @Override
        protected Void doInBackground(LatLngWithZoom... params) {
            LatLngWithZoom latLngWithZoom = params[0];
            LatLng point = latLngWithZoom.point;
            float zoom = latLngWithZoom.zoom;

            try {
                ContentValues values = new ContentValues();
                values.put(LocationsDB.COLUMN_LATITUDE, point.latitude);
                values.put(LocationsDB.COLUMN_LONGITUDE, point.longitude);
                values.put(LocationsDB.COLUMN_ZOOM, zoom);

                getContentResolver().insert(LocationsContentProvider.CONTENT_URI, values);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting location", e);
            }
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting locations", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Clear the map on the main thread after deletion
            runOnUiThread(() -> {
                if (map != null) {
                    map.clear();
                }
            });
        }
    }
}
