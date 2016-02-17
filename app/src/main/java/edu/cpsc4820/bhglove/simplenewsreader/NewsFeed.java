package edu.cpsc4820.bhglove.simplenewsreader;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays the description and title of all articles in a ListView
 *
 */
public class NewsFeed extends AppCompatActivity {
    private ListView mListView;


    private DataModel mData = DataModel.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Button categoryButton = (Button) findViewById(R.id.buttonAddCat);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsFeed.this, Subscription.class);
                startActivity(intent);
            }
        });
        ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder infoBuilder = new AlertDialog.Builder(NewsFeed.this);
                infoBuilder.setTitle("About Simple News Reader");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    infoBuilder.setView(R.layout.about_layout);
                }
                else{
                    infoBuilder.setMessage("Please refer to" +
                            "http://people.cs.clemson.edu/~bhglove/CPSC482/Assignment/assingment2.html."
                           + "Sorry about the inconvience."
                    ); }

                infoBuilder.create().show();
            }
        });
        createListView();
    }

    // Overrides the back button to set NewsFeed as the new Main Screen
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Populates a list view with the provided adapter from DataModel
     */
    private void createListView() {
        mListView = (ListView) findViewById(R.id.newsFeedView);
        ArrayAdapter<String> adapter = mData.createNewsFeedAdapter(getApplicationContext());
        mListView.setAdapter(adapter);
        TextView empty = (TextView) findViewById(R.id.emptyNewsFeed);
        if(adapter.isEmpty()){
            mListView.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
        }
        else{
            mListView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.INVISIBLE);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewsFeed.this, ArticleActivity.class);

                intent.putExtra("Link", mData.getLinks().get(position).toString());

                startActivity(intent);
            }
        });
    }
}

