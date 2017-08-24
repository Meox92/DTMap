package com.example.maola.degradotourmap.MapUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.maola.degradotourmap.Model.Report;
import com.example.maola.degradotourmap.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewMarkerActivity extends AppCompatActivity {


    @BindView(R.id.newMarker_spinner_tipologia)
    Spinner newMarkerSpinnerTipologia;
    @BindView(R.id.newMarker_cb_orario0)
    CheckBox newMarkerCbOrario0;
    @BindView(R.id.newMarker_cb_orario1)
    CheckBox newMarkerCbOrario1;
    @BindView(R.id.newMarker_cb_orario2)
    CheckBox newMarkerCbOrario2;
    @BindView(R.id.newMarker_cb_orario3)
    CheckBox newMarkerCbOrario3;
    @BindView(R.id.newMarker_cb_orario4)
    CheckBox newMarkerCbOrario4;
    @BindView(R.id.newMarker_cb_orario5)
    CheckBox newMarkerCbOrario5;
    @BindView(R.id.newMarker_edt_titolo)
    EditText newMarkerEdtTitolo;
    @BindView(R.id.newMarker_edt_descrizione)
    EditText newMarkerEdtDescrizione;
    @BindView(R.id.newMarker_btn_invia)
    Button newMarkerBtnInvia;
    private List<String> spinnerList;
    private DatabaseReference myRef;
    private int POINTS = 0;
    private Double dLat;
    private Double dLng;
    private String TAG = "NewMarkerActivity";
    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent i = getIntent();
        dLat = i.getDoubleExtra("vLat", 0);
        dLng = i.getDoubleExtra("vLng", 0);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(getString(R.string.report));

        spinnerList = new ArrayList<String>();
        spinnerList.add(getString(R.string.prostituzione));
        spinnerList.add(getString(R.string.spaccio));
        spinnerList.add(getString(R.string.furti));
        spinnerList.add(getString(R.string.vandalismo));
        spinnerList.add(getString(R.string.discarica));
        spinnerList.add(getString(R.string.altro));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newMarkerSpinnerTipologia.setAdapter(adapter);




    }

    @OnClick(R.id.newMarker_btn_invia)
    public void onViewClicked() {

//                /*---Array Checkbox*/
        List<String> listOrario = new ArrayList<>();
        if(newMarkerCbOrario0.isChecked()) { listOrario.add(newMarkerCbOrario0.getText().toString()); }
        if(newMarkerCbOrario1.isChecked()) { listOrario.add(newMarkerCbOrario1.getText().toString()); }
        if(newMarkerCbOrario2.isChecked()) { listOrario.add(newMarkerCbOrario2.getText().toString()); }
        if(newMarkerCbOrario3.isChecked()) { listOrario.add(newMarkerCbOrario3.getText().toString()); }
        if(newMarkerCbOrario4.isChecked()) { listOrario.add(newMarkerCbOrario4.getText().toString()); }



        Date date = new Date();
        List<String> commenti = new ArrayList<>();





        String markerID = myRef.push().getKey();
        Report report = new Report("user1",
                dLat,
                dLng,
                newMarkerEdtTitolo.getText().toString(),
                newMarkerEdtDescrizione.getText().toString(),
                newMarkerSpinnerTipologia.getSelectedItem().toString(),
                "uri foto",
                listOrario,
                date.getTime(),
                commenti,
                POINTS);

        myRef.child(markerID).setValue(report);




//        final ArrayList<String> friends = new ArrayList<String>();

//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // dataSnapshot is the "issue" node with all children with id 0
//                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
//
//                        friends.add(issue.getKey());
//
//                        Toast.makeText(NewMarkerActivity.this, friends.toString(), Toast.LENGTH_LONG).show();
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }




}
