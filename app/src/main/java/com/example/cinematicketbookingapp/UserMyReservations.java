package com.example.cinematicketbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMyReservations extends AppCompatActivity {
    SQLiteDatabase db;
    String usern;
    RecyclerView mRecyclerView;
    myReservationAdapter mAdapter;

    Cursor c1;
    List<String> titles = new ArrayList<String>();
    List<String> names = new ArrayList<String>();
    List<String> release_dates = new ArrayList<String>();
    List<String> dates = new ArrayList<String>();
    List<String> times = new ArrayList<String>();
    List<String> seats = new ArrayList<String>();
    List<String> prices = new ArrayList<String>();


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_reservations);
        Intent intent = getIntent();
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);

        usern = intent.getStringExtra("username");
        NavigationView NV = findViewById(R.id.navviewuser);
        View HV = NV.getHeaderView(0);
        TextView tv = (TextView) HV.findViewById(R.id.navname);
        tv.setText(usern);
        TextView tv1 = (TextView) HV.findViewById(R.id.navrole);
        tv1.setText("USER");

        Cursor c = db.rawQuery("SELECT user.uid from user WHERE user.username = '"+usern+"'",null);
        c.moveToFirst();
        int UID = c.getInt(0);
        c.close();

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout_user);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //c1 = db.rawQuery("SELECT * FROM movie, screening WHERE movie.mid = screening.mid ", null);
        c1 = db.rawQuery("SELECT reservation.sid, reservation.uid, movie.title, auditorium.name, screening.ScreeningTime, reservation.Seats, screening.PricePerSeat, reservation.NumOfSeats FROM movie, auditorium, screening, reservation, user WHERE movie.mid = screening.mid AND auditorium.aid = screening.aid AND reservation.sid = screening.sid AND reservation.uid = user.uid AND user.uid = '"+UID+"'", null); //TUKA TREBA DA SMENIS I KAJ USER, TREBA DISTINCT
        int rows = c1.getCount();
        int i=0;
        if(c1.moveToPosition(0)){

            do{
                titles.add(c1.getString(2));
                names.add(c1.getString(3));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                Date d = new Date(c1.getLong(4));
                SimpleDateFormat dateFormatOnly = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat timeFormatOnly = new SimpleDateFormat("HH:mm");
                String dateOnly = dateFormatOnly.format(d);
                String timeOnly = timeFormatOnly.format(d);
                dates.add(dateOnly);
                times.add(timeOnly);
                seats.add(c1.getString(5));
                prices.add(String.valueOf(c1.getInt(7)*c1.getInt(6)));

                i++;
                c1.moveToPosition(i);
            }while (i<rows);
            c1.close();
        }

        //сетирање на RecyclerView контејнерот
        mRecyclerView = (RecyclerView) findViewById(R.id.listReservations);
        // оваа карактеристика може да се користи ако се знае дека промените
        // во содржината нема да ја сменат layout големината на RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // ќе користиме LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // и default animator (без анимации)
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // сетирање на кориснички дефиниран адаптер myAdapter (посебна класа)
        mAdapter = new myReservationAdapter(titles, names, dates, times, seats, prices, R.layout.my_reservations_row, this);
        //прикачување на адаптерот на RecyclerView
        mRecyclerView.setAdapter(mAdapter);


        NV.getMenu().getItem(1).setChecked(true);
        NV.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_logout_user:
                        Intent intent = null;
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(),"You've logged out",Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        break;
                    case R.id.nav_home_user:
                        Intent intent1 = null;
                        intent1 = new Intent(getApplicationContext(), UserHome.class);
                        intent1.putExtra("username", usern);
                        startActivity(intent1);
                        break;
                    case R.id.nav_my_tickets:
                        finish();
                        startActivity(getIntent());
                        break;

                }

                return false;
            }
        });
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}