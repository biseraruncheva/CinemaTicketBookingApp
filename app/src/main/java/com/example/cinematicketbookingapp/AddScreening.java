package com.example.cinematicketbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddScreening extends AppCompatActivity {
    SQLiteDatabase db;
    String usern;
    private Button pickDateBtn;
    private TextView selectedDateTV;
    private Button pickTimeBtn;
    private TextView selectedTimeTV;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    private ArrayList<String> MoviesList;
    private ArrayList<String> AuditoriumsList;
    Spinner s1;
    Spinner s2;
    int check = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_screening);
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        usern = intent.getStringExtra("username");
        NavigationView NV = findViewById(R.id.navview);
        View HV = NV.getHeaderView(0);
        TextView tv = (TextView) HV.findViewById(R.id.navname);
        tv.setText(usern);
        TextView tv1 = (TextView) HV.findViewById(R.id.navrole);
        tv1.setText("ADMIN");

        s1 = findViewById(R.id.moviespinner);
        s2 = findViewById(R.id.auditoriumspinner);
        MoviesList = new ArrayList<>();
        AuditoriumsList = new ArrayList<>();

        Cursor c1 = db.rawQuery("SELECT * FROM movie", null);
        int rows = c1.getCount();
        int i=0;
        if(c1.moveToPosition(0)){
            do{
                MoviesList.add(c1.getString(1));
                i++;
                c1.moveToPosition(i);
            }while (i<rows);
            c1.close();
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, MoviesList);
        s1.setAdapter(adapter);
        // on below line we are adding on item selected listener for spinner.
        //spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //   @Override
        //  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // in on selected listener we are displaying a toast message
        //      Toast.makeText(MainActivity.this, "Selected Language is : " + languageList.get(position), Toast.LENGTH_SHORT).show();
    //}
