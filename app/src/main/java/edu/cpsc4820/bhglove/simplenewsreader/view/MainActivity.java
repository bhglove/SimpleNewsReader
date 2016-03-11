package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import edu.cpsc4820.bhglove.simplenewsreader.R;

/**
 * Prompts the user for internet permissions, automatically moves to either the login screen or
 * the main screen. Checks whether the user is currently logged in, if so it shifts to the
 * news feed, else it prompts the user  to login.
 * Created by Benjamin Glover 02/03/2016
 *
 * Resources:
 *
 * Quickly override listview font
 * http://stackoverflow.com/questions/4533440/android-listview-text-color
 *
 * Override Back Button
 *  - Our TA Sean
 * http://stackoverflow.com/questions/2354336/android-pressing-back-button-should-exit-the-app
 *
 * #3/11/2016
 * Shared Preferences
 * https://androidresearch.wordpress.com/2012/03/31/writing-and-reading-from-sharedpreferences/
 */
public class MainActivity extends AppCompatActivity{
    public static final String PREFERENCES = "SNR_PREFS";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_FNAME = "fname";
    public static final String KEY_LNAME = "lname";
    SharedPreferences prefs;
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 0;
    private int permissionCheck = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)){

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);
            }
        }
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            if(prefs.getString(KEY_FNAME, null) != null) {
                Intent intent = new Intent(MainActivity.this, NewsFeed.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
        else{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {

                }
                return;
            }

        }
        // permissions this app might request
    }

}
