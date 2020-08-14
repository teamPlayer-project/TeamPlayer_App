package com.project.teamplayer;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivityGroup extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int DEFAULT_ZOOM = 20;
    private  LatLng defaultLocation = new LatLng(31.4117, 35.0818);
    private String documentActivityName;
    private String backTo;
    private String backScreen;
    private String description;
    private String lat;
    private String lon;
    ArrayList<String> detailsList;
    ArrayList<String> activitiesNameList;
    ArrayList<String> descriptionsList;
    private ArrayList<String> managerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_group);

        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        detailsList = getIntent().getStringArrayListExtra("Details");
        activitiesNameList = getIntent().getStringArrayListExtra("ACTIVITIES_NAME_LIST");
        descriptionsList = getIntent().getStringArrayListExtra("DESCRIPTIONS_LIST");
        managerList = getIntent().getStringArrayListExtra("MANAGER_LIST");
        description = getIntent().getStringExtra("DESCRIPTION");
        backTo = getIntent().getStringExtra("GOT_FROM");
        description = getIntent().getStringExtra("DESCRIPTION");
        backTo = getIntent().getStringExtra("GOT_FROM");
        documentActivityName = getIntent().getStringExtra("ACTIVITY_NAME");
        backScreen = getIntent().getStringExtra("Back_TO");
        Button done = (Button) findViewById(R.id.button4);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backButton();
            }
        });
        ImageButton zoom = (ImageButton) findViewById(R.id.myLocation);
        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zoomOnLocation();
            }
        });
        if (!lat.equals("") && !lon.equals("")){
            defaultLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Marker in israel"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
    }

    /**
     * The function when user chose location
     */
    public void backButton(){
        Intent intent = null;
        if (backScreen.equals("Details")){
            intent = new Intent(this, activity_details.class);
            intent.putExtra("Details", detailsList);
            intent.putExtra("ACTIVITIES_NAME_LIST", activitiesNameList);
            intent.putExtra("DESCRIPTIONS_LIST", descriptionsList);
            intent.putExtra("MANAGER_LIST", managerList);
        }else if (backScreen.equals("Group")){
            intent = new Intent(this, group.class);
        }
        intent.putExtra("ACTIVITY_NAME", documentActivityName);
        intent.putExtra("DESCRIPTION", description);
        intent.putExtra("GOT_FROM", backTo);
        startActivity(intent);
    }

    public void zoomOnLocation(){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
    }
}
