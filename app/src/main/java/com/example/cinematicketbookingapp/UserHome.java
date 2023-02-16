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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserHome extends AppCompatActivity {
    SQLiteDatabase db;
    String usern;
    RecyclerView mRecyclerView;
    myAdapter mAdapter;

    Cursor c1;
    List<String> titles = new ArrayList<String>();
    List<Bitmap> images = new ArrayList<Bitmap>();
    List<String> names = new ArrayList<String>();
    List<String> release_dates = new ArrayList<String>();
    List<String> genres = new ArrayList<String>();
    List<String> desc = new ArrayList<String>();


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        Intent intent = getIntent();
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);

        usern = intent.getStringExtra("username");
        NavigationView NV = findViewById(R.id.navviewuser);
        View HV = NV.getHeaderView(0);
        TextView tv = (TextView) HV.findViewById(R.id.navname);
        tv.setText(usern);
        TextView tv1 = (TextView) HV.findViewById(R.id.navrole);
        tv1.setText("USER");


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



        c1 = db.rawQuery("SELECT DISTINCT movie.mid, movie.title, movie.genre, movie.duration_in_minutes, movie.ReleaseDate, movie.picture, movie.synopsis FROM movie, screening WHERE movie.mid = screening.mid AND datetime(ScreeningTime / 1000, 'unixepoch') > datetime('now')", null);
        int rows = c1.getCount();
        int i=0;
        titles.clear();
        images.clear();
        desc.clear();
        release_dates.clear();
        genres.clear();
        if(c1.moveToPosition(0)){

            do{
                titles.add(c1.getString(1));
                genres.add(c1.getString(2));
                byte[] movieImage = c1.getBlob(5);
                Bitmap bitmap = BitmapFactory.decodeByteArray(movieImage, 0, movieImage.length);

                images.add(bitmap);

                long releaseDateTimestamp = c1.getLong(4);

// Convert the timestamp to a Date object
                Date releaseDate = new Date(releaseDateTimestamp);

// Convert the Date object to a String in the format "dd-MM-yyyy"
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String releaseDateString = dateFormat.format(releaseDate);
                release_dates.add(releaseDateString);


                i++;
                c1.moveToPosition(i);
            }while (i<rows);
            c1.close();
        }

        //сетирање на RecyclerView контејнерот
        mRecyclerView = (RecyclerView) findViewById(R.id.listMovies);
        // оваа карактеристика може да се користи ако се знае дека промените
        // во содржината нема да ја сменат layout големината на RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // ќе користиме LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // и default animator (без анимации)
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // сетирање на кориснички дефиниран адаптер myAdapter (посебна класа)
        mAdapter = new myAdapter(titles, images, release_dates, genres, usern, "User", R.layout.my_movie_row, this);
        //прикачување на адаптерот на RecyclerView
        mRecyclerView.setAdapter(mAdapter);


        NV.getMenu().getItem(0).setChecked(true);
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
                        finish();
                        startActivity(getIntent());
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
