package edu.cpsc4820.bhglove.simplenewsreader;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Displays the description and title of all articles in a ListView
 *
 */
public class NewsFeed extends AppCompatActivity {
    private ListView mListView;
    private DataModel mData = null;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        if(mData == null)
          mData = DataModel.getInstance(getApplicationContext());

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
                } else {
                    infoBuilder.setMessage("Please refer to" +
                                    "http://people.cs.clemson.edu/~bhglove/CPSC482/Assignment/assingment2.html."
                                    + "Sorry about the inconvience."
                    );
                }

                infoBuilder.create().show();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        //Handler to offset the download of RSS Content to another thread.
        handler = new Handler();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                createListView();
            }
        };
        //Allows the progress bar to be shown
        handler.postDelayed(run, 1000);
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
        Log.d("NewsFeed", "Creating List View");
        mListView = (ListView) findViewById(R.id.newsFeedView);
        ArrayAdapter<String> adapter = mData.createNewsFeedAdapter(getApplicationContext());
        mListView.setAdapter(adapter);
        TextView empty = (TextView) findViewById(R.id.emptyNewsFeed);

        //Display a list of articles or a message identifying that there are no articles to show.
        if(adapter.isEmpty()){
            mListView.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);

        }
        else{
            mListView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.INVISIBLE);
        }
        progressBar.setVisibility(View.GONE);
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

