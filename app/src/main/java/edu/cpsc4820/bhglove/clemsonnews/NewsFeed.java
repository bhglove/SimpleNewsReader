package edu.cpsc4820.bhglove.clemsonnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class NewsFeed extends AppCompatActivity {
    private ListView mListView;

    //TODO Use data model to store headlines, links, and descriptions
    private DataModel mData = DataModel.getInstance();

    //TODO Add button to start CategoryActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Button categoryButton = (Button) findViewById(R.id.buttonAddCat);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsFeed.this, SelectCategory.class);
                startActivity(intent);
            }
        });
        createListView();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void createListView() {
        mListView = (ListView) findViewById(R.id.newsFeedView);
        ArrayAdapter<String> adapter = mData.createNewsFeedAdapter(getApplicationContext());
        mListView.setAdapter(adapter);
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

