package com.example.cinematicketbookingapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {
    private List<String> myDate;
    private List<String> myList;
    private List<Bitmap> myPics;
    private List<String> myGenres;
    private String myUsername;
    private String myRole;


    private int rowLayout;
    private Context mContext;
    // Референца на views за секој податок
// Комплексни податоци може да бараат повеќе views per item
// Пристап до сите views за податок се дефинира во view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public ImageView Pic;
        public TextView myDate;
        public TextView myGenres;
        public TextView myRow;
        public ViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            myDate = (TextView) itemView.findViewById(R.id.releasedate);
            Pic = (ImageView) itemView.findViewById(R.id.detailspic);
            myGenres = (TextView) itemView.findViewById(R.id.detailsgenre);
            myRow = (TextView) itemView.findViewById(R.id.clickablemovierow);


        }
    }
    // конструктор
    public myAdapter(List<String> myList1, List<Bitmap> myList2, List<String> myList3, List<String> myList4, String username, String role, int rowLayout, Context context) {
        this.myList = myList1;
        this.myPics = myList2;
        this.myDate = myList3;
        this.myGenres = myList4;
        this.myUsername = username;
        this.myRole = role;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }
    // Креирање нови views (повикано од layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }
    // Замена на содржината во view (повикано од layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String entry = myList.get(i);
        Bitmap pic = myPics.get(i);
        String date = myDate.get(i);
        String genres = myGenres.get(i);
        viewHolder.myName.setText(entry);
        viewHolder.myDate.setText("Release Date: "+date);
        viewHolder.myGenres.setText("Genres: "+genres);
        viewHolder.Pic.setImageBitmap(pic);

        viewHolder.myRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myRole.equals("Admin")) {
                    Intent intent = new Intent(mContext, MovieDetails.class);
                    intent.putExtra("movietitle", entry);
                    intent.putExtra("username", myUsername);

                    mContext.startActivity(intent);
                } else if(myRole.equals("User")) {
                    Intent intent = new Intent(mContext, UserMovieScreenings.class);
                    intent.putExtra("movietitle", entry);
                    intent.putExtra("username", myUsername);

                    mContext.startActivity(intent);
                }
            }
        });

    }
    // Пресметка на големината на податочното множество (повикано од
    //layout manager)
    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
