package com.example.maola.degradotourmap.Adapter;

/**
 * Created by Maola on 03/10/2017.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.maola.degradotourmap.R;
import com.google.firebase.database.Query;

import java.text.DateFormat;


public class CommentAdapter extends FirebaseRecyclerAdapter {
    private String mData;
    private Context ctx;

    public CommentAdapter(Query query) {
        super(query);
    }


    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ctx = parent.getContext();
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_layout, parent, false);

        // create ViewHolder

        ViewHolder viewHolder = new CommentAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DateFormat df = DateFormat.getDateTimeInstance();

        //Qui definisco i dati che verranno caricati

//        viewHolder.commentTxt.setText(mData
//                .get(position)
//                .getTitoloRicettaSalvata());
////        viewHolder.textViewDataSalvataggio.setText("Salvato il "+ format1.format(mData.get(position).getDatetime()));
//        viewHolder.textViewDataSalvataggio.setText(ctx.getResources().getString(R.string.salvata_il)+ df.format(mData.get(position).getDatetime()));
//
//        viewHolder.idRicettaSalvata= mData.get(position).getIdSalvate();

    }



    @Override
    public int getItemCount() {
        return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView commentTxt;
        public ImageView imgPhotoProfile;
        public ImageView imgBtnDelete;
        public Long idRicettaSalvata;
        public View viewParent;



        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            commentTxt = (TextView) itemLayoutView.findViewById(R.id.comment_it_txt_body);
            imgPhotoProfile = (ImageView) itemLayoutView.findViewById(R.id.comment_it_img_profile);
            imgBtnDelete = (ImageView) itemLayoutView.findViewById(R.id.comment_it_img_delete);



//            viewParent = (View) itemLayoutView.findViewById(R.id.ricette_default_parent);
//            viewParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            AppCompatActivity activity = (AppCompatActivity) v.getContext();


//            Bundle bundle = new Bundle();
//            Ricettario_salvate myFragment = new Ricettario_salvate();
//            bundle.putLong("idRicettaSalvata", idRicettaSalvata); // Put anything what you want
//
//            //Toast.makeText(v.getContext(), "You have clicked " + v.getId() + "id " + categoriaIdLong, Toast.LENGTH_SHORT).show();
//            //you can add data to the tag of your cardview in onBind... and retrieve it here with with.getTag().toString()..
//            myFragment.setArguments(bundle);



//            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContent, myFragment).addToBackStack(null).commit();

        }

        public void setRicettaId(long id) {
            this.idRicettaSalvata = id;
        }

    }
}

