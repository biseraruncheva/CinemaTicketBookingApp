package com.example.cinematicketbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class AddAuditorium extends AppCompatActivity {
    SQLiteDatabase db;
    String usern;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_auditorium);
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
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

        NV.getMenu().getItem(2).setChecked(true);
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
                    case R.id.nav_add_movie:
                        Intent intent2 = null;
                        intent2 = new Intent(getApplicationContext(), AddMovie.class);
                        intent2.putExtra("username", usern);
                        startActivity(intent2);
                        break;
                    case R.id.nav_home:
                        Intent intent1 = null;
                        intent1 = new Intent(getApplicationContext(), AdminHome.class);
                        intent1.putExtra("username", usern);
                        startActivity(intent1);
                        break;
                    case R.id.nav_add_screening:
                        Intent intent3 = null;
                        intent3 = new Intent(getApplicationContext(), AddScreening.class);
                        intent3.putExtra("username", usern);
                        startActivity(intent3);
                        break;
                    case R.id.nav_add_auditorium:
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

    public void AddAuditoriumFunction(View view) {
        TextView aname = findViewById(R.id.auditoriumname);
        TextView acapacity = findViewById(R.id.auditoriumcapacity);

        if(aname.getText().toString().isEmpty() || acapacity.getText().toString().isEmpty()) {
            Toast.makeText(this,"Please input all fields",Toast.LENGTH_LONG).show();
        } else {
            db.execSQL("INSERT INTO auditorium(name, SeatingCapacity) VALUES('" + aname.getText().toString() + "','" + Integer.parseInt(acapacity.getText().toString()) + "');");
            Toast.makeText(this,"Auditorium was added successfully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), AdminHome.class);
            intent.putExtra("username", usern);
            startActivity(intent);
        }



    }
}
