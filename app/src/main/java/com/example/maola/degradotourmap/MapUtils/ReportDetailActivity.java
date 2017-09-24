package com.example.maola.degradotourmap.MapUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maola.degradotourmap.Model.Report;
import com.example.maola.degradotourmap.R;
import com.example.maola.degradotourmap.User.LoginActivity;
import com.example.maola.degradotourmap.Utility.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportDetailActivity extends AppCompatActivity {
    @BindView(R.id.reportdetail_txt_points)
    TextView reportdetailTxtPoints;
    @BindView(R.id.reportdetail_txt_comments)
    TextView reportdetailTxtComments;
    @BindView(R.id.reportdetail_btn_publishcomment)
    Button reportdetailBtnPublishcomment;
    @BindView(R.id.reportdetail_txt_date)
    TextView reportdetailTxtDate;
    @BindView(R.id.reportdetail_txt_username)
    TextView reportdetailTxtUsername;
    @BindView(R.id.reportdetail_txt_title)
    TextView reportdetailTxtTitle;
    @BindView(R.id.reportdetail_txt_descr)
    TextView reportdetailTxtDescr;
    @BindView(R.id.reportdetail_img_picture)
    ImageView reportdetailImgPicture;
    @BindView(R.id.reportdetail_btn_delete)
    Button reportdetailBtnDelete;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.reportDetail_imgbtn_upvote)
    ImageButton reportDetailImgbtnUpvote;
    @BindView(R.id.reportDetail_imgbtn_downvote)
    ImageButton reportDetailImgbtnDownvote;

    private String TAG = "ReportDetailActivity";
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String idReport;
    private String reportTitle, reportAuthor, reportDate, reportPoints, reportDescr, reportTypo, reportPciture;
    private List reportTime;
    private Report report;
    private String userEmail;
    private int oldPoint = 0;
    private Query query;
    private boolean ratedPositive;
    private boolean ratedNegative;
    private SharedPreferences sharedPreferences;
    private String SHARED_PREF = "PREF_LOGIN";
    private String TagVoto = "Votato";
    private boolean votoPositivo;
    private boolean votoNegativo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
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

        myRef = FirebaseUtils.getReportRef();

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        myRef = database.getReference(getResources().getString(R.string.report));

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
                    startActivity(new Intent(ReportDetailActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        /*-------------------------------------------------*/

        userEmail = user.getEmail();


        Intent i = getIntent();
        if (i != null) {
            String markerID = i.getStringExtra("varMarkerId");
            HashMap<String, String> result = (HashMap<String, String>) i.getSerializableExtra("varIdMarkerDB");
            idReport = result.get(markerID);
            Log.i(TAG, result + "\nMarker ID: " + markerID + " Report ID :" + idReport);
        }

        /*Check if the user has already ratedPositive this report*/
        sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        votoPositivo = sharedPreferences.getBoolean("ratedPositive" + idReport, false);
        votoNegativo = sharedPreferences.getBoolean("ratedNegative" + idReport, false);
        if(votoPositivo){
            setImageVote(reportDetailImgbtnUpvote, R.drawable.likeyes);
            reportDetailImgbtnUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    annullaVotoPositivo(reportDetailImgbtnUpvote, R.drawable.likeno);
                }
            });
        }
        if(votoNegativo){
            setImageVote(reportDetailImgbtnUpvote, R.drawable.likeyes);
            reportDetailImgbtnUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    annullaVotoPositivo(reportDetailImgbtnUpvote, R.drawable.likeno);
                }
            });
        }

        /*---------------------------------------*/

        getReportInfo();


    }


    public void getReportInfo() {
        reportTitle = "";

        query = myRef.child(idReport);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                report = dataSnapshot.getValue(Report.class);
                if (report != null) {
                    reportTitle = report.getTitle();
                    reportAuthor = report.getUserId();
                    reportDate = report.getReportingDate();
                    reportPoints = String.valueOf(report.getPoints());
                    reportDescr = report.getDescription();
                    reportTime = report.getTime();
                    reportTypo = report.getTypology();
                    reportPciture = report.getPicture();
                    setInfo();
//                Log.i(TAG, reportTitle.toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.toString());

            }
        });
    }

    public void getUpdatedPoint() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (report != null) {
                    reportPoints = String.valueOf(report.getPoints());
                    reportdetailTxtPoints.setText(reportPoints);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError + "");
            }
        });
    }

    public void setInfo() {
        reportdetailTxtDate.setText(reportDate);
        reportdetailTxtUsername.setText(reportAuthor);
        reportdetailTxtTitle.setText(reportTitle);
        reportdetailTxtDescr.setText(reportDescr);
        if (reportPciture != null) {
            reportdetailImgPicture.setVisibility(View.VISIBLE);
            reportdetailImgPicture.setImageURI(Uri.parse(reportPciture));
        }
        if (userEmail.equals(reportAuthor)) {
            reportdetailBtnDelete.setVisibility(View.VISIBLE);
            reportdetailBtnDelete.setText("Autore del post");
        }

        reportdetailTxtPoints.setText(getString(R.string.points) + " " + reportPoints);
    }

    public void setRated(String TAG, boolean ratedPositive) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG + idReport, ratedPositive);

        editor.commit();
    }


    public void upVote(final ImageButton button) {
        oldPoint = report.getPoints();
        int newPoint = 0;


        if(button.equals(reportDetailImgbtnUpvote)){
            //if you clicked the upvote button, add 1 point, then color the image
            newPoint = oldPoint + 1;
            if(newPoint - oldPoint == 1) {
                setImageVote(button, R.drawable.likeyes);
                setRated("ratedPositive",true);
                myRef.child(idReport).child("points").setValue(newPoint);

                reportDetailImgbtnUpvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        annullaVotoPositivo(reportDetailImgbtnUpvote, R.drawable.likeno);
                    }
                });
            }
        }
    }

    public void downVote(ImageButton button){
        oldPoint = report.getPoints();

            int newPoint = oldPoint - 1;

            setImageVote(button, R.drawable.dislikeyes);
            setRated("ratedNegative",true);

            myRef.child(idReport).child("points").setValue(newPoint);

            reportDetailImgbtnDownvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    annullaVotoNegativo(reportDetailImgbtnDownvote, R.drawable.dislikeno);
                }
            });
    }

    public void setImageVote(ImageButton button, int draw){
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(draw)
                + '/' + getResources().getResourceTypeName(draw) + '/' + getResources().getResourceEntryName(draw));
        button.setImageURI(imageUri);
    }



    public void annullaVotoPositivo(final ImageButton button, int draw) {
        oldPoint = report.getPoints();
        setImageVote(button,draw);
        int newPoint = oldPoint - 1;
        myRef.child(idReport).child("points").setValue(newPoint);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upVote(button);
            }
        });

        setRated("ratedPositive", false);
    }


    public void annullaVotoNegativo(final ImageButton button, int draw){
        oldPoint = report.getPoints();
        setImageVote(button,draw);
        int newPoint = oldPoint + 1;
        myRef.child(idReport).child("points").setValue(newPoint);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downVote(button);
            }
        });

        setRated("ratedNegative", false);
    }

    @OnClick({R.id.reportDetail_imgbtn_upvote, R.id.reportDetail_imgbtn_downvote, R.id.reportdetail_btn_publishcomment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reportDetail_imgbtn_upvote:
                upVote(reportDetailImgbtnUpvote);
                getUpdatedPoint();
                break;
            case R.id.reportDetail_imgbtn_downvote:
                downVote(reportDetailImgbtnDownvote);
                getUpdatedPoint();
                break;
            case R.id.reportdetail_btn_publishcomment:
                break;
        }
    }
}
