package edu.cpsc4820.bhglove.clemsonnews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NewsFeed extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<String> headlines;
    private ArrayList<String> links;
    private ArrayList<String> description;
    private ArrayAdapter<String> mArrayAdapter;
    private ParseRSS parse;
    //TODO Use data model to store headlines, links, and descriptions
    private DataModel mData = DataModel.getInstance();

    //TODO Add button to start CategoryActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        parse = new ParseRSS();
        createListView();
    }

    private void createListView(){
        mListView = (ListView) findViewById(R.id.newsFeedView);

        try {
            parse.execute(mData.getAllSelectedFeed());
            parse.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        headlines = parse.getmHeadlines();
        links = parse.getmLinks();
        description = parse.getmDescription();

        //TODO Change Simple List to #2 pass in DataModel
        mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, headlines){

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);


            /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.BLUE);

                return view;
            }
        };
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewsFeed.this, ArticleActivity.class);
                intent.putExtra("Headline", headlines.get(position).toString());
                intent.putExtra("Link", links.get(position).toString());
                intent.putExtra("Description", description.get(position).toString());
                startActivity(intent);

            }
        });
    }

    private class ParseRSS extends AsyncTask<String , Integer, String> {
        private ArrayList<String> mHeadlines;
        private ArrayList<String> mLinks;
        private ArrayList<String> mDescription;
        private ProgressDialog dialog;

        public ParseRSS(){
            mHeadlines = (ArrayList) new ArrayList<String>();
            mLinks = (ArrayList) new ArrayList<String>();
            mDescription = (ArrayList) new ArrayList<String>();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Thread", "Background");
            getRSSList(params);

            return "Task Completed.";
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = new ProgressDialog(NewsFeed.this);
            dialog.show();
            dialog.setTitle("Downloading feeds");
            Log.i("Thread", "Pre Execute");
        }
        public ArrayList<String> getmHeadlines(){
            return mHeadlines;
        }
        public ArrayList<String> getmLinks(){
            return mLinks;
        }

        private boolean getRSSList(String[] feed){
            boolean retVal = false;
            Log.d("Feed", "There are " + feed.length + " selected feeds");
            dialog.setMax(feed.length * 10);
            dialog.show();
            for(int i = 0; i < feed.length; i++) {
                try {
                    URL url = new URL(feed[i]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(100 * 1000);
                    conn.setConnectTimeout(100 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    InputStream inputStream = conn.getInputStream();

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();

                    // We will get the XML from an input stream

                    xpp.setInput(inputStream, "UTF_8");

                    boolean insideItem = false;

                    // Returns the type of current event: START_TAG, END_TAG, etc..
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {

                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;
                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem)
                                    mHeadlines.add(xpp.nextText()); //extract the headline
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem)
                                    mLinks.add(xpp.nextText()); //extract the link of article
                            } else if (xpp.getName().equalsIgnoreCase("description")) {
                                if(insideItem)
                                    mDescription.add(xpp.nextText()); //extract the category
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                        }

                        eventType = xpp.next(); //move to next element
                        retVal = true;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    retVal = false;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    retVal = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    retVal = false;
                }

                Log.d("Progress", "Progress increased " + (i + 1) + " times, by " + 10);
                dialog.incrementProgressBy(10);
            }
            Log.d("Feed", "Finishing feed grab with " + mHeadlines.size() + " headlines");
            return  retVal;
        }

        @Override
        protected void onPostExecute(String params){
            super.onPostExecute(params);
            Log.i("Thread", "Post Execute");
            dialog.dismiss();
        }

        public ArrayList<String> getmDescription() {
            return mDescription;
        }
    }
}

