package com.example.cinematicketbookingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class myReservationAdapter extends RecyclerView.Adapter<myReservationAdapter.ViewHolder> {
    private List<String> myMovie;
    private List<String> myAuditorium;
    private List<String> myDate;
    private List<String> myTime;
    private List<String> mySeat;
    private List<String> myPrice;
    private List<Integer> myScreeningID;

    private int rowLayout;
    private Context mContext;
    // Референца на views за секој податок
// Комплексни податоци може да бараат повеќе views per item
// Пристап до сите views за податок се дефинира во view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myMovie;
        public TextView myAuditorium;
        public TextView myDate;
        public TextView myTime;
        public TextView mySeat;
        public TextView myPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            myMovie = (TextView) itemView.findViewById(R.id.reservationmovie);
            myAuditorium = (TextView) itemView.findViewById(R.id.reservationuserauditorium);
            myDate = (TextView) itemView.findViewById(R.id.reservationdatescreening);
            myTime = (TextView) itemView.findViewById(R.id.reservationtimescreening);
            mySeat = (TextView) itemView.findViewById(R.id.reservedseats);
            myPrice = (TextView) itemView.findViewById(R.id.reservationprice);

        }
    }
    // конструктор
    public myReservationAdapter(List<String> myList1, List<String> myList2, List<String> myList3, List<String> myList4, List<String> myList5, List<String> myList6, int rowLayout, Context context) {

        this.myMovie = myList1;
        this.myAuditorium = myList2;
        this.myDate = myList3;
        this.myTime = myList4;
        this.mySeat = myList5;
        this.myPrice = myList6;
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
        String seats = mySeat.get(i);
        String movie = myMovie.get(i);
        String price = myPrice.get(i);

        viewHolder.myAuditorium.setText("Auditorium: "+auditoriumname);
        viewHolder.myDate.setText("Date: "+date);
        viewHolder.myTime.setText("Time: "+time);
        viewHolder.myPrice.setText("Total Price: "+price+" $");
        viewHolder.myMovie.setText(movie);
        viewHolder.mySeat.setText("Seats: "+seats);


    }
    // Пресметка на големината на податочното множество (повикано од
    //layout manager)
    @Override
    public int getItemCount() {
        return myAuditorium == null ? 0 : myAuditorium.size();
    }
}
