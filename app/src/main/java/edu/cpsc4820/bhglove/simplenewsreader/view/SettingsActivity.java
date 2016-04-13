package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import edu.cpsc4820.bhglove.simplenewsreader.R;
import edu.cpsc4820.bhglove.simplenewsreader.controller.DatabaseController;

public class SettingsActivity extends AppCompatActivity {
    public static final String SETTINGS_KEY_TEXT_SIZE = "TEXT_SIZE";
    DatabaseController mData;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mData = DatabaseController.getInstance(getApplicationContext());
        pref = getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);
        Button logout = (Button) findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences
                        (MainActivity.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                mData.clearDatabase();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
