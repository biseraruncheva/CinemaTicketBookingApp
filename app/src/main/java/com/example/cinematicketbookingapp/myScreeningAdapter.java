package com.example.cinematicketbookingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myScreeningAdapter extends RecyclerView.Adapter<myScreeningAdapter.ViewHolder> {
    private List<String> myAuditorium;
    private List<String> myDate;
    private List<String> myTime;
    private String myUsername;
    private List<Integer> myScreeningID;

    private int rowLayout;
    private Context mContext;
    // Референца на views за секој податок
// Комплексни податоци може да бараат повеќе views per item
// Пристап до сите views за податок се дефинира во view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myAuditorium;
        public TextView myDate;
        public TextView myTime;
        public Button BuyTickets;
        public ViewHolder(View itemView) {
            super(itemView);
            myAuditorium = (TextView) itemView.findViewById(R.id.userauditorium);
            myDate = (TextView) itemView.findViewById(R.id.datescreening);
            myTime = (TextView) itemView.findViewById(R.id.timescreening);
            BuyTickets = (Button) itemView.findViewById(R.id.buyticketsbtn);

        }
    }
    // конструктор
    public myScreeningAdapter(List<String> myList1, List<String> myList2, List<String> myList3, String username, List<Integer> myList4, int rowLayout, Context context) {
        this.myAuditorium = myList1;
        this.myDate = myList2;
        this.myTime = myList3;
        this.myScreeningID = myList4;
        this.myUsername = username;
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
        String auditoriumname = myAuditorium.get(i);
        String date = myDate.get(i);
        String time = myTime.get(i);
        viewHolder.myAuditorium.setText("AUDITORIUM: "+auditoriumname);
        viewHolder.myDate.setText("Date: "+date);
        viewHolder.myTime.setText("Time: "+time);
        viewHolder.BuyTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                Intent intent = new Intent(mContext,UserGetTicket.class);
                intent.putExtra("username",myUsername);
                intent.putExtra("screeningid", myScreeningID.get(i));

                mContext.startActivity(intent);


            }
        });

    }
    // Пресметка на големината на податочното множество (повикано од
    //layout manager)
    @Override
    public int getItemCount() {
        return myAuditorium == null ? 0 : myAuditorium.size();
    }
}
