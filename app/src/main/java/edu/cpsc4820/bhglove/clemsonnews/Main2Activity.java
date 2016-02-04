package edu.cpsc4820.bhglove.clemsonnews;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private ListView mListView;
    private List mListSelected;
    private List mListAvailable;
    private ArrayAdapter<String> mAdapter;
    private TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mListView = (ListView) findViewById(R.id.categoryListView);
        mEmptyText = (TextView) findViewById(R.id.textView);

        mEmptyText.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
        mListAvailable = new ArrayList<String>();

        Collections.addAll(mListAvailable, ClemsonRSSCategories.values());
        mListSelected = new ArrayList<String>();


        mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mListSelected){

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

        if(mListSelected.isEmpty()){
            mEmptyText.setText("List is empty");
            mEmptyText.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyText.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }
        mListView.setAdapter(mAdapter);


    }
}
