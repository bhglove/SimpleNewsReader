package edu.cpsc4820.bhglove.simplenewsreader;

/**
 * Maps the rss feed of a news category to a logical name.
 *
 * Resources:
 * Clemson RSS Feeds
 * http://newsstand.clemson.edu/mediarelations/clemson-scientists-research-on-tropical-forests-featured-in-nature/
 *
 * Enum Refresher
 * http://examples.javacodegeeks.com/java-basics/java-enumeration-example/
 *
 * Created by Benjamin Glover on 2/3/2016.
 */

public enum PopularFeeds {
    Clemson, CNN, IEEE, NYTimes, ABC;

  static String[] allFeeds(){
      String[] feeds = new String[PopularFeeds.values().length];
      int i = 0;
      for (PopularFeeds cat : PopularFeeds.values()) {
          feeds[i] = cat.toFeed();
          i++;
      }
      return feeds;
  }

  public String toFeed(){
      switch (this){
          case Clemson:
              return "http://newsstand.clemson.edu/feed/";
          case CNN:
              return "http://rss.cnn.com/rss/cnn_topstories.rss";
          case IEEE:
              return "http://feeds.feedburner.com/IeeeSpectrumFullText?format=xml";
          case NYTimes:
              return "http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml";
          case ABC:
              return "http://feeds.abcnews.com/abcnews/topstories?format=xml";
          default:
            return null;
    }
  }
}