package edu.cpsc4820.bhglove.simplenewsreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView awk_links = (TextView) findViewById(R.id.acknowledgement_links);
        awk_links.setLinksClickable(true);
        awk_links.setClickable(true);
    }
}
