package com.example.maola.degradotourmap.MapUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maola.degradotourmap.MapsActivity;
import com.example.maola.degradotourmap.Model.Report;
import com.example.maola.degradotourmap.R;
import com.example.maola.degradotourmap.User.LoginActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    @BindView(R.id.newMarker_btn_sfoglia)
    Button newMarkerBtnSfoglia;
    @BindView(R.id.newMarker_txt_pic1)
    TextView newMarkerTxtPic1;
    @BindView(R.id.newMarker_img_pic1)
    ImageView newMarkerImgPic1;
    @BindView(R.id.newMarker_txt_address)
    TextView newMarkerTxtAddress;
    private List<String> spinnerList;
    private DatabaseReference myRef;
    private int POINTS = 0;
    private Double dLat;
    private Double dLng;
    private String TAG = "NewMarkerActivity";
    private Report report;
    private StorageReference mStorageRef;
    private StorageReference riversRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private Uri reportPic;
    private String urlReportPic;
    private String markerID;

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
        getAddress();

        /*-----------------USER INSTANCE----------------------*/
        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(NewMarkerActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        /*-------------------------------------------------*/

        mStorageRef = FirebaseStorage.getInstance().getReference();
        /*-------------------------------------------------*/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(getString(R.string.report));
        markerID = myRef.push().getKey();
        riversRef = mStorageRef.child("images/" + user.getUid() + "/" + markerID + "/reportpicture1");
        /*-------------------------------------------------*/


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

    public void getAddress(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(dLat, dLng, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // sending back first address line and locality
                newMarkerTxtAddress.setText(address.getAddressLine(0) + ", " + address.getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    setThumbnail(data);
                    uploadFile();
                }
        }
    }



    public void setThumbnail(Intent data) {
        /*Questo metodo mostra l'anteprima e il nome del file selezionata, premendo sul nome si può eliminare il file */
        reportPic = data.getData();
        newMarkerTxtPic1.setText(reportPic.getLastPathSegment());
        newMarkerTxtPic1.setVisibility(View.VISIBLE);
        newMarkerTxtPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportPic = null;
                riversRef.delete();
                newMarkerTxtPic1.setVisibility(View.GONE);
                newMarkerImgPic1.setVisibility(View.GONE);
            }
        });
        newMarkerImgPic1.setImageURI(reportPic);
        newMarkerImgPic1.setVisibility(View.VISIBLE);
    }


    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (reportPic != null) {

            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            riversRef.putFile(reportPic)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            urlReportPic = downloadUrl.toString();


                            progressDialog.dismiss();
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred());

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    public void save() {

        //                /*---Array Checkbox*/
        List<String> listOrario = new ArrayList<>();
        if (newMarkerCbOrario0.isChecked()) {
            listOrario.add(newMarkerCbOrario0.getText().toString());
        }
        if (newMarkerCbOrario1.isChecked()) {
            listOrario.add(newMarkerCbOrario1.getText().toString());
        }
        if (newMarkerCbOrario2.isChecked()) {
            listOrario.add(newMarkerCbOrario2.getText().toString());
        }
        if (newMarkerCbOrario3.isChecked()) {
            listOrario.add(newMarkerCbOrario3.getText().toString());
        }
        if (newMarkerCbOrario4.isChecked()) {
            listOrario.add(newMarkerCbOrario4.getText().toString());
        }



        Date currentTime = Calendar.getInstance().getTime();

        List<String> commenti = new ArrayList<>();


        report = new Report(user.getEmail(),
                dLat,
                dLng,
                newMarkerEdtTitolo.getText().toString(),
                newMarkerEdtDescrizione.getText().toString(),
                newMarkerSpinnerTipologia.getSelectedItem().toString(),
                urlReportPic,
                listOrario,
                currentTime.toString(),
                commenti,
                POINTS,
                "");

        myRef.child(markerID).setValue(report);

        Toast.makeText(NewMarkerActivity.this, "Segnalazione inserita con successo!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(NewMarkerActivity.this, MapsActivity.class);
        startActivity(i);
    }


//    @OnClick(R.id.newMarker_btn_invia)
//    public void onViewClicked() {
//        save();
//
//
////        final ArrayList<String> friends = new ArrayList<String>();
//
////        query.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()) {
////                    // dataSnapshot is the "issue" node with all children with id 0
////                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
////
////                        friends.add(issue.getKey());
////
////                        Toast.makeText(NewMarkerActivity.this, friends.toString(), Toast.LENGTH_LONG).show();
////
////                    }
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Se è stata caricata una foto ma il file non è stato salvato premendo su salva, allora elimina il file dallo storage
        if (reportPic != null)
            riversRef.delete();
    }


    @OnClick({R.id.newMarker_btn_sfoglia, R.id.newMarker_btn_invia})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newMarker_btn_sfoglia:
                openGallery();
                break;
            case R.id.newMarker_btn_invia:
                save();
                break;
        }
    }
}
