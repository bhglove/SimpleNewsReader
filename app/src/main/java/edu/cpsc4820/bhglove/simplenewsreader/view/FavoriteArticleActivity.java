package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.cpsc4820.bhglove.simplenewsreader.R;
import edu.cpsc4820.bhglove.simplenewsreader.controller.AccessDatabase;
import edu.cpsc4820.bhglove.simplenewsreader.controller.DatabaseController;

public class FavoriteArticleActivity extends AppCompatActivity {
    private ListView mListView;
    private AccessDatabase mData = null;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_article);
        mListView = (ListView) findViewById(R.id.favoriteListView);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar3);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(100);

        if (mData == null)
            mData = AccessDatabase.getInstance(getApplicationContext());

        //Handler to offset the download of RSS Content to another thread.
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView downloading = (TextView) findViewById(R.id.downloadingText2);
                downloading.setVisibility(View.VISIBLE);
                try {
                    mData.refreshFavoriteContent();
                    while (mData.getProgress() < 98) {
                        Thread.sleep(200);
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloading.setVisibility(View.INVISIBLE);
                        createListView();
                    }
                });
            }
        });
        thread.start();
    }

    /**
     * Populates a list view with the provided adapter from DatabaseController
     */
    private void createListView() {
        Log.d("Favorites", "Creating List View");
        mListView = (ListView) findViewById(R.id.favoriteListView);

        ArrayAdapter<String> adapter = mData.createFavoritesAdapter(getApplicationContext());
        mListView.setAdapter(adapter);
        TextView empty = (TextView) findViewById(R.id.emptyFavoritesFeed);


        //Display a list of articles or a message identifying that there are no articles to show.
        if(adapter.isEmpty()){
            mListView.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
        }
        else{
            mListView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.INVISIBLE);
        }

        mProgressBar.setVisibility(View.GONE);
        /**
         * Allows the article to be viewed in one activity.
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoriteArticleActivity.this, ArticleActivity.class);
                intent.putExtra("Title", mData.getFavoriteHeadlines().get(position).toString());
                intent.putExtra("Link", mData.getFavoriteLinks().get(position).toString());
                startActivity(intent);
            }
        });
    }
}
