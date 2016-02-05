package edu.cpsc4820.bhglove.clemsonnews;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Benjios on 2/4/2016.
 */
public class DataModel {
    private ArrayList<String> headlines;
    private ArrayList<String> links;
    private ArrayList<String> description;

    private ArrayList mListSelected;   //This is the selected feeds the user wants to display on the news feed
    private ArrayList mListAvailable;

    private static DataModel mData = null;

   private DataModel(){
       headlines = new ArrayList<String>();
       links = new ArrayList<String>();
       description = new ArrayList<String>();
       mListAvailable = new ArrayList<String>();
       mListSelected = new ArrayList<String>();
       for(ClemsonRSSCategories cat : ClemsonRSSCategories.values())
           mListAvailable.add(cat.toString());
   }

    public static DataModel getInstance(){
        if(mData == null) mData = new DataModel();

        return mData;
    }

    public void addToSelectedFeed(String value){
        Log.d("Add", "Add to Selected" + value);
        mListSelected.add(value);
        mListAvailable.remove(value);
    }

    public void addToAvailableFeed(String value){
        Log.d("Add", "Add to Available" + value);
        mListAvailable.add(value);
        mListSelected.remove(value);
    }

    public ArrayList getmListAvailable() {
        return mListAvailable;
    }

    public ArrayList getmListSelected() {
        return mListSelected;
    }

    public String[] getAllSelectedFeed(){
        String [] feed = new String[mListSelected.size()];
        for(int i = 0; i < mListSelected.size(); i++)
            feed[i] = ClemsonRSSCategories.valueOf(mListSelected.get(i).toString()).toFeed();
        return feed;
    }
}
