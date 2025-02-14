package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import org.osmdroid.config.Configuration;
import com.google.firebase.firestore.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.util.Pair;

/**
 * OrganizerMapActivity provides a map view for organizers, leveraging OSMDroid to render OpenStreetMap
 * and display multiple markers based on entrant and event data stored in Firebase.
 */
public class OrganizerMapActivity extends AppCompatActivity {

    private MapView mapView;
    private OverallStorageController overallStorageController;

    /**
     * Initializes the activity, sets up the map view, back button, and loads markers asynchronously.
     *
     * @param savedInstanceState Bundle containing saved state data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set OSMDroid cache directory
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        setContentView(R.layout.map_activity);

        // Set up the back button to return to the previous screen
        Button backButton = findViewById(R.id.map_activity_back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Initialize MapView and set basic map settings
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true); // Enable multi-touch gestures (e.g., pinch to zoom)

        // Set initial map center and zoom level
        mapView.getController().setZoom(10.0);
        mapView.getController().setCenter(new org.osmdroid.util.GeoPoint(53.5461, -113.4938)); // Center on Edmonton

        // Load locations from Firebase and add markers asynchronously
        addLocationsAndMarkers();
    }

    /**
     * Adds a marker to the map at the specified location with a given title.
     *
     * @param location GeoPoint object containing latitude and longitude coordinates.
     * @param title    The title to be displayed when the marker is tapped.
     */
    private void addMarker(GeoPoint location, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new org.osmdroid.util.GeoPoint(location.getLatitude(), location.getLongitude()));
        marker.setTitle(title);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Set anchor point
        mapView.getOverlays().add(marker); // Add the marker to the map
    }

    /**
     * Retrieves entrant and event data from Firebase and adds corresponding markers to the map.
     * Uses OverallStorageController to fetch data and process it asynchronously.
     */
    private void addLocationsAndMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        overallStorageController = new OverallStorageController();

        db.collection("EntrantDB").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("OrganizerMapActivity", "Database query successful");

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String entrantId = document.getId();

                    overallStorageController.getEntrant(entrantId, new EntrantCallback() {
                        @Override
                        public void onSuccess(Entrant entrant) {
                            // Add markers for each event in the entrant's GeoPoint map
                            for (String eventId : entrant.getGeoPointMap().keySet()) {
                                overallStorageController.getEvent(eventId, new EventCallback() {
                                    @Override
                                    public void onSuccess(Event event) {
                                        GeoPoint geoPoint = entrant.getGeoPointMap().get(eventId);
                                        if (geoPoint != null && Objects.equals(event.getGeolocation(), "true")) {
                                            String title = entrant.getName() + " joined " + eventId + " at " +
                                                    geoPoint.getLatitude() + ", " + geoPoint.getLongitude();

                                            // Add marker directly after data is fetched
                                            runOnUiThread(() -> addMarker(geoPoint, title));
                                            Log.d("OrganizerMapActivity", title);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.e("OrganizerMapActivity", "Failed to retrieve event data: " + errorMessage);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("OrganizerMapActivity", "Failed to retrieve entrant data: " + errorMessage);
                        }
                    });
                }
            } else {
                Log.e("OrganizerMapActivity", "Failed to query EntrantDB: " + task.getException().getMessage());
            }
        });
    }

    /**
     * Resumes the MapView when the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // Resume MapView when activity is resumed
    }

    /**
     * Pauses the MapView when the activity pauses.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // Pause MapView to save resources
    }

    /**
     * Releases MapView resources properly when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach(); // Properly release MapView resources
    }
}
