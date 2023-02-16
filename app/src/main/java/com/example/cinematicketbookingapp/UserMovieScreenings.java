package com.example.cinematicketbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMovieScreenings extends AppCompatActivity {
    SQLiteDatabase db;
    String title;
    ImageView moviepic;
    TextView movietitle;
    TextView duration;
    TextView genres;
    TextView date;
    TextView synopsis;
    RecyclerView mRecyclerView;
    myScreeningAdapter mAdapter;
    Integer MID;
    String usern;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_movie_screenings);
        Intent intent = getIntent();
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        title = intent.getStringExtra("movietitle");
        usern = intent.getStringExtra("username");
        moviepic = findViewById(R.id.userdetailspic);
        movietitle = findViewById(R.id.usermovieName);
        duration = findViewById(R.id.usermovieduration);
        genres = findViewById(R.id.userdetailsgenre);
        date = findViewById(R.id.userreleasedate);
        synopsis = findViewById(R.id.userdetailssynopsis);

        NavigationView NV = findViewById(R.id.navviewuser);
        View HV = NV.getHeaderView(0);
        TextView tv = (TextView) HV.findViewById(R.id.navname);
        tv.setText(usern);
        TextView tv1 = (TextView) HV.findViewById(R.id.navrole);
        tv1.setText("USER");


        Cursor c1;
        c1 = db.rawQuery("SELECT * FROM movie WHERE movie.title ='"+title+"'",null);
        int rows = c1.getCount();
        if(rows == 1){
            c1.moveToFirst();
            MID = c1.getInt(0);
            movietitle.setText(title);
            duration.setText("Duration: "+c1.getString(3)+" minutes");
            genres.setText("Genres: "+c1.getString(2));
            synopsis.setText("Synopsis:\n"+c1.getString(6));
            synopsis.setMovementMethod(new ScrollingMovementMethod());
            long releaseDateTimestamp = c1.getLong(4);

// Convert the timestamp to a Date object
            Date releaseDate = new Date(releaseDateTimestamp);

// Convert the Date object to a String in the format "dd-MM-yyyy"
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String releaseDateString = dateFormat.format(releaseDate);
            date.setText("Release Date: "+releaseDateString);
            byte[] movieImage = c1.getBlob(5);
            Bitmap bitmap = BitmapFactory.decodeByteArray(movieImage, 0, movieImage.length);
            moviepic.setImageBitmap(bitmap);
        }
        c1.close();
        List<String> names = new ArrayList<String>();
        List<String> dates = new ArrayList<String>();
        List<String> times = new ArrayList<String>();
        List<Integer> SIDs = new ArrayList<Integer>();



        c1 = db.rawQuery("SELECT * FROM screening, movie, auditorium WHERE movie.mid = screening.mid AND auditorium.aid = screening.aid AND datetime(ScreeningTime / 1000, 'unixepoch') > datetime('now') AND movie.mid = '"+MID+"' ", null);
        int rows1 = c1.getCount();
        int i=0;
        dates.clear();
        times.clear();
        names.clear();

        if(c1.moveToFirst()){

            do{


                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                Date d = new Date(c1.getLong(3));
                names.add(c1.getString(14));
                SimpleDateFormat dateFormatOnly = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat timeFormatOnly = new SimpleDateFormat("HH:mm");
                String dateOnly = dateFormatOnly.format(d);
                String timeOnly = timeFormatOnly.format(d);
                dates.add(dateOnly);
                times.add(timeOnly);
                SIDs.add(c1.getInt(0));

                i++;
                c1.moveToPosition(i);
            }while (i<rows1);
            c1.close();
        }

        //сетирање на RecyclerView контејнерот
        mRecyclerView = (RecyclerView) findViewById(R.id.listScreenings);
        // оваа карактеристика може да се користи ако се знае дека промените
        // во содржината нема да ја сменат layout големината на RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // ќе користиме LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // и default animator (без анимации)
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // сетирање на кориснички дефиниран адаптер myAdapter (посебна класа)
        mAdapter = new myScreeningAdapter(names, dates, times, usern, SIDs, R.layout.my_screening_row, this);
        //прикачување на адаптерот на RecyclerView
        mRecyclerView.setAdapter(mAdapter);


        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        Intent intent2 = null;
                        intent2 = new Intent(getApplicationContext(), UserHome.class);
                        intent2.putExtra("username", usern);
                        startActivity(intent2);
                        break;
                    case R.id.nav_my_tickets:
                        Intent intent1 = null;
                        intent1 = new Intent(getApplicationContext(), UserMyReservations.class);
                        intent1.putExtra("username", usern);
                        startActivity(intent1);
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
