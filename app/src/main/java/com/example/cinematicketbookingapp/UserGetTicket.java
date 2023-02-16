package com.example.cinematicketbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserGetTicket extends AppCompatActivity {
    SQLiteDatabase db;
    int SID;
    String usern;
    int available_seats;
    int priceperticket = 10;
    int seatingcapacity = 0;

    List<String> helplist = new ArrayList<String>();
    List<String> not_available_seats = new ArrayList<String>();
    List<String> picked_seats = new ArrayList<String>();


    double lat;
    double log;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_get_ticket);
        Intent intent = getIntent();
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        usern = intent.getStringExtra("username");
        SID = intent.getIntExtra("screeningid",0);
        NavigationView NV = findViewById(R.id.navview);
        View HV = NV.getHeaderView(0);
        TextView tv = (TextView) HV.findViewById(R.id.navname);
        tv.setText(usern);
        TextView tv1 = (TextView) HV.findViewById(R.id.navrole);
        tv1.setText("USER");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        Cursor c1 = db.rawQuery("SELECT screening.AvailableSeats, screening.PricePerSeat FROM screening WHERE screening.sid = '"+SID+"'",null);


        c1.moveToFirst();
        available_seats = c1.getInt(0);
        priceperticket = c1.getInt(1);
        c1.close();
        Cursor c2 = db.rawQuery("SELECT reservation.seats from reservation WHERE  reservation.sid = '"+SID+"'",null);
        int resultrowsrows = c2.getCount();
        int r = 0;
            if(c2.moveToFirst()){
                do{

                    String rs = c2.getString(0);
                    helplist = Arrays.asList(rs.split(", "));
                    not_available_seats.addAll(List.copyOf(helplist));
                    r++;
                    c2.moveToPosition(r);
                }while(r < resultrowsrows);
            }
            c2.close();

        Cursor c3 = db.rawQuery("SELECT auditorium.SeatingCapacity FROM auditorium, screening WHERE auditorium.aid = screening.aid AND screening.sid = '"+SID+"'",null);
        c3.moveToFirst();
        seatingcapacity = c3.getInt(0);
        c3.close();
        GenerateSeatingLayout(seatingcapacity);

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

        Button btn = findViewById(R.id.reservebtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(picked_seats.size() == 0) {

                    Toast.makeText(getApplicationContext(),"Please pick seats before submitting",Toast.LENGTH_LONG).show();


                } else {
                    Cursor c4 = db.rawQuery("UPDATE screening SET AvailableSeats = '" + (available_seats - picked_seats.size()) + "' WHERE sid = '" + SID + "'", null);
                    c4.moveToFirst();
                    c4.close();
                    c4 = db.rawQuery("SELECT user.uid FROM user WHERE user.username = '" + usern + "'", null);
                    c4.moveToFirst();
                    ContentValues values = new ContentValues();
                    values.put("uid", c4.getInt(0));
                    values.put("sid", SID);
                    values.put("NumOfSeats", picked_seats.size());
                    values.put("Seats", String.join(", ", picked_seats));
                    values.put("latitude", lat);
                    values.put("longitude", log);

                    long rowId = db.insert("reservation", null, values);

                    Intent intent2 = null;
                    intent2 = new Intent(getApplicationContext(), UserHome.class);
                    intent2.putExtra("username", usern);
                    Toast.makeText(getApplicationContext(),"Your reservation was successful",Toast.LENGTH_LONG).show();
                    startActivity(intent2);
                }
            }
        });


    }

    private void GenerateSeatingLayout(int totalseats) {
        GridLayout theaterLayout = findViewById(R.id.seatinglayout);
        TextView totalprice = findViewById(R.id.total_price);
        TextView calculation = findViewById(R.id.price_calculation);
        calculation.setText(String.valueOf("0 x "+String.valueOf(priceperticket)+"$"));
        totalprice.setText(String.valueOf(0*priceperticket)+"$");
        int column = 0;
        int row = 0;

        int numberOfRows;
        int numberOfColumns;
        numberOfColumns = 5;


        numberOfRows = (totalseats +(numberOfColumns - 1))/numberOfColumns;
        theaterLayout.setColumnCount(numberOfColumns);
        theaterLayout.setRowCount(numberOfRows);

        for(int i=0; i<numberOfRows; i++) {
            for(int j=0; j<numberOfColumns; j++){
                ImageButton seat = new ImageButton(this);
                seat.setId(i*numberOfColumns + j);

                GridLayout.LayoutParams seatParams = new GridLayout.LayoutParams();
                seatParams.width = 90;
                seatParams.height = 90;
                seat.setLayoutParams(seatParams);

                ((GridLayout.LayoutParams) seat.getLayoutParams()).columnSpec = GridLayout.spec(j,1f);
                ((GridLayout.LayoutParams) seat.getLayoutParams()).rowSpec = GridLayout.spec(i, 1f);

                seat.setScaleX(1.05f);
                seat.setScaleY(1.05f);

                if(not_available_seats.contains(String.valueOf(i*numberOfColumns + j))) {
                    seat.setBackgroundResource(R.drawable.seat_not_available);
                    seat.setTag("not available");
                } else {
                    seat.setBackgroundResource(R.drawable.seat_available);
                }
                seat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        if (view.getTag() == null || view.getTag().equals("available")) {
                            picked_seats.add(String.valueOf(id));
                            view.setBackgroundResource(R.drawable.seat_picked);
                            view.setTag("selected");
                            calculation.setText(String.valueOf(picked_seats.size())+" x "+String.valueOf(priceperticket)+"$");
                            totalprice.setText(String.valueOf(picked_seats.size()*priceperticket)+"$");
                            Toast.makeText(UserGetTicket.this, "Seat number: "+String.valueOf(id), Toast.LENGTH_SHORT).show();
                        } else if(view.getTag().equals("selected")){
                            picked_seats.remove(String.valueOf(id));
                            view.setBackgroundResource(R.drawable.seat_available);
                            view.setTag("available");
                            calculation.setText(String.valueOf(picked_seats.size())+" x "+String.valueOf(priceperticket)+"$");
                            totalprice.setText(String.valueOf(picked_seats.size()*priceperticket)+"$");
                        }
                    }
                });
                theaterLayout.addView(seat);

            }
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


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = location.getLatitude();
                            log = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(UserGetTicket.this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            lat = mLastLocation.getLatitude();
            log = mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

}
