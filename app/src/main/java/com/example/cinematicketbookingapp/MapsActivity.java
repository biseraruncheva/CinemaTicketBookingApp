package com.example.cinematicketbookingapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cinematicketbookingapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Cursor c;
    String usern;
    int MID;
    SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        usern = intent.getStringExtra("username");
        MID = intent.getIntExtra("movieid" ,0);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        c = db.rawQuery("SELECT reservation.latitude, reservation.longitude, screening.sid, user.uid FROM reservation, screening, movie, user WHERE movie.mid = screening.mid AND reservation.sid = screening.sid AND reservation.uid = user.uid AND movie.mid = '"+MID+"'",null);
        int i=0;
        int rows = c.getCount();
        if(c.moveToFirst()){
            do{
                LatLng loc = new LatLng(c.getDouble(0), c.getDouble(1));
                mMap.addMarker(new MarkerOptions().position(loc).title("Screening ID: "+String.valueOf(c.getInt(2))+" , User ID: "+c.getInt(3)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                i++;
                c.moveToPosition(i);
            }while(i<rows);
            c.close();
        }

    }
}