package edu.cpsc4820.bhglove.simplenewsreader;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjios on 2/6/2016.
 */
public class Feeds {
    private class RssFeed{
    private String title;
    private String link;

    public RssFeed() {};

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    }

    private List<RssFeed> list = new ArrayList<>();

    public Feeds() {
         for(PopularFeeds cat : PopularFeeds.values()){
             addFeed(cat.toReadableString(), cat.toFeed());
         }
    }

    public void addFeed(String title, String link){
        RssFeed feed = new RssFeed();

        feed.setTitle(title);
        feed.setLink(link);

        list.add(feed);
        Log.d("Feeds", "Adding " + title + " and " + link + " to the list.");
    }

    private RssFeed getRssFeed(String title){
        RssFeed retVal = null;

        for(int i = 0; i < list.size(); i++){
            if(title == list.get(i).getTitle())
                retVal = list.get(i);
        }

        return retVal;
    }

    public String getTitleAt(int position){
        String title = list.get(position).getTitle();
        Log.d("Feeds", "Retrieving " + title);
        return list.get(position).getTitle();
    }

    public String getLinkAt(int position){
        String link = list.get(position).getLink();
        Log.d("Feeds", "Retrieving " + link);
        return list.get(position).getLink();
    }

    public String findLink(String title){
        String link = null;

        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getTitle() == title){
                link = list.get(i).getLink();
                break;
            }
        }
        return link;
    }

    public void editFeed(String oldTitle, String title, String link){
        RssFeed oldFeed = getRssFeed(oldTitle);
        list.remove(oldFeed);
        addFeed(title, link);
    }

    public String[] getAllTitles(){
        String[] all = new String[list.size()];

        for(int i = 0; i < list.size(); i++){
            all[i] = getTitleAt(i);
        }
        return  all;
    }

    public String[] getAllLinks(){
        String[] all = new String[list.size()];

        for(int i = 0; i < list.size(); i++){
            all[i] = getLinkAt(i);
        }
        return  all;
    }

}