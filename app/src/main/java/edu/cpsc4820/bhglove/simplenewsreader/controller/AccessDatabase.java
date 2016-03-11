package edu.cpsc4820.bhglove.simplenewsreader.controller;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import edu.cpsc4820.bhglove.simplenewsreader.R;

/**
 * Created by Benjamin Glover on 3/7/2016.
 *
 * Resources:
 * POST HttpUrlConnection
 * http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
 */
public class AccessDatabase {
    private static AccessDatabase access = null;
    private static final String dbUrl =
            "http://people.cs.clemson.edu/~bhglove/CPSC482/Assignments/Assignment4Portal/";
    private ArrayList<String> favHeadlines; //The title of the articles
    private ArrayList<String> favLinks;     //The weblink of the article
    private ArrayList<String> favDescription; //A description of the article (Contains HTML data)
    private ArrayList<String> favImages;
    DatabaseController mData;
    private  AccessDatabase(Context context){
        mData = mData.getInstance(context);
    }

    public static AccessDatabase getInstance(Context context){
        if(access == null) access = new AccessDatabase(context);
        return access;
    }
    /**
     * Mediator function that returns the Titles for all articles
     * @return ArrayList
     */
    public ArrayList<String> getFavoriteHeadlines() {
        //TODO
        //favHeadlines = db.getFavoriteHeadlines();
        return favHeadlines;
    }

    /**
     * Mediator function that returns all Links for all articles
     * @return ArrayList
     */
    public ArrayList<String> getFavoriteLinks() {
        //TODO
        //favLinks = db.getFavoriteLinks();
        return favLinks;
    }

    /**
     * Mediator function that returns all Descriptions for all articles
     * @return ArryList
     */
    public ArrayList<String> getFavoriteDescriptions() {
        //TODO
        //favDescription = db.getFavoriteDescriptions();
        return favDescription;
    }

    /**
     * Returns the image urls from the database.
     * @return ArrayList Images stored in database.
     */
    public ArrayList<String> getFavoriteImages(){
        //TODO
        // favImages = db.getFavoriteImages();
        return favImages;
    }
    
    public void refreshFavoriteContent(){
        //TODO Set arraylists
    }

    public String getContentDate(String link){
        return null;
    }

    public String getContentTitle(String link){
        return null;
    }

    private String getFirstName(String email){
        String retVal = " ";
        try {
            String variables = "email=" + email;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "getFromEmail.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            Log.d("Name", "Result: " + builder.toString());
            retVal = jObject.getString("fname");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }

    private String getLastName(String email){
        String retVal = " ";
        try {
            String variables = "email=" + email;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "getFromEmail.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            Log.d("Name", "Result: " + builder.toString());
            retVal = jObject.getString("lname");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }


    public int executeRegisterUser(String email, String password, String fname, String lname){
        int retVal = 0;
        try {
            String variables = "email=" + email + "&password=" + password + "&fname=" + fname +
                    "&lname=" +lname;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "register.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            Log.d("Register", "Result: " + builder.toString());
            retVal = jObject.getInt("result");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }
    /**
     * Connects to an authentication script via method post
     * @param email
     * @param password
     * @return 1 - for successful 0 - for wrong password -1 - for the account does not exist.
     */
    public int executeLogin(String email, String password){
        int retVal = 0;
        try {
            String variables = "email=" + email + "&password=" + password;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "login.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            retVal = jObject.getInt("result");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }



    public ArrayAdapter createFavoritesAdapter(final Context context) {
        ArrayAdapter mArrayAdapter;
        /**
         * Split the articles into pages using the SQL Statement
         */
        mArrayAdapter = new ArrayAdapter<String>(context, R.layout.article,
                access.getFavoriteHeadlines()) {

            @Override
            public View getView(final int position, View convertView,
                                ViewGroup parent) {
                if(convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.article, parent, false);
                //Handler handler = new Handler();
                final ImageView imageView = (ImageView) convertView.findViewById(R.id.article_imgview);

                TextView headlineTxtView = (TextView) convertView.findViewById(R.id.headline);
                TextView descriptionTxtView = (TextView) convertView.findViewById(R.id.description);
                TextView pubDateTxtView = (TextView) convertView.findViewById(R.id.pubDate);
                TextView rssTitleTxtView = (TextView) convertView.findViewById(R.id.rssTitle);

                /*YOUR CHOICE OF COLOR*/
                headlineTxtView.setTextColor(Color.BLUE);
                headlineTxtView.setText(access.getFavoriteHeadlines().get(position));


                String description = Html.fromHtml(access.getFavoriteDescriptions().get(position).replaceAll("(<(/)img>)|(<img.+?>)", "")).toString().trim();
                descriptionTxtView.setText(description);
                descriptionTxtView.setTextColor(Color.GRAY);

                pubDateTxtView.setText(getContentDate(access.getFavoriteLinks().get(position)));
                pubDateTxtView.setTextColor(Color.GRAY);

                rssTitleTxtView.setTextColor(Color.GRAY);
                rssTitleTxtView.setText(getContentTitle(access.getFavoriteLinks().get(position)));
                try {
                    String image = access.getFavoriteImages().get(position);
                    if (image == null) {
                        image = "www.example.com";
                    }
                    final String imageUrl = image;

                    if(image.contains("www.example.com")) {
                        imageView.setImageResource(R.drawable.rss);
                        Log.d("Image", "Set stock image ");
                    }
                    else{
                        mData.loadBitmap(imageUrl, imageView);
                    }
                }catch (IndexOutOfBoundsException e){
                    Log.d("Bounds", access.getFavoriteHeadlines().size() + " " + access.getFavoriteImages().size());
                }
                return convertView;
            }
        };
        return mArrayAdapter;
    }
}
