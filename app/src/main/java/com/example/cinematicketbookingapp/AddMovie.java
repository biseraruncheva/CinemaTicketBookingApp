package com.example.cinematicketbookingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("NotConstructor")
public class AddMovie extends AppCompatActivity {
    //private static final int SELECT_IMAGE = 1;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private byte[] imageByteArray;
    byte[] pom;
    //private ImageView imageView;
    SQLiteDatabase db;
    String usern;
    private Button pickDateBtn;
    private TextView selectedDateTV;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
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

        NV.getMenu().getItem(1).setChecked(true);
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
                                finish();
                                startActivity(getIntent());
                                break;
                            case R.id.nav_home:
                                Intent intent1 = null;
                                intent1 = new Intent(getApplicationContext(), AdminHome.class);
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


    public void PickDate(View view) {
        // on below line we are getting
        // the instance of our calendar.
        pickDateBtn = findViewById(R.id.btndatepicker);
        selectedDateTV = findViewById(R.id.displdate);
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                AddMovie.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.
                        String.format("%02d-%02d-%02d", dayOfMonth,(monthOfYear + 1) , year);
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

    public void AddImage(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // permission already granted, select image
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_IMAGE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            // get the selected image as a Bitmap
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // convert Bitmap to byte[]
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByteArray = stream.toByteArray();

            // display the selected image
           // TextView tv = findViewById(R.id.noimg);
          //  tv.setText(selectedImageUri.toString());
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
           ImageView tv1 = findViewById(R.id.pictest);
           tv1.setImageBitmap(bitmap1);


        }
    }

    public void AddMovieFunction(View view) throws ParseException {
        TextView title = findViewById(R.id.auditoriumname);
        TextView genres = findViewById(R.id.auditoriumcapacity);
        TextView synposis = findViewById(R.id.moviesynopsis);
        TextView release_date = findViewById(R.id.displdate);
        String rd = release_date.getText().toString();
        TextView duration = findViewById(R.id.movieduration);
        if(title.getText().toString().isEmpty() || genres.getText().toString().isEmpty() || synposis.getText().toString().isEmpty() || release_date.getText().toString().equals("No date to display") || duration.getText().toString().isEmpty() || imageByteArray == null) {
            Toast.makeText(this,"Please input all fields",Toast.LENGTH_LONG).show();

        } else {
            SimpleDateFormat dateFormats = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = dateFormat.parse(rd);
            //String formattedDate = dateFormats.format(date);
            //java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            String mtitle = title.getText().toString();
            String mgenres = genres.getText().toString();
            String mduration = duration.getText().toString();
            String msynopsis = synposis.getText().toString();
            // TextView tv = findViewById(R.id.noimg);

            ContentValues values = new ContentValues();
            values.put("title", mtitle);
            values.put("genre", mgenres);
            values.put("duration_in_minutes", Integer.parseInt(mduration));
            //values.put("ReleaseDate", String.valueOf(sqlDate));
            values.put("ReleaseDate", date.getTime());
            values.put("picture", imageByteArray);
            values.put("synopsis", msynopsis);

            long rowId = db.insert("movie", null, values);

            //db.execSQL("INSERT INTO movie(title, genre, rating, duration_in_minutes, ReleaseDate, picture, synopsis) VALUES('" + mtitle + "','" + mgenres + "','"+rating+"','"+Integer.parseInt(mduration)+"','"+sqlDate+"','"+ imageByteArray +"','"+msynopsis +"' );");

            Toast.makeText(this,"Movie was added successfully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), AdminHome.class);
            intent.putExtra("username", usern);
            startActivity(intent);

        }
    }
}

