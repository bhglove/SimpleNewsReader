package edu.cpsc4820.bhglove.clemsonnews;

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

public enum ClemsonRSSCategories {
    MAIN, UNIVERSITY, ACADEMICS, RESEARCH, PEOPLE, SERVICE, EVENTS, STUDENTS, FACULTY, ALUMNI, CES;

  static String[] allFeeds(){
      String[] feeds = new String[ClemsonRSSCategories.values().length];
      int i = 0;
      for(ClemsonRSSCategories cat : ClemsonRSSCategories.values()){
          feeds[i] = cat.toFeed();
          i++;
      }
      return feeds;
  }

  public String toFeed(){
      switch (this){
          case MAIN:
              return "http://newsstand.clemson.edu/feed/";
          case UNIVERSITY:
            return "http://www.clemson.edu/media-relations/rss.php?cat_id=2";
          case ACADEMICS:
            return "http://www.clemson.edu/media-relations/rss.php?cat_id=3";
          case RESEARCH:
              return "http://www.clemson.edu/media-relations/rss.php?cat_id=6";
          case PEOPLE:
              return "http://www.clemson.edu/media-relations/rss.php?cat_id=5";
          case SERVICE:
              return "http://www.clemson.edu/media-relations/rss.php?cat_id=7";
          case EVENTS:
              return "http://www.clemson.edu/media-relations/rss.php?cat_id=4";
          case STUDENTS:
              return "http://www.clemson.edu/media-relations/rss.php?tag=students";
          case FACULTY:
              return "http://www.clemson.edu/media-relations/rss.php?tag=faculty-staff";
          case ALUMNI:
              return "http://www.clemson.edu/media-relations/rss.php?tag=alumni";
          case CES:
            return "http://www.clemson.edu/media-relations/rss.php?tag=CES";
          default:
            return null;
    }
  }
}