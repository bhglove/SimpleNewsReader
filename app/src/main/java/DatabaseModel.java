/**
 * Created by Benjios on 2/17/2016.
 */
public class DatabaseModel{
    private static final String DATABASE_NAME = "RSSFEEDS";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RSS = "RSS";
    private static final String KEY_RSS_ID = "_ID";
    private static final String KEY_RSS_TITLE = "TITLE";
    private static final String KEY_RSS_LINK = "LINK";
    private static final String KEY_RSS_AVAILABLE = "AVAILABLE";

    private static final String TABLE_ARTICLES = "ARTICLES";
    private static final String KEY_ARTICLES_ID = "_ID";
    private static final String KEY_ARTICLES_RSS = "RSS_ID";
    private static final String KEY_ARTICLES_CONTENT = "CONTENT_ID";

    private static final String TABLE_CONTENT = "CONTENT";




}
