
/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.maola.degradotourmap;


import com.example.maola.degradotourmap.MapUtils.NewMarkerActivity;
import com.example.maola.degradotourmap.Model.Report;
import com.example.maola.degradotourmap.Utility.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    private DatabaseReference myRef;
    private List<Report> lSegnalazioni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(getResources().getString(R.string.report));

        //TODO permette di fare la query nel db
        Query query = myRef.child("Report").orderByChild("userId").equalTo("user1");


        lSegnalazioni = new ArrayList<Report>();
//        final List<String> stringList = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapSegnalazioni : dataSnapshot.getChildren()) {
                    lSegnalazioni.add(snapSegnalazioni.getValue(Report.class));
                    //TODO creare modalità lista
                }
                for(int i = 0; i< lSegnalazioni.size(); i++){
                    Report report1 = lSegnalazioni.get(i);
                    report1.getTime();
                    String typology = report1.getTypology();
                    report1.getTitle();

//                    mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
//                    ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    setMarkerColor(report1, typology);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                double dLat = latLng.latitude;
                double dLng = latLng.longitude;
                Intent i = new Intent(MapsActivity.this, NewMarkerActivity.class);
                i.putExtra("vLat", dLat);
                i.putExtra("vLng", dLng);
                //passaggio della variabile latLng ti tipo latLng, ma per ritrovare la città dalle coordinate bisogna avere lat e lng separati
//                Bundle args = new Bundle();
//                args.putParcelable("varLatLng", latLng);
//                i.putExtra("BUNDLE_LatLng", args);
                startActivity(i);
//                mMap.addMarker(new MarkerOptions().position(latLng).title("Custom Marker"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }
        });

    }


    public void setMarkerColor(Report report1, String typology){


        if(typology.equals(getString(R.string.prostituzione))) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        if(typology.equals(getString(R.string.spaccio))){
            mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(report1.getDescription()));
        }
        if(typology.equals(getString(R.string.furti))){
            mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }        if(typology.equals(getString(R.string.vandalismo))){
            mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }        if(typology.equals(getString(R.string.discarica))){
            mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }        if(typology.equals(getString(R.string.altro))){
            mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }else{
            Log.i("MapsActivity", "Errore");
        }


    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }



}
