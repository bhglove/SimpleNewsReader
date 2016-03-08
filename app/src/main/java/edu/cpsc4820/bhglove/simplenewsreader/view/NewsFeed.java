package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.cpsc4820.bhglove.simplenewsreader.R;
import edu.cpsc4820.bhglove.simplenewsreader.controller.DatabaseController;

/**
 * The main activity that displays the a list of articles along with descriptive attributes.
 * Displays the description and title of all articles in a ListView
 * Created by Benjamin Glover 2/27/2016
 *
 * New resources 3/6/2016
 * Drawer Layout:
 * #How To: http://developer.android.com/intl/zh-tw/training/implementing-navigation/nav-drawer.html#DrawerLayout
 * #On top of Action Bar: http://stackoverflow.com/questions/23294954/android-navigation-drawer-on-top-actionbar
 *
 * Tool Bar:
 * #How To: https://www.codeofaninja.com/2014/02/android-navigation-drawer-example.html
 * #Fix Null Error: http://stackoverflow.com/questions/27469219/toolbar-findviewbyid-returning-null
 *
 * */
public class NewsFeed extends AppCompatActivity {
    private ListView mListView;
    private DatabaseController mData = null;
    private ProgressBar mProgressBar;
    private String[] mNavigationDrawerItemsTitles = {"News Feed", "Manage Subscriptions" ,
            "Currently Reading", "My Favorites", "Settings", "Log Out"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        //TODO: Maybe move the drawer and toolbar into a different class for use everywhere
        //TODO: Maybe add info to drawer
        //Toolbar at the top. Allows the navigation bar to overlap it and gives the ability to add actions
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)
                findViewById(R.id.toolbar);
        //Quick fix to remove the app name from toolbar
        toolbar.setTitle(" ");
        //Replace the default Action bar with a created Toolbar
        setSupportActionBar(toolbar);
        //Create the navigation bar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        //Array adapter for the navigation bar
        ArrayAdapter<String> drawerAdapter = new ArrayAdapter<String>(getApplicationContext(),
               R.layout.navigation_item, mNavigationDrawerItemsTitles){
            @Override
            public View getView(final int position, View convertView, ViewGroup parent){
                if(convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.navigation_item,
                            parent, false);
                TextView title = (TextView) convertView.findViewById(R.id.navTitle);
                title.setText(mNavigationDrawerItemsTitles[position]);
                title.setTextColor(Color.WHITE);
                return convertView;
            }
        };
        mDrawerList.setAdapter(drawerAdapter);
        /**
         * Implements functionality for the navigation bar.
         * 1: News Feed: Closes the navigation bar.
         * 2: Manage Subscriptions: Activates the subscription activity
         * 3: Currently Reading: TODO: Make a tab view of articles
         * 4: My Favorites: TODO: Show favorites
         * 5: Settings: TODO: Show settings
         * 6: Log Out: TODO: Log out
         */
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    onBackPressed();
                }
                if(position == 1){
                    Intent intent = new Intent(NewsFeed.this, Subscription.class);
                    startActivity(intent);
                }
                if(position == 5){
                    Intent intent = new Intent(NewsFeed.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(100);

        if (mData == null)
            mData = DatabaseController.getInstance(getApplicationContext());

        //Takes the user to the info activity
        ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsFeed.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        //Opens the navigation bar via button.
        ImageButton menuButton = (ImageButton) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //Handler to offset the download of RSS Content to another thread.
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView downloading = (TextView) findViewById(R.id.downloadingText);
                downloading.setVisibility(View.VISIBLE);
                try {
                    mData.refreshDataContent();
                    while (mData.getProgress() < 98) {
                        Thread.sleep(500);
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
     * Overrides the back button to set NewsFeed as the new Main Screen
     * Prompts the user that the content is still downloading.
     */
    @Override
    public void onBackPressed() {
        //Closes the Navigation drawer if it is open
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        //Promts the user that the content is still downloading
        else if(mData.getProgress() < 98){
            Toast.makeText(getApplicationContext(), "Your articles are currently downloading.",
                    Toast.LENGTH_SHORT).show();
        }

        else { //Returns the user to the home menu and clears the stack of old activities
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * Populates a list view with the provided adapter from DatabaseController
     */
    private void createListView() {
        Log.d("NewsFeed", "Creating List View");
        mListView = (ListView) findViewById(R.id.newsFeedView);
        Button categoryButton = (Button) findViewById(R.id.buttonAddCat);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsFeed.this, Subscription.class);
                startActivity(intent);
            }
        });
        ArrayAdapter<String> adapter = mData.createNewsFeedAdapter(getApplicationContext());
        mListView.setAdapter(adapter);
        TextView empty = (TextView) findViewById(R.id.emptyNewsFeed);


        //Display a list of articles or a message identifying that there are no articles to show.
        if(adapter.isEmpty()){
            mListView.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
            categoryButton.setVisibility(View.VISIBLE);
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
                Intent intent = new Intent(NewsFeed.this, ArticleActivity.class);
                intent.putExtra("Link", mData.getLinks().get(position).toString());
                startActivity(intent);
            }
        });
        /**
         * TODO: Add web page into a tab view
         * TODO: Mark as favorite
         */
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(NewsFeed.this);
                dialog.setTitle("Options");
                dialog.setItems(new String[]{"Open in New Tab", "Mark as Favorite"},
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return true;
            }
        });
    }
}

