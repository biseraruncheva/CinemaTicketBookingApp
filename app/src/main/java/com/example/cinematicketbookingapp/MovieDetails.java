package com.example.cinematicketbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieDetails extends AppCompatActivity {

    SQLiteDatabase db;
    String title;
    String usern;
    Integer MID;
    ImageView moviepic;
    TextView movietitle;
    TextView duration;
    TextView genres;
    TextView date;
    TextView synopsis;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        title = intent.getStringExtra("movietitle");
        usern = intent.getStringExtra("username");
        moviepic = findViewById(R.id.detailspic);
        movietitle = findViewById(R.id.Name);
        duration = findViewById(R.id.movieduration);
        genres = findViewById(R.id.detailsgenre);
        date = findViewById(R.id.releasedate);
        synopsis = findViewById(R.id.detailssynopsis);

        NavigationView NV = findViewById(R.id.navview);
        View HV = NV.getHeaderView(0);
        TextView tv = (TextView) HV.findViewById(R.id.navname);
        tv.setText(usern);
        TextView tv1 = (TextView) HV.findViewById(R.id.navrole);
        tv1.setText("ADMIN");

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

        ScrollView scrollview = findViewById(R.id.scrollView2);
        scrollview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                synopsis.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        synopsis.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                synopsis.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });


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
                    case R.id.nav_logout:
                        Intent intent = null;
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(),"You've logged out",Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        break;
                    case R.id.nav_home:
                        Intent intent4 = null;
                        intent4 = new Intent(getApplicationContext(), AdminHome.class);
                        intent4.putExtra("username", usern);
                        startActivity(intent4);
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

        Cursor cc = db.rawQuery("SELECT auditorium.name, screening.ScreeningTime, SUM(reservation.NumOfSeats) FROM auditorium, screening, reservation, movie WHERE auditorium.aid = screening.aid AND screening.sid = reservation.sid AND screening.mid = movie.mid AND movie.mid = '"+MID+"' GROUP BY screening.sid",null);
        int cursorrows = cc.getCount();
        if (cursorrows > 0) {

            HorizontalBarChart horizontalBarChart = (HorizontalBarChart) findViewById(R.id.barchart);

// Disable description text
            horizontalBarChart.getDescription().setEnabled(false);

// Disable right axis
            horizontalBarChart.getAxisRight().setEnabled(false);

// Enable touch gestures
            horizontalBarChart.setTouchEnabled(true);

// Enable scaling and dragging
            horizontalBarChart.setDragEnabled(true);
            horizontalBarChart.setScaleEnabled(true);

// Add data to the chart
            final ArrayList<BarEntry> entries = new ArrayList<>();
            final ArrayList<String> xAxisLabels = new ArrayList<>();

            int j=0;
            if(cc.moveToFirst()){
                do{
                    entries.add(new BarEntry(j,(float) cc.getInt(2)));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date d = new Date(cc.getLong(1));
                    SimpleDateFormat datetimeFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String datetime = datetimeFormat.format(d);
                    xAxisLabels.add(cc.getString(0)+"\n"+datetime);

                    j++;
                    cc.moveToPosition(j);
                }while(j < cursorrows);
            }
            cc.close();
            BarDataSet dataSet = new BarDataSet(entries, "Number of participants");
            dataSet.setValueTextColor(Color.WHITE);
            BarData data = new BarData(dataSet);
            data.setBarWidth(0.5f); // set custom bar width


            ArrayList<Integer> colors = new ArrayList<>();

            for (int color: ColorTemplate.MATERIAL_COLORS) {
                colors.add(color);
            }

            for (int color: ColorTemplate.VORDIPLOM_COLORS) {
                colors.add(color);
            }

            dataSet.setColors(colors);

            data.setBarWidth(0.9f); // set custom bar width

            horizontalBarChart.setData(data);

// Specify the x-axis labels
            XAxis xAxis = horizontalBarChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setTextColor(Color.WHITE);

// Specify the y-axis labels
            Cursor cc1 = db.rawQuery("SELECT MAX(auditorium.SeatingCapacity) FROM auditorium, screening, movie WHERE screening.aid = auditorium.aid AND screening.mid = movie.mid AND movie.mid = '"+MID+"'",null);
            cc1.moveToFirst();
            YAxis yAxis = horizontalBarChart.getAxisLeft();
            yAxis.setAxisMinimum(0f); // start at zero
            yAxis.setAxisMaximum((float) cc1.getInt(0)); // the y-axis range
            yAxis.setTextColor(Color.WHITE);

// Update the chart
            horizontalBarChart.invalidate();
        } else {
            cc.close();
        }



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


    public void ShowMap(View view) {
        Intent intent = null;
        intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("username", usern);
        intent.putExtra("movieid", MID);
        startActivity(intent);
    }
}
