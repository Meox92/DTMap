//package com.example.maola.degradotourmap.MapUtils;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.widget.ImageButton;
//
//import com.example.maola.degradotourmap.Model.Report;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.Query;
//
//import java.util.List;
//
///**
// * Created by Maola on 24/09/2017.
// */
//
//public class VoteUtils {
//
//
//    private int oldPoint = 0;
//    private int newPoint = 0;
//    private SharedPreferences sharedPreferences;
//    private String SHARED_PREF = "PREF_LOGIN";
//    private String idReport;
//    private DatabaseReference myRef;
//    private Report report;
//    private Context ctx;
//
//
//    public void setRated(String TAG, boolean ratedPositive) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(TAG + idReport, ratedPositive);
//
//        editor.commit();
//    }
//
//    public void setImageVote(ImageButton button, int draw, Context ctx){
//        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                "://" + ctx.getResources().getResourcePackageName(draw)
//                + '/' + ctx.getResources().getResourceTypeName(draw) + '/' + ctx.getResources().getResourceEntryName(draw));
//        button.setImageURI(imageUri);
//    }
//
//
//
//    public void upVote(final ImageButton button) {
//        //Prende il valore del punteggio attuale dal DB
//        oldPoint = report.getPoints();
//
//        //Aggiungi un punto
//        newPoint = oldPoint + 1;
//
//        //metodo che rende l'immagine colorata dopo averci cliccato(stile like di facebook)
//        setImageVote(button, R.drawable.likeyes);
//
//        //Salva nelle Shared Preferences che si è messo un like
//        setRated("ratedPositive",true);
//
//        //Inserisci il nuovo punteggio nel DB
//        myRef.child(idReport).child("points").setValue(newPoint);
//
//        //Cliccando di nuovo sul bottone, è possibile annullare il proprio voto, l'icona torna in bianco e nero
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                annullaVotoPositivo(reportDetailImgbtnUpvote, R.drawable.likeno);
//            }
//        });}
//}
//
//
//    //Speculare al metodo upvote
//    public void downVote(ImageButton button){
//        oldPoint = report.getPoints();
//        int newPoint = 0;
//
//        if(button.equals(button)){
//            newPoint = oldPoint - 1;
//
//            setImageVote(button, R.drawable.dislikeyes);
//            setRated("ratedNegative",true);
//
//            myRef.child(idReport).child("points").setValue(newPoint);
//
//            reportDetailImgbtnDownvote.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    annullaVotoNegativo(reportDetailImgbtnDownvote, R.drawable.dislikeno);
//                }
//            });
//
//
//        }
//    }
//
//    //Metodo per annullare il voto, al punteggio viene tolto un punto,
//    //l'icona torna in bianco e nero
//    public void annullaVotoNegativo(final ImageButton button, int draw){
//        //recupera il punteggio dal DB
//        oldPoint = report.getPoints();
//
//        //Reimposta l'icona in bianco e nero
//        setImageVote(button,draw);
//
//        //esegui l'annullamento del dislike, aumenta il punteggio di 1
//        int newPoint = oldPoint + 1;
//
//        //inserisci nel DB il nuovo valore
//        myRef.child(idReport).child("points").setValue(newPoint);
//
//        //Viene reimpostato il metodo per il dislike
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downVote(button);
//            }
//        });
//        //inserisci nelle sharedPreferences che non è stato effettuato il downvote
//        setRated("ratedNegative", false);
//    }
//
//
//
//    //Speculare a annullaVotoPositivo
//    public void annullaVotoPositivo(final ImageButton button, int draw) {
//        oldPoint = report.getPoints();
//        setImageVote(button,draw);
//        int newPoint = oldPoint - 1;
//        myRef.child(idReport).child("points").setValue(newPoint);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                upVote(button);
//            }
//        });
//
//        setRated("ratedPositive", false);
//    }
//
//
//}
