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
import com.example.maola.degradotourmap.MapUtils.ReportDetailActivity;
import com.example.maola.degradotourmap.Model.Report;
import com.example.maola.degradotourmap.Utility.FirebaseUtils;
import com.example.maola.degradotourmap.Utility.PermissionUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
        ActivityCompat.OnRequestPermissionsResultCallback{

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
    private Report report1;
    private String description, time;
    private Object reportIdObj;
    private List<Object> listaChiavi;
    private Marker markerID;
    private HashMap<String, Object> result;
    private List<String> markerIdList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myRef = FirebaseUtils.getReportRef();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        myRef = database.getReference(getResources().getString(R.string.report));

        //TODO permette di fare la query nel db
        Query query = myRef.child("Report").orderByChild("userId").equalTo("user1");


        reportListener();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "Clicked on snippet" + marker.getTitle() + marker.getId(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MapsActivity.this, ReportDetailActivity.class);
                i.putExtra("varMarkerId", marker.getId());
                i.putExtra("varIdMarkerDB", result);
                startActivity(i);
                Log.i("MarkerID", result.get(marker.getId()) + "");
//                Iterator myVeryOwnIterator = result.keySet().iterator();
//                while(myVeryOwnIterator.hasNext()) {
//                    String key=(String)myVeryOwnIterator.next();
//                    String value=(String)result.get(marker.getId());
//                    Log.i("Marker", "Key: "+key+" Value: "+value);
//                }



            }
        });


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

    public void reportListener(){
        lSegnalazioni = new ArrayList<Report>();
        listaChiavi = new ArrayList<>();
//        final List<String> stringList = new ArrayList<>();
        markerIdList = new ArrayList<String>();
        result = new HashMap<>();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapSegnalazioni : dataSnapshot.getChildren()) {
                    lSegnalazioni.add(snapSegnalazioni.getValue(Report.class));
                     reportIdObj = snapSegnalazioni.getKey();
                    listaChiavi.add(reportIdObj);

                    //TODO creare modalità lista
                }
                for(int i = 0; i< lSegnalazioni.size(); i++){
                    report1 = lSegnalazioni.get(i);
                    String typology = report1.getTypology();
                    description = report1.getDescription();
                    time = report1.getReportingDate();

//                    mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
//                    ).title(report1.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                    setMarkerColor(report1, typology);
                    markerID = setMarkerColor(report1, typology);
//                    markerIdList.add(i, markerID.getId());

                    result.put(markerID.getId(), listaChiavi.get(i));
                    Log.i("TAG", result.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }




    public Marker setMarkerColor(Report report1, String typology){
        Marker mRenderedMarker;

        if(typology.equals(getString(R.string.prostituzione))) {
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getReportingDate() + "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        else if(typology.equals(getString(R.string.spaccio))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(report1.getPoints() + "_" + report1.getReportingDate()+ "_" + report1.getDescription()));
        }
        else if(typology.equals(getString(R.string.furti))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getReportingDate()+ "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        } else if(typology.equals(getString(R.string.vandalismo))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getReportingDate()+ "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        } else if(typology.equals(getString(R.string.discarica))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getReportingDate()+ "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } else if(typology.equals(getString(R.string.altro))){
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet(report1.getPoints() + "_" + report1.getReportingDate()+ "_" + report1.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }else{
            Log.i("MapsActivity", "Errore");
            mRenderedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(report1.getLat(), report1.getLng())
            ).title(report1.getTitle())
                    .snippet("non disp1_non disp2_non disp3"));
        }

        return mRenderedMarker;
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



    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


        private final View mContents;

        CustomInfoWindowAdapter() {
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }


        private void render(Marker marker, View view) {

            String str = marker.getSnippet();
            final String[] str2 = str.split("_");

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.info_title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            TextView snippetUi = ((TextView)view.findViewById(R.id.info_date));
            if (str2[1] != null) {
                SpannableString snippetText = new SpannableString(time);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
//                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, str2.length, 0);
                snippetUi.setText(str2[1]);
            } else {
                snippetUi.setText("");
            }

            TextView txtDescription = (TextView)view.findViewById(R.id.info_description);
            if(str2.length>2){
                txtDescription.setText(str2[2]);
            } else {
                txtDescription.setText("Description not available");
            }

            TextView txtPoint = (TextView)view.findViewById(R.id.info_points);
            txtPoint.setText(str2[0]);

        }


    }

}
