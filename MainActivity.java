package com.example.shesafe;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView txtStatus;
    Button btnSOS;

    // Home location (example)
    double homeLat = 12.9716;
    double homeLng = 77.5946;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        btnSOS = findViewById(R.id.btnSOS);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // LOCATION TRACKING
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5,
                location -> {

                    if (isAfterSetTime() &&
                            isOutsideHome(location.getLatitude(),
                                    location.getLongitude())) {

                        txtStatus.setText("Tracking ON");
                    } else {
                        txtStatus.setText("Tracking OFF");
                    }
                }
        );

        // SOS BUTTON
        btnSOS.setOnClickListener(v -> sendEmergencyAlert());
    }

    // TIME CHECK LOGIC
    private boolean isAfterSetTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 19; // After 7 PM (user-defined later)
    }

    // HOME GEOFENCE LOGIC
    private boolean isOutsideHome(double lat, double lng) {
        float[] result = new float[1];
        Location.distanceBetween(lat, lng, homeLat, homeLng, result);
        return result[0] > 100; // Outside 100 meters
    }

    // FIREBASE ALERT
    private void sendEmergencyAlert() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("alerts");
        ref.push().setValue("Emergency Alert Triggered");
    }
}
