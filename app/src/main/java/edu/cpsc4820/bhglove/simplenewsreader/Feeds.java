package edu.cpsc4820.bhglove.simplenewsreader;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a list of RSS Feeds and binds the title of the feed with an associate link.
 * Created by Benjamin Glover on 2/6/2016.
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
             addFeed(cat.toString(), cat.toFeed());
         }
    }

    /**
     * Creates a new RSS feed using the class RSSFeed
     * @param title
     * @param link
     */
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

    /**
     * Returns the title of the RSS Feed based on it's position in the list.
     * @param position
     * @return String
     */
    public String getTitleAt(int position){
        String title = list.get(position).getTitle();
        Log.d("Feeds", "Retrieving " + title);
        return list.get(position).getTitle();
    }

    /**
     * Returns the associated link address based on position in the list.
     * @param position
     * @return String
     */
    public String getLinkAt(int position){
        String link = list.get(position).getLink();
        Log.d("Feeds", "Retrieving " + link);
        return list.get(position).getLink();
    }

    /**
     * Returns the associated link address based on the title of the RSS Feed.
     * @param title
     * @return String
     */
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

    /**
     * Updates the list, by removing the old Feed and adding a newly created RSS Feed.
     * @param oldTitle
     * @param title
     * @param link
     */
    public void editFeed(String oldTitle, String title, String link){
        RssFeed oldFeed = getRssFeed(oldTitle);
        list.remove(oldFeed);
        addFeed(title, link);
    }

    /**
     * Returns the names of all RSS Feeds in the list.
     * @return String[]
     */
    public String[] getAllTitles(){
        String[] all = new String[list.size()];

        for(int i = 0; i < list.size(); i++){
            all[i] = getTitleAt(i);
        }
        return  all;
    }

    /**
     * Returns all link addresses in the list.
     * @return String[]
     */
    public String[] getAllLinks(){
        String[] all = new String[list.size()];

        for(int i = 0; i < list.size(); i++){
            all[i] = getLinkAt(i);
        }
        return  all;
    }

}