//
    // @Override
    // public void onNothingSelected(AdapterView<?> parent) {

    // }
    //});

        Cursor c2 = db.rawQuery("SELECT * FROM auditorium", null);
        rows = c2.getCount();
        int j=0;
        if(c2.moveToPosition(0)){
            do{
                AuditoriumsList.add(c2.getString(1));
                j++;
                c2.moveToPosition(j);
            }while (j<rows);
            c2.close();
        }

        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, AuditoriumsList);
        s2.setAdapter(adapter1);


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

        NV.getMenu().getItem(3).setChecked(true);
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
                    case R.id.nav_add_auditorium:
                        Intent intent3 = null;
                        intent3 = new Intent(getApplicationContext(), AddAuditorium.class);
                        intent3.putExtra("username", usern);
                        startActivity(intent3);
                        break;
                    case R.id.nav_add_screening:
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

    public void PickScreeningDate(View view) {
        // on below line we are getting
        // the instance of our calendar.
        pickDateBtn = findViewById(R.id.selectdatebtn);
        selectedDateTV = findViewById(R.id.displscreeningdate);
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                AddScreening.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.
                        //selectedDateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        selectedDateTV.setText(String.format("%02d-%02d-%02d", dayOfMonth,(monthOfYear + 1) , year));


                    }
                },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        datePickerDialog.show();
    }

    public void PickScreeningTime(View view) {
        pickTimeBtn = findViewById(R.id.selecttimebtn);
        selectedTimeTV = findViewById(R.id.displscreeningtime);

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddScreening.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        //selectedTimeTV.setText(hourOfDay + ":" +minute);
                        selectedTimeTV.setText(String.format("%02d:%02d", hourOfDay,minute));

                    }
                },
        hour, minute, true);

        timePickerDialog.show();
    }

    public void AddScreeningFunction(View view) throws ParseException {

        check = 1;
        s1 = findViewById(R.id.moviespinner);
        s2 = findViewById(R.id.auditoriumspinner);
        TextView date = findViewById(R.id.displscreeningdate);
        TextView time = findViewById(R.id.displscreeningtime);
        EditText price = findViewById(R.id.priceinput);
        if (date.getText().toString().isEmpty() || time.getText().toString().isEmpty() || price.getText().toString().isEmpty()) {

            Toast.makeText(this,"Please input all fields",Toast.LENGTH_LONG).show();

        } else {
            String movietitle = s1.getSelectedItem().toString();
            String auditoriumname = s2.getSelectedItem().toString();
            //String datetime = date.getText().toString() + " " + time.getText().toString();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            //Date d = dateFormat.parse(datetime);
            //java.util.Date sqlDate = new java.util.Date(d.getTime());
            String datetime = date.getText().toString() + " " + time.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            SimpleDateFormat formatscreeningdate = new SimpleDateFormat("dd-MM-yyyy");
            Date screeningd = formatscreeningdate.parse(date.getText().toString());
            Date d = dateFormat.parse(datetime);
            String formattedDate = dateFormat.format(d);
            Cursor c1 = db.rawQuery("SELECT mid, ReleaseDate, duration_in_minutes FROM movie WHERE title ='" + movietitle + "' ", null);
            int movierows = c1.getCount();
            if (movierows == 1) {
                c1.moveToFirst();
            }
            Cursor c2 = db.rawQuery("SELECT auditorium.aid , auditorium.SeatingCapacity FROM auditorium WHERE name ='" + auditoriumname + "' ", null);
            int auditoriumrows = c2.getCount();
            if (auditoriumrows == 1) {
                c2.moveToFirst();
            }
            SimpleDateFormat formatreleasedate = new SimpleDateFormat("dd-MM-yyyy");
            /*Date releaseddate = null;
            try {
                releaseddate = formatreleasedate.parse(c1.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            Date releaseddate =new Date(c1.getLong(1));
            //Cursor c3 = db.rawQuery("SELECT screening.ScreeningTime, movie.duration_in_minutes FROM movie ,screening, auditorium WHERE screening.aid = auditorium.aid AND auditorium.aid = '" + c2.getInt(0) + "' AND substr(ScreeningTime, 1, 10) = '" + date.getText().toString() + "'", null);
            Cursor c3 = db.rawQuery("SELECT screening.ScreeningTime, movie.duration_in_minutes FROM movie ,screening, auditorium WHERE screening.aid = auditorium.aid AND auditorium.aid = '" + c2.getInt(0) + "' AND strftime('%d-%m-%Y', ScreeningTime / 1000, 'unixepoch') = '" + date.getText().toString() + "'", null);
            int c3rows = c3.getCount();
            int k = 0;
            if (c3.moveToFirst()) {
                do {
                   /* String dtStart = c3.getString(0);
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date start_date = null;
                    try {
                        start_date = format.parse(dtStart);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/
                    Date start_date = new Date(c3.getLong(0));
                    long start_millis = start_date.getTime();
                    long input_start_milis = d.getTime();
                    long input_end_millis = input_start_milis + c1.getInt(2) * 60000; // calculate the end time of the new screening
                    if (input_start_milis < start_millis + (c3.getInt(1) + 30) * 60000 && input_end_millis > start_millis){
                        check = 0;
                        break;
                    }
                    k++;
                    c3.moveToPosition(k);

                } while (k < c3rows);
                c3.close();
            }


            if (screeningd.compareTo(releaseddate) < 0) {
                Toast.makeText(this, "Screening date cant be before Release date", Toast.LENGTH_LONG).show();
            } else {

                if (check == 0) {
                    Toast.makeText(this, "Pick another time, auditorium is not available then", Toast.LENGTH_LONG).show();
                } else {
                    ContentValues columns = new ContentValues();
                    columns.put("mid", c1.getInt(0));
                    columns.put("aid", c2.getInt(0));
                    //columns.put("ScreeningTime", formattedDate);
                    //Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("14-02-2023 15:30");
                    Timestamp timestamp = new Timestamp(d.getTime());
                    columns.put("ScreeningTime", timestamp.getTime());
                    columns.put("AvailableSeats", c2.getInt(1));
                    columns.put("PricePerSeat", Integer.valueOf(price.getText().toString()));
                    int sid = (int) db.insert("screening", null, columns);
                    //db.execSQL("INSERT INTO screening(mid, aid, ScreeningTime, AvailableSeats, PricePerSeat) VALUES('" + c1.getInt(0) + "','" + c2.getInt(0) + "','"+formattedDate+"','"+c2.getInt(1)+"','"+Integer.valueOf(price.getText().toString())+"');");
                    c1.close();
                    c2.close();

                    c2 = db.rawQuery("SELECT uid, username FROM user", null);
                    int rows = c2.getCount();
                    int i = 0;
                    if (c2.moveToFirst()) {
                        do {
                            String message = "Hi " + c2.getString(1) + ". The new screening for movie " + movietitle + " is now available.";
                            db.execSQL("INSERT INTO allnotifications (uid, context, notiftype, sid) VALUES('" + Integer.valueOf(c2.getString(0)) + "','" + message + "','Start','" + sid + "' );");
                            i++;
                            c2.moveToPosition(i);
                        } while (i < rows);
                        c2.close();
                    }
                    Toast.makeText(this,"Screening was added successfully",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                    intent.putExtra("username", usern);
                    startActivity(intent);

                }
            }
        }
    }
}