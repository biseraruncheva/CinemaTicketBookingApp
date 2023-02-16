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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AdminHome extends AppCompatActivity {
    SQLiteDatabase db;
    String usern;
    RecyclerView mRecyclerView;
    myAdapter mAdapter;

    Cursor c1;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Intent intent = getIntent();
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        //c1 = db.rawQuery("SELECT * FROM movie", null);

        usern = intent.getStringExtra("username");
        NavigationView NV = findViewById(R.id.navview);
        View HV = NV.getHeaderView(0);
        TextView tv = (TextView) HV.findViewById(R.id.navname);
        tv.setText(usern);
        TextView tv1 = (TextView) HV.findViewById(R.id.navrole);
        tv1.setText("ADMIN");


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

        List<String> titles = new ArrayList<String>();
        List<Bitmap> images = new ArrayList<Bitmap>();
        List<String> names = new ArrayList<String>();
        List<String> release_dates = new ArrayList<String>();
        List<String> genres = new ArrayList<String>();
        List<String> desc = new ArrayList<String>();


        Spinner s1 = findViewById(R.id.filtermovies);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position){
                    case 0:
                        c1 = db.rawQuery("SELECT * FROM movie", null);

                        break;
                    case 1:
                        //c1 = db.rawQuery("SELECT * FROM movie WHERE ReleaseDate > strftime('%d-%m-%Y', 'now') ", null);
                        c1 = db.rawQuery("SELECT * FROM movie WHERE ReleaseDate > strftime('%s', 'now') * 1000", null);
                        break;
                    case 2:
                        //c1 = db.rawQuery("SELECT * FROM movie, screening WHERE movie.mid = screening.mid ", null); //TUKA TREBA DA SMENIS I KAJ USER, TREBA DISTINCT
                        //c1 = db.rawQuery("SELECT DISTINCT movie.mid, movie.title, movie.genre, movie.duration_in_minutes, movie.ReleaseDate, movie.picture, movie.synopsis FROM movie, screening WHERE movie.mid = screening.mid AND screening.ScreeningTime > strftime('%d-%m-%Y %H:%M', datetime('now'))", null); //TUKA TREBA DA SMENIS I KAJ USER, TREBA DISTINCT
                        c1 = db.rawQuery("SELECT DISTINCT movie.mid, movie.title, movie.genre, movie.duration_in_minutes, movie.ReleaseDate, movie.picture, movie.synopsis FROM movie, screening WHERE movie.mid = screening.mid AND datetime(ScreeningTime / 1000, 'unixepoch') > datetime('now')", null);
                        break;

                }

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
                        //String uri = (c1.getString(6));
                        //images.add(uri);
                        images.add(bitmap);
                        //Date date = new Date(c1.getLong(5));
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        //String dateString = dateFormat.format(date);
                        long releaseDateTimestamp = c1.getLong(4);

// Convert the timestamp to a Date object
                        Date releaseDate = new Date(releaseDateTimestamp);

// Convert the Date object to a String in the format "dd-MM-yyyy"
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String releaseDateString = dateFormat.format(releaseDate);
                        release_dates.add(releaseDateString);

                        //release_dates.add(c1.getString(4));
                        //desc.add(c1.getString(7));

                        i++;
                        c1.moveToPosition(i);
                    }while (i<rows);
                    c1.close();
                }
                mAdapter.notifyDataSetChanged();

            }


            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
        mAdapter = new myAdapter(titles, images, release_dates, genres, usern, "Admin", R.layout.my_movie_row, this);
        //прикачување на адаптерот на RecyclerView
        mRecyclerView.setAdapter(mAdapter);


        NV.getMenu().getItem(0).setChecked(true);
        NV.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch(item.getItemId()){
                            case R.id.nav_logout:
                                Intent intent = null;
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                                Toast.makeText(getApplicationContext(),"You've logged out",Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                break;
                            case R.id.nav_home:
                                finish();
                                startActivity(getIntent());
                                break;
                            case R.id.nav_add_movie:
                                Intent intent1 = null;
                                intent1 = new Intent(getApplicationContext(), AddMovie.class);
                                intent1.putExtra("username", usern);
                                startActivity(intent1);
                                break;
                            case R.id.nav_add_auditorium:
                                Intent intent2 = null;
                                intent2 = new Intent(getApplicationContext(), AddAuditorium.class);
                                intent2.putExtra("username", usern);
                                startActivity(intent2);
                                break;
                            case R.id.nav_add_screening:
                                Intent intent3 = null;
                                intent3 = new Intent(getApplicationContext(), AddScreening.class);
                                intent3.putExtra("username", usern);
                                startActivity(intent3);
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