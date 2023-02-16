package com.example.cinematicketbookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user(username VARCHAR UNIQUE, password VARCHAR, FullName VARCHAR ,uid INTEGER PRIMARY KEY);");
        db.execSQL("CREATE TABLE IF NOT EXISTS admin(username VARCHAR UNIQUE, password VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS movie(mid INTEGER PRIMARY KEY , title VARCHAR UNIQUE, genre VARCHAR, duration_in_minutes INTEGER, ReleaseDate DATE, picture BLOB, synopsis TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS auditorium(aid INTEGER PRIMARY KEY, name VARCHAT UNIQUE, SeatingCapacity INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS screening(sid INTEGER PRIMARY KEY, mid INTEGER, aid INTEGER, ScreeningTime DATETIME, AvailableSeats INTEGER, PricePerSeat INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS reservation(rid INTEGER PRIMARY KEY, uid INTEGER, sid INTEGER, NumOfSeats INTEGER, Seats STRING, latitude DOUBLE, longitude DOUBLE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS allnotifications(nid INTEGER PRIMARY KEY , uid INTEGER, context VARCHAR, notiftype VARCHAR, sid INTEGER)");
        String s2 = "biserarunceva";
        String s1 = "biserar";
        Cursor c1 = db.rawQuery("SELECT * FROM user WHERE  username = '"+s1+"'", null);
        Cursor c2 = db.rawQuery("SELECT * FROM admin WHERE  username = '"+s2+"'", null);

        if(c1.getCount()>0 && c2.getCount()>0) {
            c1.close();
            c2.close();
        }
        else {
            db.execSQL("INSERT INTO user (username,password,FUllName) VALUES('biserar', '12345','Bisera Runcheva');");
            db.execSQL("INSERT INTO admin (username,password) VALUES('biserarunceva', '12345');");
            c1.close();
            c2.close();
        }

    }
    public void ClickLogin(View view) {
        Intent intent = null;
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    public void ClickRegister(View view) {
        Intent intent = null;
        intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

}