package edu.cpsc4820.bhglove.clemsonnews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NewsFeed extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<String> headlines;
    private ArrayList<String> links;
    private ArrayList<String> description;
    private ArrayAdapter<String> mArrayAdapter;

    private DataModel mData = DataModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        mListView = (ListView) findViewById(R.id.newsFeedView);

        ParseRSS parse = new ParseRSS();

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
}
