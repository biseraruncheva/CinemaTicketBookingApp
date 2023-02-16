package com.example.cinematicketbookingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase db;
    EditText usern;
    EditText passw;
    int Uid;
    ArrayList<Integer> sids = new ArrayList<>();
    ArrayList<String> movies = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    long end_millis;
    long total_millis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);

    }

    public void ClLogin(View view) {
        usern = findViewById(R.id.etusername);
        passw = findViewById(R.id.etpassword);
        String s1=usern.getText().toString();
        String s2=passw.getText().toString();

        Cursor c1 = db.rawQuery("SELECT * FROM user WHERE  username = '" + usern.getText().toString() + "' AND password = '"+passw.getText().toString()+"'", null);
        Cursor c2 = db.rawQuery("SELECT * FROM admin WHERE  username = '" + usern.getText().toString() + "' AND password = '"+passw.getText().toString()+"'", null);

        if (c1.moveToFirst()) {
            Intent intent = null;
            intent = new Intent(this, UserHome.class);
            intent.putExtra("username", usern.getText().toString());
            intent.putExtra("userid", c1.getString(3));
            intent.putExtra("fullname", c1.getString(2));
            Uid = c1.getInt(3);
            c1.close();
            c2.close();
            addNotification();
            startActivity(intent);
        }
        else if (c2.moveToFirst()) {
            Intent intent = null;
            intent = new Intent(this, AdminHome.class);
            intent.putExtra("username", usern.getText().toString());
            c2.close();
            c1.close();
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Invalid username or password",Toast.LENGTH_LONG).show();
        }
    }

    public void addNotification() {

        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            mChannel.setDescription(Description);

            manager.createNotificationChannel(mChannel);
        }

        //SharedPreferences prefs = getSharedPreferences("myprefs", MODE_PRIVATE);
        Cursor c = db.rawQuery("SELECT * FROM allnotifications, screening, movie, user WHERE user.uid = '"+Uid+"' AND allnotifications.sid = screening.sid AND screening.mid = movie.mid",null);
        int rows=c.getCount();
        int i=0;
        if(c.moveToFirst()){
            do{
                sids.add(c.getInt(4));
                long releaseDateTimestamp = c.getLong(8);

// Convert the timestamp to a Date object
                Date releaseDate = new Date(releaseDateTimestamp);

// Convert the Date object to a String in the format "dd-MM-yyyy"
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String releaseDateString = dateFormat.format(releaseDate);
                dates.add(releaseDateString);
                movies.add(c.getString(12));
                i++;
                c.moveToPosition(i);

            }while(i<rows);
            c.close();

            int k;
            for(k=0; k<sids.size(); k++) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date d = null;
                try {
                    d = dateFormat.parse(dates.get(k));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                end_millis = d.getTime();
                total_millis = end_millis - System.currentTimeMillis();

                if (total_millis < 0) {
                    total_millis = 0;
                    db = openOrCreateDatabase("cinemaapp", MODE_PRIVATE, null);
                    //Cursor c3 = db.rawQuery("UPDATE poll SET status = 'Finished' WHERE pid = '" + Integer.valueOf(pids.get(k)) + "'", null);
                    //c3.moveToFirst();
                    //c3.close();
                    Cursor c3 = db.rawQuery("SELECT uid, username FROM user WHERE uid = '"+Uid+"'", null);
                    c3.moveToFirst();

                    String message = "Hi "+c3.getString(1)+". The screening for movie "+movies.get(k)+" has started. You cant book tickets anymore";
                    //i++;
                    Cursor c4 = db.rawQuery("UPDATE allnotifications SET context = '"+message+"', notiftype='End' WHERE uid ='"+Uid+"' AND sid='"+sids.get(k)+"'",null);
                    c4.moveToFirst();
                    c4.close();

                }

            }


        }
        c = db.rawQuery("SELECT * FROM allnotifications WHERE uid = '"+Uid+"' AND (notiftype='Start' OR notiftype='End')",null);
        rows=c.getCount();
        i=0;
        if(c.moveToFirst()){
            do{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                builder.setContentTitle("SCREENING INFO");
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(c.getString(2)));
                //builder.setContentText(c.getString(2));
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                Notification notification = builder.build();
                managerCompat.notify(i, notification);

                i++;
                c.moveToPosition(i);
            }while(i<rows);
            c.close();
            Cursor cc = db.rawQuery("UPDATE allnotifications SET notiftype='notEnd' WHERE uid = '"+Uid+"' AND notiftype='Start'", null);
            cc.moveToFirst();
            cc.close();
            cc = db.rawQuery("DELETE FROM allnotifications WHERE uid = '"+Uid+"' AND notiftype='End'", null);
            cc.moveToFirst();
            cc.close();

        }


    }
}