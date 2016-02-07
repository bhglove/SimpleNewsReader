package edu.cpsc4820.bhglove.clemsonnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
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
 */
public class MainActivity extends AppCompatActivity{

    //TODO change the UI of the main activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Check for Internet permissions for 6.0
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button newsFeedButton = (Button) findViewById(R.id.buttonNewsFeed);
        newsFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewsFeed.class);
                startActivity(intent);
            }
        });

        //TODO You're going to want this to be empty for adding permissions in 6.0

        //TODO Check to make sure phone has internet
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
